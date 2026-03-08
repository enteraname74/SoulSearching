package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteAlbumDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiAlbumDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AlbumBottomSheetViewModel(
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: AlbumBottomSheetNavScope,
    settings: SoulSearchingSettings,
    params:  AlbumBottomSheetDestination,
): ViewModel() {
    private val albumIds: List<UUID> = params.albumIds

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    val state: StateFlow<AlbumBottomSheetState> = combine(
        commonAlbumUseCase.getFromIds(albumIds),
        playbackManager.playedList,
        dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        )
    ) { albums, playedList, dialogState, isQuickAccessShown ->
        AlbumBottomSheetState(
            albums = albums,
            bottomSheetTopInformation = buildTopInformation(albums),
            rowSpecs = buildRowSpecs(
                playlists = albums,
                isQuickAccessShown = isQuickAccessShown,
                playedList = playedList,
            ),
            dialogState = dialogState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AlbumBottomSheetState(),
    )

    private fun buildRowSpecs(
        playlists: List<AlbumWithMusics>,
        playedList: List<Music>,
        isQuickAccessShown: Boolean,
    ) : List<BottomSheetRowSpec> = buildList {
        val editEnabled: Boolean = playlists.size == 1

        if (isQuickAccessShown) {
            val isInQuickAccess: Boolean = if (playlists.size == 1) {
                playlists.first().album.isInQuickAccess
            } else {
                playlists.all { it.album.isInQuickAccess }
            }

            add(
                BottomSheetRowSpec.quickAccess(
                    onClick = { handleQuickAccess(!isInQuickAccess) },
                    isInQuickAccess = isInQuickAccess,
                )
            )
        }

        if (editEnabled) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_edit_filled,
                    title = strings.modifyAlbum,
                    onClick = ::toModifyAlbum,
                )
            )
        }

        addAll(
            listOf(
                BottomSheetRowSpec.playNext(::playNext),
                BottomSheetRowSpec.addToQueue(::addToQueue),
            )
        )

        if (playedList.isNotEmpty()) {
            add(
                BottomSheetRowSpec.removeFromPlayedList(::removeFromPlayedList),
            )
        }

        add(
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_delete_filled,
                title = if (playlists.size == 1) {
                    strings.deleteAlbum
                } else {
                    strings.deleteSelectedAlbums
                },
                onClick = ::showDeleteDialog,
            )
        )
    }

    private fun toModifyAlbum() {
        albumIds.firstOrNull()?.let {
            viewModelScope.launch {
                multiSelectionManager.clearMultiSelection()
                navScope.toModifyAlbum(it)
            }
        }
    }

    private fun buildTopInformation(albums: List<AlbumWithMusics>): BottomSheetTopInformation =
        if (albums.size == 1) {
            val album = albums.first()
            BottomSheetTopInformation(
                title = album.album.albumName,
                subTitle = strings.musics(total = album.musics.filter { !it.isHidden }.size),
                cover = album.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = albums.size),
                cover = null,
            )
        }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.albums.size == 1) {
            DeleteAlbumDialog(
                onDelete = ::deleteAlbums,
                onClose = { dialogState.value = null },
            )
        } else {
            DeleteMultiAlbumDialog(
                onDelete = ::deleteAlbums,
                onClose = { dialogState.value = null },
            )
        }
    }

    private fun deleteAlbums() {
        viewModelScope.launch {
            dialogState.value = null
            loadingManager.withLoading {
                deleteAlbumUseCase(state.value.albums.map { it.album.albumId })
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonAlbumUseCase.upsertAll(
                    albums = state.value.albums.map {
                        it.album.copy(
                            isInQuickAccess = newValue,
                        )
                    }
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun playNext() {
        viewModelScope.launch {
            loadingManager.withLoading {
                val musics: List<Music> =
                    state.value.albums
                        .flatMap { it.musics }
                        .distinctBy { it.musicId }

                playbackManager.addMultipleMusicsToPlayNext(
                    musics = musics,
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun addToQueue() {
        viewModelScope.launch {
            loadingManager.withLoading {
                val musics: List<Music> =
                    state.value.albums
                        .flatMap { it.musics }
                        .distinctBy { it.musicId }

                playbackManager.addMultipleMusicsToQueue(
                    musics = musics,
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun removeFromPlayedList() {
        viewModelScope.launch {
            loadingManager.withLoading {
                val musicIds: List<UUID> =
                    state.value.albums
                        .flatMap { it.musics }
                        .distinctBy { it.musicId }
                        .map { it.musicId }

                playbackManager.removeSongsFromPlayedPlaylist(
                    musicIds = musicIds,
                )
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }
}
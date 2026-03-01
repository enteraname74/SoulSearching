package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiPlaylistDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeletePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class PlaylistBottomSheetViewModel(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: PlaylistBottomSheetNavScope,
    settings: SoulSearchingSettings,
    params:  PlaylistBottomSheetDestination,
) : ViewModel() {
    private val playlistIds: List<UUID> = params.playlistIds

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    val state: StateFlow<PlaylistBottomSheetState> = combine(
        commonPlaylistUseCase.getFromIds(playlistIds),
        playbackManager.mainState,
        dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        )
    ) { playlists, playbackState, dialogState, isQuickAccessShown ->
        PlaylistBottomSheetState(
            playlists = playlists,
            bottomSheetTopInformation = buildTopInformation(playlists),
            rowSpecs = buildRowSpecs(
                playlists = playlists,
                isQuickAccessShown = isQuickAccessShown,
                playedList = (playbackState as? PlaybackManagerState.Data)?.playedList
                    ?: emptyList(),
            ),
            dialogState = dialogState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlaylistBottomSheetState(),
    )

    private fun buildRowSpecs(
        playlists: List<PlaylistWithMusics>,
        playedList: List<Music>,
        isQuickAccessShown: Boolean,
    ) : List<BottomSheetRowSpec> = buildList {
        val editEnabled: Boolean = playlists.size == 1
        val showDelete: Boolean = if (playlists.size == 1) {
            !playlists.first().playlist.isFavorite
        } else {
            true
        }

        if (isQuickAccessShown) {
            val isInQuickAccess: Boolean = if (playlists.size == 1) {
                playlists.first().playlist.isInQuickAccess
            } else {
                playlists.all { it.playlist.isInQuickAccess }
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
                    title = strings.modifyPlaylist,
                    onClick = ::toModifyPlaylist,
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

        if (showDelete) {
            add(
                BottomSheetRowSpec(
                    icon = CoreRes.drawable.ic_delete_filled,
                    title = if (playlists.size == 1) {
                        strings.deletePlaylist
                    } else {
                        strings.deleteSelectedPlaylists
                    },
                    onClick = ::showDeleteDialog,
                )
            )
        }
    }

    private fun toModifyPlaylist() {
        playlistIds.firstOrNull()?.let {
            viewModelScope.launch {
                multiSelectionManager.clearMultiSelection()
                navScope.toModifyPlaylist(it)
            }
        }
    }

    private fun buildTopInformation(playlists: List<PlaylistWithMusics>): BottomSheetTopInformation =
        if (playlists.size == 1) {
            val playlist = playlists.first()
            BottomSheetTopInformation(
                title = playlist.playlist.name,
                subTitle = strings.musics(total = playlist.musics.filter { !it.isHidden }.size),
                cover = playlist.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = playlists.size),
                cover = null,
            )
        }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.playlists.size == 1) {
            DeletePlaylistDialog(
                onDelete = ::deletePlaylists,
                onClose = { dialogState.value = null },
            )
        } else {
            DeleteMultiPlaylistDialog(
                onDelete = ::deletePlaylists,
                onClose = { dialogState.value = null },
            )
        }
    }

    private fun deletePlaylists() {
        viewModelScope.launch {
            dialogState.value = null
            loadingManager.withLoading { commonPlaylistUseCase.deleteAll(playlistIds) }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonPlaylistUseCase.upsertAll(
                    playlists = state.value.playlists.map {
                        it.playlist.copy(
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
                    state.value.playlists
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
                    state.value.playlists
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
                    state.value.playlists
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
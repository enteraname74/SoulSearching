package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteArtistDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiArtistDialog
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

class ArtistBottomSheetViewModel(
    private val commonArtistUseCase: CommonArtistUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val playbackManager: PlaybackManager,
    private val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val navScope: ArtistBottomSheetNavScope,
    settings: SoulSearchingSettings,
    params:  ArtistBottomSheetDestination,
) : ViewModel() {
    private val artistIds: List<UUID> = params.artistIds

    private val dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    val state: StateFlow<ArtistBottomSheetState> = combine(
        commonArtistUseCase.getFromIds(artistIds),
        playbackManager.mainState,
        dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        )
    ) { artists, playbackState, dialogState, isQuickAccessShown ->
        ArtistBottomSheetState(
            artists = artists,
            bottomSheetTopInformation = buildTopInformation(artists),
            rowSpecs = buildRowSpecs(
                playlists = artists,
                isQuickAccessShown = isQuickAccessShown,
                playedList = (playbackState as? PlaybackManagerState.Data)?.playedList
                    ?: emptyList(),
            ),
            dialogState = dialogState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ArtistBottomSheetState(),
    )

    private fun buildRowSpecs(
        playlists: List<ArtistWithMusics>,
        playedList: List<Music>,
        isQuickAccessShown: Boolean,
    ) : List<BottomSheetRowSpec> = buildList {
        val editEnabled: Boolean = playlists.size == 1

        if (isQuickAccessShown) {
            val isInQuickAccess: Boolean = if (playlists.size == 1) {
                playlists.first().artist.isInQuickAccess
            } else {
                playlists.all { it.artist.isInQuickAccess }
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
                    title = strings.modifyArtist,
                    onClick = ::toModifyArtist,
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
                    strings.deleteArtist
                } else {
                    strings.deleteSelectedArtists
                },
                onClick = ::showDeleteDialog,
            )
        )
    }

    private fun toModifyArtist() {
        artistIds.firstOrNull()?.let {
            viewModelScope.launch {
                multiSelectionManager.clearMultiSelection()
                navScope.toModifyArtist(it)
            }
        }
    }

    private fun buildTopInformation(artists: List<ArtistWithMusics>): BottomSheetTopInformation =
        if (artists.size == 1) {
            val artist = artists.first()
            BottomSheetTopInformation(
                title = artist.artist.artistName,
                subTitle = strings.musics(total = artist.musics.filter { !it.isHidden }.size),
                cover = artist.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = artists.size),
                cover = null,
            )
        }

    private fun showDeleteDialog() {
        dialogState.value = if (state.value.artists.size == 1) {
            DeleteArtistDialog(
                onDelete = ::deleteArtists,
                onClose = { dialogState.value = null },
            )
        } else {
            DeleteMultiArtistDialog(
                onDelete = ::deleteArtists,
                onClose = { dialogState.value = null },
            )
        }
    }

    private fun deleteArtists() {
        viewModelScope.launch {
            dialogState.value = null
            loadingManager.withLoading {
                deleteArtistUseCase(state.value.artists)
            }
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonArtistUseCase.upsertAll(
                    allArtists = state.value.artists.map {
                        it.artist.copy(
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
                    state.value.artists
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
                    state.value.artists
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
                    state.value.artists
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
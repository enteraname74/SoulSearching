package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMultiMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MusicBottomSheetViewModel(
    private val playerMusicListViewManager: PlayerMusicListViewManager,
    private val playerViewManager: PlayerViewManager,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val multiSelectionManager: MultiSelectionManager,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase,
    private val playbackManager: PlaybackManager,
    private val navScope: MusicBottomSheetNavScope,
    settings: SoulSearchingSettings,
    params: MusicBottomSheetDestination,
) : ViewModel() {
    private val musicIds: List<UUID> = params.musicIds
    private val playlistId: UUID? = params.playlistId

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    val state: StateFlow<MusicBottomSheetState> = combine(
        commonMusicUseCase.getFromIds(musicIds),
        playbackManager.mainState,
        _dialogState,
        settings.getFlowOn(
            settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
        )
    ) { musics, playbackState, dialogState, isQuickAccessShown ->
        MusicBottomSheetState(
            musics = musics,
            bottomSheetTopInformation = buildTopInformation(musics),
            rowSpecs = buildRowSpecs(
                musics = musics,
                playedList = (playbackState as? PlaybackManagerState.Data)?.playedList
                    ?: emptyList(),
                currentPlayedMusic = (playbackState as? PlaybackManagerState.Data)?.currentMusic,
                isQuickAccessShown = isQuickAccessShown,
            ),
            dialogState = dialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MusicBottomSheetState(),
    )

    private fun buildRowSpecs(
        musics: List<Music>,
        playedList: List<Music>,
        currentPlayedMusic: Music?,
        isQuickAccessShown: Boolean,
    ): List<BottomSheetRowSpec> = buildList {
        val editEnabled: Boolean = musics.size == 1
        val queueAction: Boolean = if (musics.size == 1) {
            val isSameMusic =
                currentPlayedMusic != null && musics.first().musicId == currentPlayedMusic.musicId
            !isSameMusic || playedList.isEmpty()
        } else {
            true
        }
        val removeFromPlayedList: Boolean = if (musics.size == 1) {
            playedList.any { it.musicId == musics.first().musicId }
        } else {
            playedList.isNotEmpty()
        }

        if (isQuickAccessShown) {
            val isInQuickAccess: Boolean = if (musics.size == 1) {
                musics.first().isInQuickAccess
            } else {
                musics.all { it.isInQuickAccess }
            }
            add(
                BottomSheetRowSpec.quickAccess(
                    onClick = { handleQuickAccess(!isInQuickAccess) },
                    isInQuickAccess = if (musics.size == 1) {
                        musics.first().isInQuickAccess
                    } else {
                        musics.all { it.isInQuickAccess }
                    }
                )
            )
        }
        add(
            BottomSheetRowSpec.addToPlaylist(
                onClick = ::addToPlaylists
            )
        )
        if (editEnabled) {
            add(
                BottomSheetRowSpec(
                    icon = Icons.Rounded.Edit,
                    title = strings.modifyMusic,
                    onClick = ::modifyMusic,
                )
            )
        }
        if (queueAction) {
            addAll(
                listOf(
                    BottomSheetRowSpec.playNext(::playNext),
                    BottomSheetRowSpec.addToQueue(::addToQueue),
                )
            )
        }

        if (removeFromPlayedList) {
            add(
                BottomSheetRowSpec(
                    icon = Icons.Rounded.PlaylistRemove,
                    title = strings.removeFromPlayedList,
                    onClick = ::removeFromPlayedList,
                )
            )
        }

        if (playlistId != null) {
            add(
                BottomSheetRowSpec(
                    icon = Icons.Rounded.Delete,
                    title = strings.removeFromPlaylist,
                    onClick = ::showRemoveFromPlaylistDialog,
                )
            )
        }

        add(
            BottomSheetRowSpec(
                icon = Icons.Rounded.Delete,
                title = if (musics.size == 1) {
                    strings.deleteMusic
                } else {
                    strings.deleteSelectedMusics
                },
                onClick = ::showDeleteDialog,
            )
        )
    }

    private fun buildTopInformation(musics: List<Music>): BottomSheetTopInformation =
        if (musics.size == 1) {
            val music = musics.first()
            BottomSheetTopInformation(
                title = music.name,
                subTitle = music.informationText,
                cover = music.cover,
            )
        } else {
            BottomSheetTopInformation(
                title = strings.multipleSelection,
                subTitle = strings.selectedElements(total = musics.size),
                cover = null,
            )
        }

    private suspend fun minimisePlayerViewsIfNeeded() {
        if (playerMusicListViewManager.currentValue == BottomSheetStates.EXPANDED) {
            playerMusicListViewManager.animateTo(newState = BottomSheetStates.COLLAPSED)
        }
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
        }
    }

    private fun deleteMusics() {
        viewModelScope.launch {
            deleteMusicUseCase(musicIds = musicIds)
            _dialogState.value = null
            multiSelectionManager.clearMultiSelection()
            // TODO PLAYER: This call should be unnecessary
            playbackManager.removeSongsFromPlayedPlaylist(musicIds)
            navScope.navigateBack()
        }
    }

    private fun removeFromPlaylist() {
        if (playlistId == null) return

        viewModelScope.launch {
            musicIds.forEach { musicId ->
                commonMusicPlaylistUseCase.delete(
                    musicId = musicId,
                    playlistId = playlistId,
                )
                _dialogState.value = null
                multiSelectionManager.clearMultiSelection()
                navScope.navigateBack()
            }
        }
    }

    private fun modifyMusic() {
        if (state.value.musics.size > 1) return
        viewModelScope.launch {
            minimisePlayerViewsIfNeeded()
            multiSelectionManager.clearMultiSelection()
            navScope.toModifyMusic(musicIds.first())
        }
    }

    private fun handleQuickAccess(newValue: Boolean) {
        viewModelScope.launch {
            commonMusicUseCase.upsertAll(
                allMusics = state.value.musics.map {
                    it.copy(
                        isInQuickAccess = newValue,
                    )
                }
            )
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun showDeleteDialog() {
        _dialogState.value = if (state.value.musics.size == 1) {
            DeleteMusicDialog(
                onDelete = { deleteMusics() },
                onClose = { _dialogState.value = null }
            )
        } else {
            DeleteMultiMusicDialog(
                onDelete = { deleteMusics() },
                onClose = { _dialogState.value = null }
            )
        }
    }

    private fun showRemoveFromPlaylistDialog() {
        if (playlistId == null) return
        _dialogState.value = if (state.value.musics.size == 1) {
            RemoveMusicFromPlaylistDialog(
                onConfirm = ::removeFromPlaylist,
                onClose = { _dialogState.value = null }
            )
        } else {
            RemoveMultiMusicFromPlaylistDialog(
                onConfirm = ::removeFromPlaylist,
                onClose = { _dialogState.value = null }
            )
        }
    }

    private fun addToPlaylists() {
        navScope.toAddToPlaylists(musicIds)
    }

    private fun removeFromPlayedList() {
        viewModelScope.launch {
            // TODO PLAYER: Should no longer be useful
            playbackManager.removeSongsFromPlayedPlaylist(musicIds)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun playNext() {
        viewModelScope.launch {
            playbackManager.addMultipleMusicsToPlayNext(state.value.musics)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun addToQueue() {
        viewModelScope.launch {
            playbackManager.addMultipleMusicsToQueue(state.value.musics)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }
}
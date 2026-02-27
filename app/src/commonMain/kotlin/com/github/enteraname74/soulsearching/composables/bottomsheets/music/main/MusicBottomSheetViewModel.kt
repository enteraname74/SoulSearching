package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
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
    params: MusicBottomSheetDestination,
) : ViewModel() {
    private val musicIds: List<UUID> = params.musicIds
    private val playlistId: UUID? = params.playlistId

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    val state: StateFlow<MusicBottomSheetState> = combine(
        commonMusicUseCase.getFromIds(musicIds),
        playbackManager.mainState,
        _dialogState,
    ) { musics, playbackState, dialogState ->
        MusicBottomSheetState(
            musics = musics,
            bottomSheetTopInformation = buildTopInformation(musics),
            itemsVisibility = buildItemsVisibility(
                musics = musics,
                playedList = (playbackState as? PlaybackManagerState.Data)?.playedList ?: emptyList(),
                currentPlayedMusic = (playbackState as? PlaybackManagerState.Data)?.currentMusic,
            ),
            dialogState = dialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MusicBottomSheetState(),
    )

    private fun buildItemsVisibility(
        musics: List<Music>,
        playedList: List<Music>,
        currentPlayedMusic: Music?
    ): MusicBottomSheetItemsVisibility =
        if (musics.size == 1) {
            val music = musics.first()
            val isSameMusic = currentPlayedMusic != null && music.musicId == currentPlayedMusic.musicId
            MusicBottomSheetItemsVisibility(
                removeFromPlayedList = playedList.any { it.musicId == music.musicId },
                queueActions = !isSameMusic || playedList.isEmpty(),
                isInQuickAccess = music.isInQuickAccess,
                editEnabled = true,
                inPlaylist = playlistId != null,
            )
        } else {
            MusicBottomSheetItemsVisibility(
                removeFromPlayedList = playedList.isNotEmpty(),
                queueActions = true,
                isInQuickAccess = musics.all { it.isInQuickAccess },
                editEnabled = false,
                inPlaylist = playlistId != null,
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

    fun modifyMusic() {
        if (state.value.musics.size > 1) return
        viewModelScope.launch {
            minimisePlayerViewsIfNeeded()
            multiSelectionManager.clearMultiSelection()
            navScope.toModifyMusic(musicIds.first())
        }
    }

    fun handleQuickAccess() {
        viewModelScope.launch {
            commonMusicUseCase.upsertAll(
                allMusics = state.value.musics.map {
                    it.copy(
                        isInQuickAccess = !state.value.itemsVisibility.isInQuickAccess,
                    )
                }
            )
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    fun showDeleteDialog() {
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

    fun showRemoveFromPlaylistDialog() {
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

    fun addToPlaylists() {
        navScope.toAddToPlaylists(musicIds)
    }

    fun removeFromPlayedList() {
        if (!state.value.itemsVisibility.removeFromPlayedList) return

        viewModelScope.launch {
            // TODO PLAYER: Should no longer be useful
            playbackManager.removeSongsFromPlayedPlaylist(musicIds)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    fun playNext() {
        viewModelScope.launch {
            playbackManager.addMultipleMusicsToPlayNext(state.value.musics)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    fun addToQueue() {
        viewModelScope.launch {
            playbackManager.addMultipleMusicsToQueue(state.value.musics)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }
}
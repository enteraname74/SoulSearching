package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetMode
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val musicId: UUID = params.musicId
    private val playlistId: UUID? = params.playlistId
    private val mode: MusicBottomSheetMode = params.mode

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    val state: StateFlow<MusicBottomSheetState> = combine(
        commonMusicUseCase.getFromId(musicId),
        playbackManager.mainState,
    ) { music, playbackState ->
        MusicBottomSheetState(
            selectedMusic = music,
            isCurrentlyPlaying = playbackManager.isSameMusicAsCurrentPlayedOne(musicId),
            isInPlayedList = (playbackState as? PlaybackManagerState.Data)
                ?.playedList
                ?.any { it.musicId == musicId } == true,
            mode = mode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MusicBottomSheetState(),
    )

    private suspend fun minimisePlayerViewsIfNeeded() {
        if (playerMusicListViewManager.currentValue == BottomSheetStates.EXPANDED) {
            playerMusicListViewManager.animateTo(newState = BottomSheetStates.COLLAPSED)
        }
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
        }
    }

    private fun deleteMusic(music: Music) {
        viewModelScope.launch {
            deleteMusicUseCase(music = music)
            _dialogState.value = null
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    private fun removeFromPlaylist() {
        if (playlistId == null) return

        viewModelScope.launch {
            commonMusicPlaylistUseCase.delete(
                musicId = musicId,
                playlistId = playlistId,
            )
        }
        _dialogState.value = null
        multiSelectionManager.clearMultiSelection()
        navScope.navigateBack()
    }

    fun modifyMusic() {
        viewModelScope.launch {
            minimisePlayerViewsIfNeeded()
            navScope.toModifyMusic(musicId)
        }
    }

    fun toggleQuickAccess() {
        viewModelScope.launch {
            val music: Music = state.value.selectedMusic ?: return@launch
            commonMusicUseCase.upsert(
                music = music.copy(
                    isInQuickAccess = !music.isInQuickAccess,
                )
            )
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    fun showDeleteDialog() {
        val music: Music = state.value.selectedMusic ?: return
        _dialogState.value = DeleteMusicDialog(
            musicToDelete = music,
            onDelete = { deleteMusic(music) },
            onClose = { _dialogState.value = null }
        )
    }

    fun showRemoveFromPlaylistDialog() {
        if (playlistId == null) return
        RemoveMusicFromPlaylistDialog(
            onConfirm = ::removeFromPlaylist,
            onClose = { _dialogState.value = null }
        )
    }

    fun addToPlaylists() {
        navScope.toAddToPlaylists(musicId)
    }

    fun removeFromPlayedList() {
        viewModelScope.launch {
            playbackManager.removeSongsFromPlayedPlaylist(
                musicIds = listOf(musicId)
            )
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    fun playNext() {
        val music: Music = state.value.selectedMusic ?: return

        viewModelScope.launch {
            playbackManager.addMusicToPlayNext(music = music)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }

    fun addToQueue() {
        val music: Music = state.value.selectedMusic ?: return

        viewModelScope.launch {
            playbackManager.addMusicToQueue(music = music)
            multiSelectionManager.clearMultiSelection()
            navScope.navigateBack()
        }
    }
}
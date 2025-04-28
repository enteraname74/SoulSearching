package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MusicBottomSheet(
    private val onClose: () -> Unit,
    private val selectedMusic: Music,
    private val onModifyMusic: () -> Unit,
    private val onPlayNext: () -> Unit,
    private val onAddToQueue: () -> Unit,
    private val onRemoveFromPlayedList: () -> Unit,
    private val onDeleteMusic: () -> Unit,
    private val onRemoveFromPlaylist: () -> Unit,
    private val onAddToPlaylist: () -> Unit,
    private val musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    private val toggleQuickAccess: () -> Unit,
): SoulBottomSheet, KoinComponent {
    private val playbackManager: PlaybackManager by inject()
    private val playerViewManager: PlayerViewManager by inject()
    private val playerMusicListViewManager: PlayerMusicListViewManager by inject()

    private suspend fun minimisePlayerViewsIfNeeded() {
        if (playerMusicListViewManager.currentValue == BottomSheetStates.EXPANDED) {
            playerMusicListViewManager.animateTo(newState = BottomSheetStates.COLLAPSED)
        }
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
        }
    }

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose
        ) { closeWithAnim ->
            Content(closeWithAnim = closeWithAnim)
        }
    }

    @Composable
    private fun Content(
        closeWithAnim: () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val playbackState by playbackManager.mainState.collectAsState(PlaybackManagerState.Stopped)

        MusicBottomSheetMenu(
            selectedMusic = selectedMusic,
            musicBottomSheetState = musicBottomSheetState,
            modifyAction = {
                closeWithAnim()
                coroutineScope
                    .launch {
                        minimisePlayerViewsIfNeeded()
                    }.invokeOnCompletion {
                        onModifyMusic()
                    }
            },
            quickAccessAction = {
                closeWithAnim()
                toggleQuickAccess()
            },
            removeAction = onDeleteMusic,
            addToPlaylistAction = onAddToPlaylist,
            removeFromPlaylistAction = onRemoveFromPlaylist,
            removeFromPlayedListAction = {
                closeWithAnim()
                onRemoveFromPlayedList()
            },
            playNextAction = {
                closeWithAnim()
                onPlayNext()
            },
            addToQueueAction = {
                closeWithAnim()
                onAddToQueue()
            },
            isInQuickAccess = selectedMusic.isInQuickAccess,
            isCurrentlyPlaying = playbackManager.isSameMusicAsCurrentPlayedOne(selectedMusic.musicId),
            isPlayedListEmpty = (playbackState as? PlaybackManagerState.Data)?.playedList?.isEmpty() != false,
        )
    }
}
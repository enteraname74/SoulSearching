package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MusicBottomSheet(
    private val onClose: () -> Unit,
    private val selectedMusic: Music,
    private val onModifyMusic: () -> Unit,
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
        playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
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

        MusicBottomSheetMenu(
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
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.removeSongFromPlayedPlaylist(
                        musicId = selectedMusic.musicId
                    )
                }
                closeWithAnim()
            },
            playNextAction = {
                coroutineScope.launch {
                    if (playerViewManager.currentValue == BottomSheetStates.COLLAPSED) {
                        playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
                    }
                }.invokeOnCompletion {
                    playbackManager.addMusicToPlayNext(
                        music = selectedMusic
                    )
                }
                closeWithAnim()
            },
            isInQuickAccess = selectedMusic.isInQuickAccess,
            isCurrentlyPlaying = playbackManager.isSameMusicAsCurrentPlayedOne(selectedMusic.musicId)
        )
    }
}
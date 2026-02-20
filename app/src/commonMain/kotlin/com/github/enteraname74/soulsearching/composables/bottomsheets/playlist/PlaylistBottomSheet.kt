package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class PlaylistBottomSheet(
    private val onClose: () -> Unit,
    private val selectedPlaylist: PlaylistPreview,
    private val onModifyPlaylist: () -> Unit,
    private val onPlayNext: () -> Unit,
    private val onAddToQueue: () -> Unit,
    private val onDeletePlaylist: () -> Unit,
    private val onRemoveFromPlayedList: () -> Unit,
    private val toggleQuickAccess: () -> Unit,
): SoulBottomSheet, KoinComponent {
    private val playbackManager: PlaybackManager by inject()

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
        val playbackState by playbackManager.state.collectAsState(PlaybackManagerState.Stopped)

        PlaylistBottomSheetMenu(
            selectedPlaylist = selectedPlaylist,
            modifyAction = {
                closeWithAnim()
                onModifyPlaylist()
            },
            deleteAction = onDeletePlaylist,
            quickAccessAction = {
                closeWithAnim()
                toggleQuickAccess()
            },
            isInQuickAccess = selectedPlaylist.isInQuickAccess,
            playNextAction = onPlayNext,
            addToQueueAction = onAddToQueue,
            removeFromPlayedListAction = {
                closeWithAnim()
                onRemoveFromPlayedList()
            },
            isPlayedListEmpty = playbackState.isEmpty(),
        )
    }
}
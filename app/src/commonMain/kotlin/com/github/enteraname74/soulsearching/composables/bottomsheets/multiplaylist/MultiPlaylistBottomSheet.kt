package com.github.enteraname74.soulsearching.composables.bottomsheets.multiplaylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class MultiPlaylistBottomSheet(
    private val onClose: () -> Unit,
    private val selectedIds: List<UUID>,
    private val onPlayNext: () -> Unit,
    private val onAddToQueue: () -> Unit,
    private val onRemoveFromPlayedList: () -> Unit,
    private val onDelete: () -> Unit,
): SoulBottomSheet, KoinComponent {
    private val playbackManager: PlaybackManager by inject()

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
        ) { _ ->
            Content()
        }
    }

    @Composable
    private fun Content() {
        val playbackState by playbackManager.state.collectAsState(PlaybackManagerState.Stopped)

        MultiPlaylistBottomSheetMenu(
            total = selectedIds.size,
            deleteAction = onDelete,
            playNextAction = onPlayNext,
            addToQueueAction = onAddToQueue,
            removeFromPlayedListAction = onRemoveFromPlayedList,
            isPlayedListEmpty = playbackState.isEmpty(),
        )
    }
}
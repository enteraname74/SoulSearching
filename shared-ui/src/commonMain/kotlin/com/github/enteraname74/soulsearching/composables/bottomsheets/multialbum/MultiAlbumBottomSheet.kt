package com.github.enteraname74.soulsearching.composables.bottomsheets.multialbum

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class MultiAlbumBottomSheet(
    private val onClose: () -> Unit,
    private val selectedIds: List<UUID>,
    private val onPlayNext: () -> Unit,
    private val onDelete: () -> Unit,
    private val onRemoveFromPlayedList: () -> Unit,
) : SoulBottomSheet, KoinComponent {
    private val playbackManager: PlaybackManager by inject()

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
        ) { closeWithAnim ->
            Content(closeWithAnim = closeWithAnim)
        }
    }

    @Composable
    private fun Content(
        closeWithAnim: () -> Unit,
    ) {
        val playbackState by playbackManager.mainState.collectAsState(PlaybackManagerState.Stopped)

        MultiAlbumBottomSheetMenu(
            total = selectedIds.size,
            deleteAction = onDelete,
            playNextAction = {
                closeWithAnim()
                onPlayNext()
            },
            isPlayedListEmpty = playbackState.isEmpty(),
            removeFromPlayedListAction = {
                closeWithAnim()
                onRemoveFromPlayedList()
            }
        )
    }
}
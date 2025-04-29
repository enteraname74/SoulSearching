package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlbumBottomSheet(
    private val onClose: () -> Unit,
    private val selectedAlbum: AlbumWithMusics,
    private val onModifyAlbum: () -> Unit,
    private val onPlayNext: () -> Unit,
    private val onAddToQueue: () -> Unit,
    private val onDeleteAlbum: () -> Unit,
    private val onRemoveFromPlayedList: () -> Unit,
    private val toggleQuickAccess: () -> Unit,
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

        AlbumBottomSheetMenu(
            modifyAction = {
                closeWithAnim()
                onModifyAlbum()
            },
            deleteAction = onDeleteAlbum,
            quickAccessAction = {
                closeWithAnim()
                toggleQuickAccess()
            },
            isInQuickAccess = selectedAlbum.isInQuickAccess,
            selectedAlbum = selectedAlbum,
            playNextAction = {
                closeWithAnim()
                onPlayNext()
            },
            addToQueueAction = {
                closeWithAnim()
                onAddToQueue()
            },
            removeFromPlayedListAction = {
                closeWithAnim()
                onRemoveFromPlayedList()
            },
            isPlayedListEmpty = playbackState.isEmpty(),
        )
    }
}
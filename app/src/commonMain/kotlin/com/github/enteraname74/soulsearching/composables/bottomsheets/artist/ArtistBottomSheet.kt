package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ArtistBottomSheet(
    private val onClose: () -> Unit,
    private val selectedArtist: ArtistWithMusics,
    private val onModifyArtist: () -> Unit,
    private val onPlayNext: () -> Unit,
    private val onAddToQueue: () -> Unit,
    private val onRemoveFromPlayedList: () -> Unit,
    private val onDeleteArtist: () -> Unit,
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
        val playbackState by playbackManager.mainState.collectAsState(PlaybackManagerState.Stopped)

        ArtistBottomSheetMenu(
            modifyAction = {
                closeWithAnim()
                onModifyArtist()
            },
            deleteAction = onDeleteArtist,
            quickAccessAction = {
                closeWithAnim()
                toggleQuickAccess()
            },
            isInQuickAccess = selectedArtist.artist.isInQuickAccess,
            selectedArtist = selectedArtist,
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
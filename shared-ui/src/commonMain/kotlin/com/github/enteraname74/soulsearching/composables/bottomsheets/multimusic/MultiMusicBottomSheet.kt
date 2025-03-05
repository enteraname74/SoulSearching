package com.github.enteraname74.soulsearching.composables.bottomsheets.multimusic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class MultiMusicBottomSheet(
    private val multiSelectionManagerImpl: MultiSelectionManagerImpl?,
    private val onClose: () -> Unit,
    private val selectedIds: List<UUID>,
    private val onDeleteAll: () -> Unit,
    private val onPlayNext: () -> Unit,
    private val onRemoveFromPlayedList: () -> Unit,
    private val onRemoveFromPlaylist: () -> Unit,
    private val onAddToPlaylist: () -> Unit,
    private val musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
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

        MultiMusicBottomSheetMenu(
            musicBottomSheetState = musicBottomSheetState,
            total = selectedIds.size,
            deleteAll = onDeleteAll,
            removeFromPlaylistAction = onRemoveFromPlaylist,
            removeFromPlayedListAction = {
                closeWithAnim()
                onRemoveFromPlayedList()
            },
            addToPlaylistAction = onAddToPlaylist,
            playNextAction = {
                closeWithAnim()
                onPlayNext()
            },
            isPlayedListEmpty = (playbackState as? PlaybackManagerState.Data)?.playedList?.isEmpty() != false,
        )
    }
}
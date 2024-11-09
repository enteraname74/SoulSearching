package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import java.util.*


class AddToPlaylistBottomSheet(
    private val onClose: () -> Unit,
    private val addMusicToSelectedPlaylists: (selectedPlaylists: List<PlaylistWithMusics>) -> Unit,
    private val playlistsWithMusics: List<PlaylistWithMusics>,
) : SoulBottomSheet {
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
        AddToPlaylistMenuBottomSheet(
            onDismiss = closeWithAnim,
            onConfirm = { selectedPlaylistsIds ->
                closeWithAnim()
                addMusicToSelectedPlaylists(selectedPlaylistsIds)
            },
            playlistsWithMusics = playlistsWithMusics,
        )
    }
}
package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeletePlaylistDialog(
    private val playlistToDelete: PlaylistWithMusics,
    private val onDelete: () -> Unit,
    private val onClose: () -> Unit,
): SoulDialog, KoinComponent {
    private val playbackManager: PlaybackManager by inject()

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction = {
                onDelete()
                CoroutineScope(Dispatchers.IO).launch {
                    playlistToDelete.musics.forEach {
                        playbackManager.removeSongFromPlayedPlaylist(
                            musicId = it.musicId
                        )
                    }
                }
            },
            dismissAction = onClose,
            title = strings.deletePlaylistDialogTitle,
            confirmText = strings.delete,
            dismissText = strings.cancel
        )
    }
}
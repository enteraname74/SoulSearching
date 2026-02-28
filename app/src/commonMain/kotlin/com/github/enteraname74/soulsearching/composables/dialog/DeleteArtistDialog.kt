package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeleteArtistDialog(
    private val artistToDelete: ArtistWithMusics,
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
                    playbackManager.removeSongsFromPlayedPlaylist(
                        musicIds = artistToDelete.musics.map { it.musicId }
                    )
                }
            },
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            title = strings.deleteArtistDialogTitle,
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_delete_filled,
                    contentDescription = strings.delete,
                )
            }
        )
    }
}







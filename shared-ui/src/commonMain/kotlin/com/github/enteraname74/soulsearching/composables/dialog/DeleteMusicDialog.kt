package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeleteMusicDialog(
    private val musicToDelete: Music,
    private val onDelete: () -> Unit,
    private val onClose: () -> Unit,
): SoulDialog, KoinComponent {
    private val playbackManager: PlaybackManager by inject()

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            title = strings.deleteMusicDialogTitle,
            text = strings.deleteMusicDialogText,
            confirmAction = {
                onDelete()
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.removeSongFromPlayedPlaylist(
                        musicId = musicToDelete.musicId
                    )
                }
            },
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            icon = {
                Image(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = strings.delete,
                    colorFilter = ColorFilter.tint(
                        SoulSearchingColorTheme.colorScheme.onPrimary
                    )
                )
            }
        )
    }
}
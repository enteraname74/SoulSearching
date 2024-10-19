package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import com.github.enteraname74.domain.model.ArtistWithMusics
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
                    artistToDelete.musics.forEach {
                        playbackManager.removeSongFromPlayedPlaylist(
                            musicId = it.musicId
                        )
                    }
                }
            },
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            title = strings.deleteArtistDialogTitle,
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







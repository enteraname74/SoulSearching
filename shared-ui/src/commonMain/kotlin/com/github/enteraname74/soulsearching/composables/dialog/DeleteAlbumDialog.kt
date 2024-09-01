package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeleteAlbumDialog(
    private val selectedAlbum: Album,
    private val onDelete: () -> Unit,
    private val onClose: () -> Unit,
): SoulDialog, KoinComponent {
    private val playbackManager: PlaybackManager by inject()
    private val getAlbumWithMusicsUseCase: GetAlbumWithMusicsUseCase by inject()

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction =  {
                onDelete()
                CoroutineScope(Dispatchers.IO).launch {
                    val albumWithMusics: AlbumWithMusics = getAlbumWithMusicsUseCase(
                        albumId = selectedAlbum.albumId,
                    ).first() ?: return@launch
                    albumWithMusics.musics.forEach {
                        playbackManager.removeSongFromPlayedPlaylist(
                            musicId = it.musicId
                        )
                    }
                }
            },
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            title = strings.deleteAlbumDialogTitle,
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
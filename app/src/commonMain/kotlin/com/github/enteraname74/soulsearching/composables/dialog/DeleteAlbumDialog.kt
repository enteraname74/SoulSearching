package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
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
    private val commonAlbumUseCase: CommonAlbumUseCase by inject()

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction =  {
                onDelete()
                CoroutineScope(Dispatchers.IO).launch {
                    val albumWithMusics: AlbumWithMusics = commonAlbumUseCase.getAlbumWithMusics(
                        albumId = selectedAlbum.albumId,
                    ).first() ?: return@launch
                    playbackManager.removeSongsFromPlayedPlaylist(
                        musicIds = albumWithMusics.musics.map { it.musicId }
                    )
                }
            },
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            title = strings.deleteAlbumDialogTitle,
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_delete_filled,
                    contentDescription = strings.delete,
                )
            }
        )
    }
}
package com.github.enteraname74.soulsearching.composables.dialog

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

class RemoveMultiMusicFromPlaylistDialog(
    private val onClose: () -> Unit,
    private val onConfirm: () -> Unit,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            title = CoreRes.string.remove_selected_music_from_playlist_title.toTextSpec(),
            text = CoreRes.string.remove_selected_music_from_playlist_text.toTextSpec(),
            confirmAction = onConfirm,
            dismissAction = onClose,
            confirmText = CoreRes.string.delete.toTextSpec(),
            dismissText = CoreRes.string.cancel.toTextSpec(),
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_delete_filled,
                    contentDescription = CoreRes.string.delete.toTextSpec().string,
                )
            }
        )
    }
}

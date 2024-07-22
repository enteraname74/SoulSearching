package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField


class CreatePlaylistDialog(
    private val onConfirm: (playlistName: String) -> Unit,
    private val onDismiss: () -> Unit,
) : SoulDialog {

    override fun Dialog() {
        var playlistName by rememberSaveable {
            mutableStateOf("")
        }
        val focusManager = LocalFocusManager.current

        SoulAlertDialog(
            confirmAction = { onConfirm(playlistName.trim()) },
            dismissAction = { onDismiss() },
            confirmText = strings.create,
            dismissText = strings.cancel,
            title = strings.createPlaylistDialogTitle,
            content = {
                SoulTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    labelName = strings.playlistName,
                    focusManager = focusManager
                )
            }
        )
    }
}
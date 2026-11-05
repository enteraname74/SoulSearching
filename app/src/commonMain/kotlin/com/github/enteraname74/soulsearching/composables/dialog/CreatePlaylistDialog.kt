package com.github.enteraname74.soulsearching.composables.dialog

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit

class CreatePlaylistDialog(
    private val onConfirm: (playlistName: String) -> Unit,
    private val onDismiss: () -> Unit,
) : SoulDialog {

    @Composable
    override fun Dialog() {
        var playlistName by rememberSaveable {
            mutableStateOf("")
        }
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        LaunchInit {
            focusRequester.requestFocus()
        }

        SoulAlertDialog(
            confirmAction = { onConfirm(playlistName.trim()) },
            dismissAction = {
                focusRequester.freeFocus()
                onDismiss()
            },
            confirmText = CoreRes.string.create.toTextSpec(),
            isConfirmButtonEnabled = playlistName.isNotBlank(),
            dismissText = CoreRes.string.cancel.toTextSpec(),
            title = CoreRes.string.create_playlist_dialog_title.toTextSpec(),
            content = {
                val baseHeight: Dp = 10.dp
                var textFieldHeight: Float by rememberSaveable { mutableFloatStateOf(0f) }
                Box(
                    modifier = Modifier
                        .height(baseHeight + textFieldHeight.toDp())
                        .animateContentSize()
                ) {
                    SoulTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onGloballyPositioned {
                                textFieldHeight = it.size.height.toFloat()
                            },
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        labelName = CoreRes.string.playlist_name.toTextSpec(),
                        focusManager = focusManager,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusRequester.freeFocus()
                                focusManager.clearFocus()
                                onConfirm(playlistName.trim())
                            }
                        ),
                        style = SoulTextFieldStyle.Unique,
                        error = CoreRes.string.field_cannot_be_empty.toTextSpec(),
                        isInError = playlistName.isBlank(),
                        isReadOnly = false,
                    )
                }
            }
        )
    }
}

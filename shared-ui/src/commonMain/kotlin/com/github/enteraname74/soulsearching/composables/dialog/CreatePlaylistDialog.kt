package com.github.enteraname74.soulsearching.composables.dialog

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
import com.github.enteraname74.soulsearching.coreui.strings.strings
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
            confirmText = strings.create,
            isConfirmButtonEnabled = playlistName.isNotBlank(),
            dismissText = strings.cancel,
            title = strings.createPlaylistDialogTitle,
            content = {
                val baseHeight: Dp = 10.dp
                var textFieldHeight: Float by rememberSaveable { mutableStateOf(0f) }
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
                        labelName = strings.playlistName,
                        focusManager = focusManager,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusRequester.freeFocus()
                                focusManager.clearFocus()
                                onConfirm(playlistName.trim())
                            }
                        ),
                        style = SoulTextFieldStyle.Unique,
                        error = strings.fieldCannotBeEmpty,
                        isInError = playlistName.isBlank(),
                    )
                }
            }
        )
    }
}
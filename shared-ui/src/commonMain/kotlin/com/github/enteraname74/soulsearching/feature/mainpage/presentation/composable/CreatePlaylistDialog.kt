package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

        SoulAlertDialog(
            confirmAction = { onConfirm(playlistName.trim()) },
            dismissAction = { onDismiss() },
            confirmText = strings.create,
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
                            .onGloballyPositioned {
                                textFieldHeight = it.size.height.toFloat()
                            },
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        labelName = strings.playlistName,
                        focusManager = focusManager,
                        keyboardActions = KeyboardActions(
                            onDone = {
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
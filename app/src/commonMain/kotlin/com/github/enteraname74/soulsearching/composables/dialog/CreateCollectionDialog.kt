package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit

class CreateCollectionDialog(
    private val onConfirm: (collectionName: String) -> Unit,
    private val onDismiss: () -> Unit,
) : SoulDialog {
    @Composable
    override fun Dialog() {
        var collectionName by rememberSaveable { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        LaunchInit { focusRequester.requestFocus() }

        SoulAlertDialog(
            confirmAction = { onConfirm(collectionName.trim()) },
            dismissAction = {
                focusRequester.freeFocus()
                onDismiss()
            },
            confirmText = strings.create,
            isConfirmButtonEnabled = collectionName.isNotBlank(),
            dismissText = strings.cancel,
            title = strings.createCollectionDialogTitle,
            content = {
                val baseHeight = 10.dp
                var textFieldHeight by rememberSaveable { mutableFloatStateOf(0f) }
                Box(
                    modifier = Modifier
                        .height(baseHeight + textFieldHeight.toDp())
                        .animateContentSize()
                ) {
                    SoulTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onGloballyPositioned { textFieldHeight = it.size.height.toFloat() },
                        value = collectionName,
                        onValueChange = { collectionName = it },
                        labelName = strings.collectionName,
                        focusManager = focusManager,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusRequester.freeFocus()
                                focusManager.clearFocus()
                                onConfirm(collectionName.trim())
                            }
                        ),
                        style = SoulTextFieldStyle.Unique,
                        error = strings.fieldCannotBeEmpty,
                        isInError = collectionName.isBlank(),
                        isReadOnly = false,
                    )
                }
            },
        )
    }
}

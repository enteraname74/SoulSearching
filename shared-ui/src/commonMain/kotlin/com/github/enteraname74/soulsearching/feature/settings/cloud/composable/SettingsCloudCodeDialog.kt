package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.dialog.SoulBasicDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle

class SettingsCloudCodeDialog(
    private val onDismiss: () -> Unit,
    private val code: CloudInscriptionCode,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulBasicDialog(
            onDismiss = onDismiss,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
            ) {
                TopBar()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(UiConstants.Spacing.large),
                    contentAlignment = Alignment.Center,
                ) {
                    CodeTextField(code = code)
                }
            }
        }
    }


    @Composable
    private fun TopBar() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                modifier = Modifier
                    .size(UiConstants.ImageSize.smallPlus)
            )

            Text(
                text = strings.cloudSignInCode,
                style = UiConstants.Typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            SoulIconButton(
                icon = Icons.Rounded.Cancel,
                onClick = onDismiss,
            )
        }
    }

    @Composable
    private fun CodeTextField(
        code: CloudInscriptionCode,
    ) {
        val clipboardManager = LocalClipboardManager.current

        SoulTextField(
            value = code.code,
            onValueChange = { _ -> },
            error = null,
            isInError = false,
            isActive = false,
            labelName = strings.cloudSignInCode,
            focusManager = LocalFocusManager.current,
            style = SoulTextFieldStyle.Unique,
            isPassword = true,
            leadingIcon = {
                SoulIconButton(
                    icon = Icons.Rounded.ContentCopy,
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code.code))
                    }
                )
            }
        )
    }
}
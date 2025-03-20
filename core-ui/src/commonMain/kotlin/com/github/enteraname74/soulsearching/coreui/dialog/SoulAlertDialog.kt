package com.github.enteraname74.soulsearching.coreui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulAlertDialog(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
    title: String? = null,
    text: String = "",
    icon: @Composable (() -> Unit)? = null,
    confirmText: String = strings.ok,
    dismissText: String? = null,
    backgroundColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    AlertDialog(
        onDismissRequest = dismissAction,
        confirmButton = {
            TextButton(onClick = { confirmAction() }) {
                Text(
                    text = confirmText,
                    color = contentColor
                )
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = dismissAction) {
                    Text(
                        text = it,
                        color = contentColor
                    )
                }
            }
        },
        title = title?.let {
            {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = text,
                textAlign = TextAlign.Center
            )
        },
        icon = icon,
        containerColor = backgroundColor,
        textContentColor = contentColor,
        titleContentColor = contentColor,
        iconContentColor = contentColor
    )
}

@Composable
fun SoulAlertDialog(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null,
    confirmText: String = strings.ok,
    isConfirmButtonEnabled: Boolean = true,
    dismissText: String = strings.cancel,
    backgroundColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    AlertDialog(
        onDismissRequest = dismissAction,
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = contentColor,
                    disabledContentColor = contentColor.copy(alpha = 0.7f),
                ),
                onClick = { confirmAction() },
                enabled = isConfirmButtonEnabled,
            ) {
                Text(
                    text = confirmText,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = dismissAction) {
                Text(
                    text = dismissText,
                    color = contentColor
                )
            }
        },
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            content()
        },
        icon = icon,
        containerColor = backgroundColor,
        textContentColor = contentColor,
        titleContentColor = contentColor,
        iconContentColor = contentColor
    )
}
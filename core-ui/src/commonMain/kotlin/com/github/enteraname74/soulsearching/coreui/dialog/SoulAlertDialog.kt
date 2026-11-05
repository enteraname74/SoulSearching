package com.github.enteraname74.soulsearching.coreui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.cancel
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ok
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulAlertDialog(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
    title: SoulTextSpec? = null,
    text: SoulTextSpec = "".toTextSpec(),
    icon: @Composable (() -> Unit)? = null,
    confirmText: SoulTextSpec = CoreRes.string.ok.toTextSpec(),
    dismissText: SoulTextSpec? = null,
    backgroundColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    AlertDialog(
        onDismissRequest = dismissAction,
        confirmButton = {
            TextButton(onClick = { confirmAction() }) {
                SoulText(
                    text = confirmText,
                    color = contentColor
                )
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = dismissAction) {
                    SoulText(
                        text = it,
                        color = contentColor
                    )
                }
            }
        },
        title = title?.let {
            {
                SoulText(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        text = {
            SoulText(
                text = text,
                textAlign = TextAlign.Center,
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
    title: SoulTextSpec,
    content: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null,
    confirmText: SoulTextSpec = CoreRes.string.ok.toTextSpec(),
    isConfirmButtonEnabled: Boolean = true,
    dismissText: SoulTextSpec = CoreRes.string.cancel.toTextSpec(),
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
                SoulText(
                    text = confirmText,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = dismissAction) {
                SoulText(
                    text = dismissText,
                    color = contentColor
                )
            }
        },
        title = {
            SoulText(
                text = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
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

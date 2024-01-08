package com.github.soulsearching.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.R
import com.github.soulsearching.theme.DynamicColor

@Composable
fun SoulSearchingDialog(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
    title: String,
    text: String,
    confirmText: String = stringResource(id = R.string.delete),
    dismissText: String = stringResource(id = R.string.cancel),
    primaryColor: Color = DynamicColor.primary,
    textColor: Color = DynamicColor.onPrimary
) {
    AlertDialog(
        onDismissRequest = dismissAction,
        confirmButton = {
            TextButton(onClick = { confirmAction() }) {
                Text(
                    text = confirmText,
                    color = textColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = dismissAction) {
                Text(
                    text = dismissText,
                    color = textColor
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
            Text(
                text = text,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Image(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(id = R.string.delete),
                colorFilter = ColorFilter.tint(textColor)
            )
        },
        containerColor = primaryColor,
        textContentColor = textColor,
        titleContentColor = textColor,
        iconContentColor = textColor
    )
}
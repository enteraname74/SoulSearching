package com.github.soulsearching.composables.dialogs

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.R

@Composable
fun SoulSearchingDialog(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
    title: String,
    text: String,
    confirmText: String = stringResource(id = R.string.delete),
    dismissText: String = stringResource(id = R.string.cancel)
) {
    AlertDialog(
        onDismissRequest = dismissAction,
        confirmButton = {
            TextButton(onClick = { confirmAction() }) {
                Text(
                    text = confirmText,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = dismissAction) {
                Text(
                    text = dismissText,
                    color = MaterialTheme.colorScheme.onPrimary
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
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.delete),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        textContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        iconContentColor = MaterialTheme.colorScheme.onPrimary
    )
}
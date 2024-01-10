package com.github.soulsearching.composables.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.theme.SoulSearchingColorTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SoulSearchingDialog(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
    title: String,
    text: String = "",
    confirmText: String,
    dismissText: String,
    backgroundColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    AlertDialog(
        shape = RoundedCornerShape(Constants.Spacing.veryLarge),
        onDismissRequest = dismissAction,
        confirmButton = {
            TextButton(
                modifier = Modifier.padding(
                    bottom = Constants.Spacing.veryLarge,
                    end = Constants.Spacing.large
                ),
                onClick = { confirmAction() }
            ) {
                Text(
                    text = confirmText,
                    color = contentColor
                )
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.padding(
                    bottom = Constants.Spacing.veryLarge
                ),
                onClick = dismissAction
            ) {
                Text(
                    text = dismissText,
                    color = contentColor
                )
            }
        },
        title = {
            Text(
                modifier = Modifier.padding(
                    horizontal = Constants.Spacing.medium,
                    vertical = Constants.Spacing.veryLarge
                ),
                text = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = SoulSearchingColorTheme.colorScheme.onPrimary
            )
        },
        text = {
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}
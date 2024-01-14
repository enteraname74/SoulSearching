package com.github.soulsearching.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.soulsearching.Constants
import com.github.soulsearching.theme.SoulSearchingColorTheme


@Composable
actual fun SoulSearchingDialog(
    confirmAction: () -> Unit,
    dismissAction: () -> Unit,
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Dialog(
        onDismissRequest = dismissAction
    ) {
        Card(
            shape = RoundedCornerShape(Constants.Spacing.veryLarge),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            )
        ) {
            Column(
                modifier = Modifier.padding(Constants.Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Constants.Spacing.large),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = dismissAction) {
                        Text(
                            text = dismissText,
                            color = contentColor
                        )
                    }
                    TextButton(onClick = { confirmAction() }) {
                        Text(
                            text = confirmText,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
package com.github.enteraname74.soulsearching.coreui.dialog

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulBasicDialog(
    onDismiss: () -> Unit,
    colors: SoulDialogColors = SoulBasicDialogDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colors.containerColor,
                contentColor = colors.contentColor,
            )
        ) {
            content()
        }
    }
}

object SoulBasicDialogDefaults {
    @Composable
    fun colors(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.primary,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    ): SoulDialogColors = SoulDialogColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}

data class SoulDialogColors(
    val containerColor: Color,
    val contentColor: Color,
)
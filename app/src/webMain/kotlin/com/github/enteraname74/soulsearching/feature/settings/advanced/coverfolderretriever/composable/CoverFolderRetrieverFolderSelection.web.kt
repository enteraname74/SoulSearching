package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_folder_filled
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
actual fun CoverFolderRetrieverFolderSelection(
    onSelectedFolder: (newFolderPath: String) -> Unit,
    currentFolder: String?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        Text(
            text = strings.coverFolderRetrieverPathSelectionTitle,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.body.copy(
                fontWeight = FontWeight.Bold,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoulIconButton(
                icon = CoreRes.drawable.ic_folder_filled,
                onClick = {},
            )
            Text(
                modifier = Modifier.weight(1f),
                text = currentFolder ?: strings.coverFolderRetrieverPathSelectionNoPathSelected,
                color = if (currentFolder != null) {
                    SoulSearchingColorTheme.colorScheme.onPrimary
                } else {
                    SoulSearchingColorTheme.colorScheme.subPrimaryText
                },
                style = UiConstants.Typography.body,
            )
        }
    }
}

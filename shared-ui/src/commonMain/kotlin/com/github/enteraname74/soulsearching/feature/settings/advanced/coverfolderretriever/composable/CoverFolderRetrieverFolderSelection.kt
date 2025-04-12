package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher

@Composable
fun CoverFolderRetrieverFolderSelection(
    onSelectedFolder: (newFolderPath: String) -> Unit,
    currentFolder: String,
    title: String,
) {
    val folderPicker = rememberDirectoryPickerLauncher(
        title = title,
    ) { platformFile ->
        platformFile?.absolutePath()?.let {
            onSelectedFolder(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        Text(
            text = title,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.bodyTitle,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = currentFolder,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                style = UiConstants.Typography.body,
            )
            Box(
                modifier = Modifier
                    .weight(
                        weight = 1f,
                        fill = false,
                    )
            ) {
                SoulIconButton(
                    icon = Icons.Rounded.Folder,
                    onClick = {
                        folderPicker.launch()
                    }
                )
            }
        }
    }
}
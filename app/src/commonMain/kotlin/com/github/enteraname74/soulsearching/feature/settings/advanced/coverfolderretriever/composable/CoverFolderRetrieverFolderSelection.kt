package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_folder_filled
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.util.FilePlatformUtils
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun CoverFolderRetrieverFolderSelection(
    onSelectedFolder: (newFolderPath: String) -> Unit,
    currentFolder: String?,
) {
    val folderPicker = rememberDirectoryPickerLauncher(
        dialogSettings = provideDirectoryPickerSettings(),
    ) { platformFile ->
        platformFile?.let {
            val path = FilePlatformUtils.getPath(file = platformFile)
            onSelectedFolder(path)
        }
    }

    CoverFolderRetrieverFolderSelectionContent(
        currentFolder = currentFolder,
        onClick = folderPicker::launch,
    )
}

@Composable
private fun CoverFolderRetrieverFolderSelectionContent(
    currentFolder: String?,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        SoulText(
            text = CoreRes.string.cover_folder_retriever_path_selection_title.toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.text,
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
                onClick = onClick,
            )
            SoulText(
                modifier = Modifier.weight(1f),
                text = currentFolder?.toTextSpec() ?: CoreRes.string.cover_folder_retriever_path_selection_no_path_selected.toTextSpec(),
                color = if (currentFolder != null) {
                    SoulSearchingColorTheme.colorScheme.text
                } else {
                    SoulSearchingColorTheme.colorScheme.textSecondary
                },
                style = UiConstants.Typography.body,
            )
        }
    }
}

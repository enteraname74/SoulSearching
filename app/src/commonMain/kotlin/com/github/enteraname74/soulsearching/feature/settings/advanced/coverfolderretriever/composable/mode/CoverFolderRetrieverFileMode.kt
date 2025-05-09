package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.mode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverExpander
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverFolderSelection

@Composable
fun CoverFolderRetrieverFileMode(
    actions: CoverFolderRetrieverActions,
    coverFolderRetriever: CoverFolderRetriever,
    extensionTextField: SoulTextFieldHolder,
) {
    val isSelected = coverFolderRetriever.mode == CoverFolderRetriever.DynamicMode.File

    CoverFolderRetrieverExpander(
        title = strings.coverFolderRetrieverFileTitle,
        subTitle = strings.coverFolderRetrieverFileText,
        isExpanded = isSelected,
        onClick = {
            if (!isSelected) {
                actions.toggleMode()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            CoverFolderRetrieverFolderSelection(
                onSelectedFolder = actions::updateFileModePath,
                currentFolder = coverFolderRetriever.fileModePath,
            )
            FileExtension(textField = extensionTextField)
        }
    }
}

@Composable
private fun FileExtension(
    textField: SoulTextFieldHolder,
) {
    textField.TextField(
        modifier = Modifier
            .fillMaxWidth(),
        focusManager = LocalFocusManager.current,
    )
}
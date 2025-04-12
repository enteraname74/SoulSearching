package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpand
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions

@Composable
fun CoverFolderRetrieverFolderMode(
    actions: CoverFolderRetrieverActions,
    coverFolderRetriever: CoverFolderRetriever
) {
    SoulMenuExpand(
        title = strings.coverFolderRetrieverFolderTitle,
        subTitle = strings.coverFolderRetrieverFolderText,
        clickAction = actions::toggleMode,
        isExpanded = coverFolderRetriever.mode == CoverFolderRetriever.DynamicMode.Folder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            CoverFolderRetrieverFolderSelection(
                onSelectedFolder = actions::updateFolderPath,
                currentFolder = coverFolderRetriever.folderPath,
                title = strings.coverFolderRetrieverFolderPathSelectionTitle,
            )
            CoverName(
                onUpdateCoverName = actions::updateCoverFileName,
                currentCoverName = coverFolderRetriever.coverFileName,
            )
        }
    }
}

@Composable
private fun CoverName(
    onUpdateCoverName: (newCoverName: String) -> Unit,
    currentCoverName: String,
) {
    SoulTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = currentCoverName,
        onValueChange = onUpdateCoverName,
        error = strings.fieldCannotBeEmpty,
        isInError = currentCoverName.isBlank(),
        labelName = strings.coverFolderRetrieverDynamicFileTitle,
        style = SoulTextFieldStyle.Unique,
        focusManager = LocalFocusManager.current,
    )
}
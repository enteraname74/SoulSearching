package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.mode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverExpander
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverFolderSelection
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun CoverFolderRetrieverFolderMode(
    actions: CoverFolderRetrieverActions,
    coverFolderRetriever: CoverFolderRetriever,
    coverFileNameTextField: SoulTextFieldHolder,
) {
    val isSelected = coverFolderRetriever.mode == CoverFolderRetriever.DynamicMode.Folder
    CoverFolderRetrieverExpander(
        title = CoreRes.string.cover_folder_retriever_folder_title.toTextSpec(),
        subTitle = CoreRes.string.cover_folder_retriever_folder_text.toTextSpec(),
        isExpanded = isSelected,
        onClick = {
            if (!isSelected) {
                actions.toggleMode()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = UiConstants.Spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            CoverFolderRetrieverFolderSelection(
                onSelectedFolder = actions::updateFolderModePath,
                currentFolder = coverFolderRetriever.folderModePath,
            )
            CoverName(textField = coverFileNameTextField)
        }
    }
}

@Composable
private fun CoverName(
    textField: SoulTextFieldHolder,
) {
    textField.TextField(
        modifier = Modifier
            .fillMaxWidth(),
        focusManager = LocalFocusManager.current,
    )
}

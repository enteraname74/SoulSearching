package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.mode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable.CoverFolderRetrieverExpander

@Composable
fun CoverFolderRetrieverFileMode(
    actions: CoverFolderRetrieverActions,
    coverFolderRetriever: CoverFolderRetriever,
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

        }
    }
}
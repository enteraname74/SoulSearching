package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.runtime.Composable

@Composable
expect fun CoverFolderRetrieverFolderSelection(
    onSelectedFolder: (newFolderPath: String) -> Unit,
    currentFolder: String?,
)

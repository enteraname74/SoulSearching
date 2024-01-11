package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel

@Composable
expect fun ModifyAlbumScreen(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    finishAction: () -> Unit
)
package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModel

@Composable
expect fun ModifyPlaylistScreen(
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    selectedPlaylistId: String,
    finishAction: () -> Unit
)
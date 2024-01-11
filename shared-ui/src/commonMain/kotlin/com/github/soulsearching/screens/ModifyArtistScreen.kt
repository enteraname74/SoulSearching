package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.viewmodel.ModifyArtistViewModel

@Composable
expect fun ModifyArtistScreen(
    modifyArtistViewModel: ModifyArtistViewModel,
    selectedArtistId: String,
    finishAction: () -> Unit,
)
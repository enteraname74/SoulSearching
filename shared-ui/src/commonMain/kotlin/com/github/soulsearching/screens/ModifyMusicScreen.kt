package com.github.soulsearching.screens

import androidx.compose.runtime.Composable
import com.github.soulsearching.viewmodel.ModifyMusicViewModel

@Composable
expect fun ModifyMusicScreen(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit
)
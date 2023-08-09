package com.github.soulsearching.composables.playlistComposable

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun RowPlaylistPanel(
    editAction: () -> Unit,
    shuffleAction: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(DynamicColor.primary)
        .padding(bottom = Constants.Spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImagesButton(
            editAction = editAction,
            shuffleAction = shuffleAction
        )
    }
}
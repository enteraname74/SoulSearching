package com.github.soulsearching.composables.playlistComposable

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun PlaylistPanel(
    editAction: () -> Unit,
    shuffleAction: () -> Unit,
    isLandscapeMode: Boolean
) {
    if (isLandscapeMode) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .background(DynamicColor.primary)
            .padding(end = Constants.Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ImagesButton(
                editAction = editAction,
                shuffleAction = shuffleAction
            )
        }
    } else {
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

}
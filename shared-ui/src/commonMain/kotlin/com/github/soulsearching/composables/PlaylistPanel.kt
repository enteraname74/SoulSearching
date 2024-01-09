package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.Constants
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.types.PlaylistType

@Composable
fun PlaylistPanel(
    editAction: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    isLandscapeMode: Boolean,
    playlistType: PlaylistType,
    primaryColor: Color = DynamicColor.primary,
    secondaryColor: Color = DynamicColor.secondary,
    tint: Color = DynamicColor.onSecondary
) {
    if (isLandscapeMode) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .background(primaryColor)
            .padding(end = Constants.Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ImagesButton(
                editAction = editAction,
                shuffleAction = shuffleAction,
                searchAction = searchAction,
                playlistType = playlistType,
                tint = tint,
                primaryColor = secondaryColor
            )
        }
    } else {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor)
            .padding(bottom = Constants.Spacing.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImagesButton(
                editAction = editAction,
                shuffleAction = shuffleAction,
                searchAction = searchAction,
                playlistType = playlistType,
                tint = tint,
                primaryColor = secondaryColor
            )
        }
    }

}
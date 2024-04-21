package com.github.soulsearching.elementpage.playlistpage.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.Constants
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.elementpage.domain.PlaylistType

@Composable
fun PlaylistPanel(
    editAction: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    playlistType: PlaylistType,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(primaryColor)
        .padding(bottom = Constants.Spacing.medium),
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
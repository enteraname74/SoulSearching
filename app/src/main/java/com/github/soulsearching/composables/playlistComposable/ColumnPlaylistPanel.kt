package com.github.soulsearching.composables.playlistComposable

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun ColumnPlaylistPanel(
    modifier: Modifier = Modifier,
    editAction: () -> Unit,
    shuffleAction: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxHeight()
        .composed { modifier }
        .clip(RoundedCornerShape(30.dp))
        .padding(Constants.Spacing.medium)
        .background(MaterialTheme.colorScheme.secondary)
        .padding(Constants.Spacing.medium),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagesButton(
            editAction = editAction,
            shuffleAction = shuffleAction
        )
    }
}
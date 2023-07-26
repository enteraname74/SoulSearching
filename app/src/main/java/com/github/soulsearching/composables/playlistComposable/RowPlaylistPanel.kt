package com.github.soulsearching.composables.playlistComposable

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
fun RowPlaylistPanel(
    modifier: Modifier = Modifier,
    editAction: () -> Unit,
    shuffleAction: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .composed { modifier }
        .clip(RoundedCornerShape(30.dp))
        .padding(Constants.Spacing.medium)
        .background(MaterialTheme.colorScheme.secondary)
        .padding(Constants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImagesButton(
            editAction = editAction,
            shuffleAction = shuffleAction
        )
    }
}
package com.github.soulsearching.composables.playlistComposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun ImagesButton(
    editAction: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit
) {
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = DynamicColor.secondary,
                shape = CircleShape
            )
            .clickable {
                editAction()
            }
            .padding(10.dp),
        imageVector = Icons.Rounded.Edit,
        contentDescription = "",
        colorFilter = ColorFilter.tint(DynamicColor.onSecondary)
    )
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = DynamicColor.secondary,
                shape = CircleShape
            )
            .padding(10.dp),
        imageVector = Icons.Rounded.PlaylistAdd,
        contentDescription = "",
        colorFilter = ColorFilter.tint(DynamicColor.onSecondary)
    )
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = DynamicColor.secondary,
                shape = CircleShape
            )
            .clickable {
                shuffleAction()
            }
            .padding(10.dp),
        imageVector = Icons.Rounded.Shuffle,
        contentDescription = "",
        colorFilter = ColorFilter.tint(DynamicColor.onSecondary)
    )
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = DynamicColor.secondary,
                shape = CircleShape
            )
            .clickable {
                searchAction()
            }
            .padding(10.dp),
        imageVector = Icons.Rounded.Search,
        contentDescription = "",
        colorFilter = ColorFilter.tint(DynamicColor.onSecondary)
    )
}
package com.github.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.soulsearching.types.PlaylistType
import com.github.soulsearching.theme.DynamicColor

@Suppress("UNUSED_PARAMETER")
@Composable
fun ImagesButton(
    editAction: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    playlistType: PlaylistType,
    primaryColor: Color = DynamicColor.secondary,
    tint: Color = DynamicColor.onSecondary
) {
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = primaryColor,
                shape = CircleShape
            )
            .clickable {
                editAction()
            }
            .padding(10.dp),
        imageVector = Icons.Rounded.Edit,
        contentDescription = "",
        colorFilter = ColorFilter.tint(tint)
    )
//    if (playlistType == PlaylistType.PLAYLIST) {
//        Image(
//            modifier = Modifier
//                .size(48.dp)
//                .background(
//                    color = primaryColor,
//                    shape = CircleShape
//                )
//                .padding(10.dp),
//            imageVector = Icons.Rounded.PlaylistAdd,
//            contentDescription = "",
//            colorFilter = ColorFilter.tint(tint)
//        )
//    }
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = primaryColor,
                shape = CircleShape
            )
            .clickable {
                shuffleAction()
            }
            .padding(10.dp),
        imageVector = Icons.Rounded.Shuffle,
        contentDescription = "",
        colorFilter = ColorFilter.tint(tint)
    )
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = primaryColor,
                shape = CircleShape
            )
            .clickable {
                searchAction()
            }
            .padding(10.dp),
        imageVector = Icons.Rounded.Search,
        contentDescription = "",
        colorFilter = ColorFilter.tint(tint)
    )
}
package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun PlaylistPanel(
    editAction: (() -> Unit)?,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor)
            .padding(vertical = UiConstants.Spacing.mediumPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImagesButton(
            editAction = editAction,
            shuffleAction = shuffleAction,
            searchAction = searchAction,
            tint = tint,
            primaryColor = secondaryColor
        )
    }

}

@Composable
private fun ImagesButton(
    editAction: (() -> Unit)?,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    editAction?.let {
        Image(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = primaryColor,
                    shape = CircleShape
                )
                .clickableWithHandCursor {
                    it()
                }
                .padding(10.dp),
            imageVector = Icons.Rounded.Edit,
            contentDescription = "",
            colorFilter = ColorFilter.tint(tint)
        )
    }
    Image(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = primaryColor,
                shape = CircleShape
            )
            .clickableWithHandCursor {
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
            .clickableWithHandCursor {
                searchAction()
            }
            .padding(10.dp),
        imageVector = Icons.Rounded.Search,
        contentDescription = "",
        colorFilter = ColorFilter.tint(tint)
    )
}
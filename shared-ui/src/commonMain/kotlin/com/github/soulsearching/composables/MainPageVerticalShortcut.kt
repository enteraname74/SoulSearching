package com.github.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import com.github.soulsearching.Constants
import com.github.soulsearching.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.ElementEnum

@Composable
fun MainPageVerticalShortcut(
    currentPage: Int,
    switchPageAction: (Int) -> Unit,
    visibleElements: List<ElementEnum>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        items(visibleElements) {
            val pos = visibleElements.indexOf(it)
            val isCurrentPosition = pos == currentPage ||
                    ((pos == visibleElements.size - 1) && (currentPage >= pos))

            Row(
                modifier = Modifier
                    .padding(
                        top = Constants.Spacing.large,
                        bottom = Constants.Spacing.large,
                        start = Constants.Spacing.medium,
                        end = Constants.Spacing.large
                    )
                    .clickable {
                        switchPageAction(pos)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(Constants.ImageSize.medium)
                        .alpha(
                            if (!isCurrentPosition) {
                                0F
                            } else {
                                1F
                            }
                        ),
                    colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onPrimary),
                    imageVector = when (it) {
                        ElementEnum.QUICK_ACCESS -> Icons.Rounded.DoubleArrow
                        ElementEnum.PLAYLISTS -> Icons.Rounded.QueueMusic
                        ElementEnum.ALBUMS -> Icons.Rounded.Album
                        ElementEnum.ARTISTS -> Icons.Rounded.Person
                        ElementEnum.MUSICS -> Icons.Rounded.MusicNote
                    },
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .rotate(-90f)
                        .vertical(),
                    text = when (it) {
                        ElementEnum.QUICK_ACCESS -> strings.quickAccess
                        ElementEnum.PLAYLISTS -> strings.playlists
                        ElementEnum.ALBUMS -> strings.albums
                        ElementEnum.ARTISTS -> strings.artists
                        ElementEnum.MUSICS -> strings.musics
                    },
                    color = if (isCurrentPosition) {
                        SoulSearchingColorTheme.colorScheme.onPrimary
                    } else {
                        SoulSearchingColorTheme.colorScheme.subText
                    },
                    fontWeight = if (isCurrentPosition) {
                        FontWeight.ExtraBold
                    } else {
                        FontWeight.Normal
                    }
                )
            }

        }
        item {
            PlayerSpacer()
        }
    }
}

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
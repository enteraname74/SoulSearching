package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.Pin
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material.icons.rounded.South
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType

@Composable
fun SortOptionsComposable(
    imageSize: Dp = UiConstants.ImageSize.small,
    sortByDateAction: () -> Unit,
    sortByMostListenedAction: () -> Unit,
    sortByName: () -> Unit,
    setSortDirection: () -> Unit,
    sortType: Int,
    sortDirection: Int
) {
    Row(
        modifier = Modifier.background(
            color = SoulSearchingColorTheme.colorScheme.secondary,
            shape = RoundedCornerShape(10.dp)
        ),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickable { sortByDateAction() }
                    .padding(UiConstants.Spacing.small)
                    .size(
                        if (sortType == SortType.ADDED_DATE) (imageSize + imageSize / 3) else imageSize
                    ),
                imageVector = Icons.Rounded.Schedule,
                contentDescription = strings.sortByDateAdded,
                tint = getSelectedColor(sortType == SortType.ADDED_DATE)
            )
            Icon(
                modifier = Modifier
                    .clickable { sortByMostListenedAction() }
                    .padding(UiConstants.Spacing.small)
                    .size(
                        if (sortType == SortType.NB_PLAYED) (imageSize + imageSize / 3) else imageSize
                    ),
                imageVector = Icons.Rounded.Pin,
                contentDescription = strings.sortByMostListened,
                tint = getSelectedColor(sortType == SortType.NB_PLAYED)
            )
            Icon(
                modifier = Modifier
                    .clickable { sortByName() }
                    .padding(UiConstants.Spacing.small)
                    .size(
                        if (sortType == SortType.NAME) (imageSize + imageSize / 3) else imageSize
                    ),
                imageVector = Icons.Rounded.SortByAlpha,
                contentDescription = strings.sortByName,
                tint = getSelectedColor(sortType == SortType.NAME)
            )
        }
        Icon(
            modifier = Modifier
                .clickable { setSortDirection() }
                .padding(UiConstants.Spacing.small)
                .size(
                    imageSize
                ),
            imageVector = if (sortDirection == SortDirection.ASC) Icons.Rounded.North else Icons.Rounded.South,
            contentDescription = strings.sortByAscOrDesc,
            tint = SoulSearchingColorTheme.colorScheme.onSecondary
        )
    }
}
@Composable
private fun getSelectedColor(condition: Boolean): Color =
    if (condition) SoulSearchingColorTheme.colorScheme.onSecondary else SoulSearchingColorTheme.colorScheme.subSecondaryText
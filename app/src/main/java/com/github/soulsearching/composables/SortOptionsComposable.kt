package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.SortDirection
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SortOptionsComposable(
    imageSize: Dp = Constants.ImageSize.small,
    sortByDateAction: () -> Unit,
    sortByMostListenedAction: () -> Unit,
    sortByName: () -> Unit,
    setSortTypeAction: () -> Unit,
    sortType: Int,
    sortDirection: Int
) {
    Row(
        modifier = Modifier.background(
            color = DynamicColor.secondary,
            shape = RoundedCornerShape(10.dp)
        ),
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickable { sortByDateAction() }
                    .padding(Constants.Spacing.small)
                    .size(
                        if (sortType == SortType.ADDED_DATE) (imageSize + imageSize / 3) else imageSize
                    ),
                imageVector = Icons.Rounded.Schedule,
                contentDescription = stringResource(id = R.string.sort_by_date_added),
                tint = if (sortType == SortType.ADDED_DATE) DynamicColor.outline else DynamicColor.onPrimary
            )
            Icon(
                modifier = Modifier
                    .clickable { sortByMostListenedAction() }
                    .padding(Constants.Spacing.small)
                    .size(
                        if (sortType == SortType.NB_PLAYED) (imageSize + imageSize / 3) else imageSize
                    ),
                imageVector = Icons.Rounded.Pin,
                contentDescription = stringResource(id = R.string.sort_by_most_listened),
                tint = if (sortType == SortType.NB_PLAYED) DynamicColor.outline else DynamicColor.onPrimary
            )
            Icon(
                modifier = Modifier
                    .clickable { sortByName() }
                    .padding(Constants.Spacing.small)
                    .size(
                        if (sortType == SortType.NAME) (imageSize + imageSize / 3) else imageSize
                    ),
                imageVector = Icons.Rounded.SortByAlpha,
                contentDescription = stringResource(id = R.string.sort_by_name),
                tint = if (sortType == SortType.NAME) DynamicColor.outline else DynamicColor.onPrimary
            )
        }
        Icon(
            modifier = Modifier
                .clickable { setSortTypeAction() }
                .padding(Constants.Spacing.small)
                .size(
                    imageSize
                ),
            imageVector = if (sortDirection == SortDirection.ASC) Icons.Rounded.North else Icons.Rounded.South,
            contentDescription = stringResource(id = R.string.sort_by_asc_or_dsc),
            tint = DynamicColor.onPrimary
        )
    }
}
package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SortOptionsComposable(
    setSortType: (Int) -> Unit,
    toggleSortDirection: () -> Unit,
    sortType: Int,
    sortDirection: Int
) {
    Row(
        modifier = Modifier
            .height(CONTAINER_HEIGHT)
            .background(
                color = SoulSearchingColorTheme.colorScheme.secondary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                all = UiConstants.Spacing.small,
            ),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoulIcon(
                modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp,
                        )
                    )
                    .clickable { setSortType(SortType.ADDED_DATE) },
                icon = Icons.Rounded.Schedule,
                contentDescription = strings.sortByDateAdded,
                tint = getSelectedColor(sortType == SortType.ADDED_DATE),
                size = getImageSize(isSelected = sortType == SortType.ADDED_DATE),
            )
            SoulIcon(
                modifier = Modifier
                    .clickable { setSortType(SortType.NB_PLAYED) },
                icon = Icons.Rounded.Pin,
                contentDescription = strings.sortByMostListened,
                tint = getSelectedColor(sortType == SortType.NB_PLAYED),
                size = getImageSize(isSelected = sortType == SortType.NB_PLAYED),
            )
            SoulIcon(
                modifier = Modifier
                    .clickable { setSortType(SortType.NAME) },
                icon = Icons.Rounded.SortByAlpha,
                contentDescription = strings.sortByName,
                tint = getSelectedColor(sortType == SortType.NAME),
                size = getImageSize(isSelected = sortType == SortType.NAME),
            )
        }
        SoulIcon(
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(
                        topEnd = 10.dp,
                        bottomEnd = 10.dp,
                    )
                )
                .clickable { toggleSortDirection() },
            icon = if (sortDirection == SortDirection.ASC) Icons.Rounded.North else Icons.Rounded.South,
            contentDescription = strings.sortByAscOrDesc,
            tint = SoulSearchingColorTheme.colorScheme.onSecondary,
            size = BASE_IMAGE_SIZE
        )
    }
}

private val BASE_IMAGE_SIZE: Dp = UiConstants.ImageSize.small
private val CONTAINER_HEIGHT: Dp = 32.dp

private fun getImageSize(isSelected: Boolean): Dp =
    if (isSelected) BASE_IMAGE_SIZE * 1.3f else BASE_IMAGE_SIZE

@Composable
private fun getSelectedColor(condition: Boolean): Color =
    if (condition) SoulSearchingColorTheme.colorScheme.onSecondary else SoulSearchingColorTheme.colorScheme.subSecondaryText
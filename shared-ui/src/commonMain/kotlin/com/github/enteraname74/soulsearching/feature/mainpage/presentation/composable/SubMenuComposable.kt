package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SubMenuComposable(
    modifier: Modifier = Modifier,
    title: String,
    setSortType: (Int) -> Unit,
    toggleSortDirection: () -> Unit,
    rightComposable: @Composable (() -> Unit) = {},
    leftComposable: @Composable (() -> Unit) = {},
    sortType: Int,
    sortDirection: Int,
    isUsingSort: Boolean = true,
    backgroundColor: Color = Color.Transparent
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(
                horizontal = UiConstants.Spacing.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .weight(1f, fill = false),
            text = title,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            leftComposable()
            if (isUsingSort) {
                SortOptionsComposable(
                    setSortType = setSortType,
                    toggleSortDirection = toggleSortDirection,
                    sortDirection = sortDirection,
                    sortType = sortType
                )
            }
            rightComposable()
        }
    }
}
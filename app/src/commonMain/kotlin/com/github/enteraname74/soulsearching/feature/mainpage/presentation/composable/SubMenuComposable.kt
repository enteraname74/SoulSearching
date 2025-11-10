package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SubMenuComposable(
    modifier: Modifier = Modifier,
    title: String,
    setSortType: (SortType) -> Unit,
    toggleSortDirection: () -> Unit,
    rightComposable: @Composable (() -> Unit) = {},
    sortType: SortType,
    sortDirection: SortDirection,
    isUsingSort: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SoulSearchingColorTheme.colorScheme.primary)
            .padding(horizontal = UiConstants.Spacing.medium)
            .padding(bottom = UiConstants.Spacing.medium),
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
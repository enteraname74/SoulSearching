package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType

@Composable
fun SubMenuComposable(
    modifier: Modifier = Modifier,
    title: SoulTextSpec,
    setSortType: (SortType) -> Unit,
    toggleSortDirection: () -> Unit,
    rightComposable: @Composable (() -> Unit) = {},
    sortType: SortType,
    sortDirection: SortDirection,
    isUsingSort: Boolean = true,
) {
    val paddingValues = if (isLargeWindow()) {
        PaddingValues(
            top = UiConstants.Spacing.veryLarge,
            start = UiConstants.Spacing.huge,
            end = UiConstants.Spacing.huge,
            bottom = UiConstants.Spacing.medium,
        )
    } else {
        PaddingValues(
            start = UiConstants.Spacing.medium,
            end = UiConstants.Spacing.medium,
            bottom = UiConstants.Spacing.medium,
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SoulSearchingColorTheme.colorScheme.background)
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SoulText(
            modifier = Modifier
                .weight(1f, fill = false),
            text = title,
            color = SoulSearchingColorTheme.colorScheme.text,
            style = if (isLargeWindow()) {
                UiConstants.Typography.title
            } else {
                UiConstants.Typography.titleSmall
            },
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

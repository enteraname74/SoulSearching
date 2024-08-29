package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.coreui.UiConstants

@Composable
fun <T> MainPageList(
    list: List<T>,
    title: String,
    leftComposable: @Composable () -> Unit = {},
    setSortType: (Int) -> Unit = {},
    toggleSortDirection: () -> Unit = {},
    sortType: Int = SortType.NAME,
    sortDirection: Int = SortDirection.DESC,
    isUsingSort: Boolean = true,
    item: @Composable (element: T) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubMenuComposable(
                title = title,
                setSortType = setSortType,
                toggleSortDirection = toggleSortDirection,
                sortType = sortType,
                sortDirection = sortDirection,
                leftComposable = leftComposable,
                isUsingSort = isUsingSort
            )
        }
        if (list.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.FixedSize(UiConstants.ImageSize.veryLarge),
                contentPadding = PaddingValues(
                    top = UiConstants.Spacing.small,
                    start = UiConstants.Spacing.small,
                    end = UiConstants.Spacing.small,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(
                    items = list
                ) { element ->
                    item(element)
                }
            }
        } else {
            NoElementView()
        }
    }
}
package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanelUiUtils
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyVerticalGridCompat
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.utils.OptionalPaddingForPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import kotlin.uuid.Uuid

@Composable
private fun mainPageListPaddingValues(): PaddingValues =
    if (NavigationPanelUiUtils.canShowPanel()) {
        PaddingValues(
            start = UiConstants.Spacing.huge,
            end = UiConstants.Spacing.huge,
            bottom = PlayerMinimisedHeight.toDp() + OptionalPaddingForPlayerSpacer
        )
    } else {
        PaddingValues(
            start = UiConstants.Spacing.medium,
            end = UiConstants.Spacing.medium,
            bottom = PlayerMinimisedHeight.toDp() + OptionalPaddingForPlayerSpacer
        )
    }

@Composable
fun <T> MainPageList(
    list: List<T>,
    title: SoulTextSpec,
    rightComposable: @Composable () -> Unit = {},
    setSortType: (SortType) -> Unit = {},
    toggleSortDirection: () -> Unit = {},
    sortType: SortType = SortType.DEFAULT,
    sortDirection: SortDirection = SortDirection.DEFAULT,
    isUsingSort: Boolean = true,
    key: ((T) -> Any)?,
    contentType: (T) -> Any,
    emptyTitle: SoulTextSpec,
    emptyDescription: SoulTextSpec,
    item: @Composable LazyGridItemScope.(element: T) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        SubMenuComposable(
            title = title,
            setSortType = setSortType,
            toggleSortDirection = toggleSortDirection,
            sortType = sortType,
            sortDirection = sortDirection,
            rightComposable = rightComposable,
            isUsingSort = isUsingSort
        )
        LazyVerticalGridCompat(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(UiConstants.ImageSize.veryLarge),
            contentPadding = mainPageListPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(
                if (NavigationPanelUiUtils.canShowPanel()) {
                    UiConstants.Spacing.mediumPlus
                } else {
                    UiConstants.Spacing.medium
                }
            ),
            horizontalArrangement = Arrangement.spacedBy(
                if (NavigationPanelUiUtils.canShowPanel()) {
                    UiConstants.Spacing.mediumPlus
                } else {
                    UiConstants.Spacing.medium
                }
            ),
        ) {
            items(
                items = list,
                key = key,
                contentType = contentType,
            ) { element ->
                item(element)
            }

            if (list.isEmpty()) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    EmptyCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = emptyTitle,
                        description = emptyDescription,
                    )
                }
            }
        }
    }
}

@Composable
fun <T : Any> MainPageListPaged(
    list: LazyPagingItems<T>,
    title: SoulTextSpec,
    rightComposable: @Composable () -> Unit = {},
    setSortType: (SortType) -> Unit = {},
    toggleSortDirection: () -> Unit = {},
    sortType: SortType = SortType.DEFAULT,
    sortDirection: SortDirection = SortDirection.DEFAULT,
    isUsingSort: Boolean = true,
    key: ((T?) -> Any?)?,
    contentType: Any,
    emptyTitle: SoulTextSpec?,
    emptyDescription: SoulTextSpec,
    item: @Composable LazyGridItemScope.(element: T) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SubMenuComposable(
            title = title,
            setSortType = setSortType,
            toggleSortDirection = toggleSortDirection,
            sortType = sortType,
            sortDirection = sortDirection,
            rightComposable = rightComposable,
            isUsingSort = isUsingSort
        )
        LazyVerticalGridCompat(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(UiConstants.ImageSize.veryLarge),
            contentPadding = mainPageListPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(
                if (NavigationPanelUiUtils.canShowPanel()) {
                    UiConstants.Spacing.mediumPlus
                } else {
                    UiConstants.Spacing.medium
                }
            ),
            horizontalArrangement = Arrangement.spacedBy(
                if (NavigationPanelUiUtils.canShowPanel()) {
                    UiConstants.Spacing.mediumPlus
                } else {
                    UiConstants.Spacing.medium
                }
            ),
        ) {
            if (list.itemCount > 0) {
                items(
                    count = list.itemCount,
                    key = { key?.invoke(list[it]) ?: Uuid.random() },
                    contentType = { contentType },
                ) { index ->
                    list[index]?.let {
                        item(it)
                    }
                }
            } else if (emptyTitle != null) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    EmptyCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = emptyTitle,
                        description = emptyDescription,
                    )
                }
            }
        }
    }
}

package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodUiStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.StatisticsUiElement
import kotlin.uuid.Uuid

object SettingsStatisticsSectionFactory {
    @OptIn(ExperimentalFoundationApi::class)
    fun LazyListScope.title(title: String) {
        stickyHeader(
            key = title,
            contentType = SECTION_TITLE_CONTENT_TYPE,
        ) {
            SettingsStatisticsSectionTitle(title = title)
        }
    }

    fun LazyListScope.emptyContent() {
        item(
            contentType = EMPTY_CONTENT_CONTENT_TYPE
        ) {
            NoElementView()
        }
    }

    fun LazyListScope.optionalContent(
        optionalContent: PeriodUiStatistics.Stats.OptionalContent?,
        modifier: Modifier = Modifier,
    ) {
        optionalContent?.let {
            item {
                SettingsStatisticsOptionalContent(
                    optionalContent = optionalContent,
                    modifier = modifier,
                )
            }
        }
    }

    fun LazyListScope.largeList(
        elements: LazyPagingItems<StatisticsUiElement>,
    ) {
        if (elements.itemCount == 1) {
            emptyContent()
        } else {
            items(
                key = { elements[it]?.id ?: Uuid.random() },
                count = elements.itemCount,
                contentType = { SECTION_LIST_CONTENT_TYPE }
            ) { index ->
                elements[index]?.let { elt ->
                    if (index > 0) {
                        // We skip the first element as it is shown elsewhere
                        LinearPreviewComposable(
                            padding = PaddingValues(
                                vertical = UiConstants.Spacing.small,
                            ),
                            title = elt.title,
                            text = elt.text,
                            cover = elt.cover,
                            onClick = elt.onClick,
                            onLongClick = null,
                        )
                    }
                }
            }
        }
    }

    fun LazyListScope.playerSpacer() {
        item {
            SoulPlayerSpacer()
        }
    }

    fun LazyListScope.smallList(
        elements: LazyPagingItems<StatisticsUiElement>,
    ) {
        if (elements.itemCount > 0) {
            items(
                key = { elements[it]?.id ?: Uuid.random() },
                count = elements.itemCount,
                contentType = { SECTION_LIST_CONTENT_TYPE }
            ) { index ->
                elements[index]?.let { elt ->
                    if (index == 0) {
                        SettingsStatisticsSectionHeader(
                            element = elt,
                            modifier = Modifier
                                .padding(vertical = UiConstants.Spacing.large)
                        )
                    } else {
                        LinearPreviewComposable(
                            padding = PaddingValues(
                                vertical = UiConstants.Spacing.small,
                            ),
                            title = elt.title,
                            text = elt.text,
                            cover = elt.cover,
                            onClick = elt.onClick,
                            onLongClick = null,
                        )
                    }
                }
            }
        } else {
            emptyContent()
        }
    }

    private const val SECTION_TITLE_CONTENT_TYPE: String = "SECTION_TITLE_CONTENT_TYPE"
    private const val SECTION_LIST_CONTENT_TYPE: String = "SECTION_LIST_CONTENT_TYPE"
    private const val EMPTY_CONTENT_CONTENT_TYPE: String = "EMPTY_CONTENT_CONTENT_TYPE"
}

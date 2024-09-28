package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.ListenedElement

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

    fun LazyListScope.header(
        element: ListenedElement,
        modifier: Modifier = Modifier,
    ) {
        item(
            contentType = SECTION_HEADER_CONTENT_TYPE,
        ) {
            SettingsStatisticsSectionHeader(
                element = element,
                modifier = modifier,
            )
        }
    }

    fun LazyListScope.list(
        elements: List<ListenedElement>
    ) {
        items(
            items = elements,
            contentType = { SECTION_LIST_CONTENT_TYPE }
        ) {element ->
            LinearPreviewComposable(
                padding = PaddingValues(
                    vertical = UiConstants.Spacing.small,
                ),
                title = element.title,
                text = element.text(),
                coverId = element.coverId,
                onClick = null,
                onLongClick = null,
            )
        }
    }

    private const val SECTION_TITLE_CONTENT_TYPE: String = "SECTION_TITLE_CONTENT_TYPE"
    private const val SECTION_HEADER_CONTENT_TYPE: String = "SECTION_HEADER_CONTENT_TYPE"
    private const val SECTION_LIST_CONTENT_TYPE: String = "SECTION_LIST_CONTENT_TYPE"
    private const val EMPTY_CONTENT_CONTENT_TYPE: String = "EMPTY_CONTENT_CONTENT_TYPE"
}
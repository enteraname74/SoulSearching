package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.ListenedElement

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.settingsStatisticsSection(
    title: String,
    elements: List<ListenedElement>
) {
    stickyHeader(
        key = title,
        contentType = SECTION_TITLE_CONTENT_TYPE,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = SoulSearchingColorTheme.colorScheme.primary,
                )
                .padding(
                    bottom = UiConstants.Spacing.medium,
                ),
            text = title,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleSmall,
        )
    }
    if (elements.isNotEmpty()) {
        items(
            contentType = { SECTION_LIST_CONTENT_TYPE },
            count = elements.size,
        ) { index ->
            val listenedElement = elements[index]
            LinearPreviewComposable(
                modifier = if (index == elements.lastIndex) {
                    Modifier.padding(
                        bottom = UiConstants.Spacing.veryHuge
                    )
                } else {
                    Modifier
                },
                padding = PaddingValues(
                    vertical = UiConstants.Spacing.small,
                ),
                title = listenedElement.title,
                text = listenedElement.text(),
                coverId = listenedElement.coverId,
                onClick = null,
                onLongClick = null,
            )
        }
    } else {
        item {
            NoElementView()
        }
    }
}


private const val SECTION_TITLE_CONTENT_TYPE: String = "SECTION_TITLE_CONTENT_TYPE"
private const val SECTION_LIST_CONTENT_TYPE: String = "SECTION_LIST_CONTENT_TYPE"
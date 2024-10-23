package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.MonthMusics
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.SoulHorizontalScrollBar
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun MusicMonthsHorizontalList(
    months: List<MonthMusics>,
    onMonthClicked: (month: String) -> Unit = {},
    onMonthLongClicked: (month: String) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = UiConstants.Spacing.veryLarge),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        Text(
            modifier = Modifier
                .padding(start = UiConstants.Spacing.medium),
            text = strings.byMonths,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleSmall,
        )
        if (months.isNotEmpty()) {
            Column {
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                    contentPadding = PaddingValues(
                        start = UiConstants.Spacing.medium,
                        end = UiConstants.Spacing.medium
                    )
                ) {
                    items(
                        items = months,
                        key = { it.month },
                        contentType = { MUSIC_MONTH_CONTENT_TYPE }
                    ) { element ->
                        BigPreviewComposable(
                            cover = element.cover,
                            title = element.month,
                            text = strings.musics(total = element.allMusicsSize),
                            onClick = {
                                onMonthClicked(element.month)
                            },
                            onLongClick = { onMonthLongClicked(element.month) }
                        )
                    }
                }
                SoulHorizontalScrollBar(
                    lazyListState = lazyListState
                )
            }
        } else {
            NoElementView()
        }
    }
}

private const val MUSIC_MONTH_CONTENT_TYPE: String = "MUSIC_MONTH_CONTENT_TYPE"
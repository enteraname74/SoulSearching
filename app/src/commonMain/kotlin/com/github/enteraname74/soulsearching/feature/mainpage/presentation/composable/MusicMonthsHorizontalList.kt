package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanelUiUtils
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.by_months
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.empty_months
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.empty_months_explanations
import com.github.enteraname74.soulsearching.coreui.list.LazyRowCompat
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.MonthMusicsPreview

@Composable
fun MusicMonthsHorizontalList(
    months: List<MonthMusicsPreview>,
    selectedMonths: List<String> = emptyList(),
    isSelectionModeOn: Boolean = false,
    onMonthClicked: (month: String) -> Unit = {},
    onMonthLongClicked: (month: String) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    val listContentPadding = if (NavigationPanelUiUtils.canShowPanel()) {
        PaddingValues(
            horizontal = UiConstants.Spacing.huge,
        )
    } else {
        PaddingValues(
            horizontal = UiConstants.Spacing.medium,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                if (NavigationPanelUiUtils.canShowPanel()) {
                    PaddingValues(
                        top = UiConstants.Spacing.veryLarge,
                    )
                } else {
                    PaddingValues(
                        bottom = UiConstants.Spacing.veryLarge,
                    )
                }
            )
            .padding(bottom = UiConstants.Spacing.veryLarge),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        SoulText(
            modifier = Modifier
                .padding(
                    if (NavigationPanelUiUtils.canShowPanel()) {
                        PaddingValues(
                            start = UiConstants.Spacing.huge,
                            end = UiConstants.Spacing.huge,
                            bottom = UiConstants.Spacing.medium,
                        )
                    } else {
                        PaddingValues(
                            start = UiConstants.Spacing.medium,
                        )
                    }
                ),
            text = CoreRes.string.by_months.toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.titleSmall,
        )
        if (months.isNotEmpty()) {
            Column {
                LazyRowCompat(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        if (NavigationPanelUiUtils.canShowPanel()) {
                            UiConstants.Spacing.mediumPlus
                        } else {
                            UiConstants.Spacing.medium
                        }
                    ),
                    contentPadding = listContentPadding,
                ) {
                    items(
                        items = months,
                        key = { it.month },
                        contentType = { MUSIC_MONTH_CONTENT_TYPE }
                    ) { element ->
                        BigPreviewComposable(
                            cover = element.cover,
                            title = element.month.toTextSpec(),
                            text = SoulTexts.musics(total = element.totalMusics),
                            onClick = {
                                onMonthClicked(element.month)
                            },
                            onLongClick = { onMonthLongClicked(element.month) },
                            isSelected = selectedMonths.contains(element.month),
                            isSelectionModeOn = isSelectionModeOn,
                        )
                    }
                }
            }
        } else {
            EmptyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = UiConstants.Spacing.medium,
                    ),
                title = CoreRes.string.empty_months.toTextSpec(),
                description = CoreRes.string.empty_months_explanations.toTextSpec(),
            )
        }
    }
}

private const val MUSIC_MONTH_CONTENT_TYPE: String = "MUSIC_MONTH_CONTENT_TYPE"

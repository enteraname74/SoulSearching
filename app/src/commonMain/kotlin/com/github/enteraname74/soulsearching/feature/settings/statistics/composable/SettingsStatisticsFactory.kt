package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.paging.compose.LazyPagingItems
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButton
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButtonData
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.no_data_on_this_period
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsListStatisticsCard
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsMusicStatisticsCard
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodGeneralUiStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.StatisticsUiElement
import kotlin.math.max
import kotlin.uuid.Uuid

object SettingsStatisticsFactory {
    fun LazyListScope.emptyContent() {
        item(
            contentType = EMPTY_CONTENT_CONTENT_TYPE
        ) {
            NoElementView(
                modifier = Modifier
                    .animateItem(),
                text = CoreRes.string.no_data_on_this_period.toTextSpec(),
            )
        }
    }

    fun LazyListScope.spacer(
        height: Dp
    ) {
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            )
        }
    }

    fun LazyListScope.playerSpacer() {
        item {
            SoulPlayerSpacer()
        }
    }

    fun LazyListScope.smallCards(
        generalUiStatistics: PeriodGeneralUiStatistics?,
    ) {
        if (!generalUiStatistics?.smallCards.isNullOrEmpty()) {
            item {
                SettingsStatisticsSmallCardsView(
                    modifier = Modifier
                        .animateItem()
                        .padding(
                            horizontal = UiConstants.Spacing.large,
                        ),
                    specs = generalUiStatistics.smallCards,
                )
            }
        }
    }

    fun LazyListScope.firstLargeCards(
        generalUiStatistics: PeriodGeneralUiStatistics?,
        musicStatistics: ListStatistics.Musics?,
    ) {
        item {
            Row(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .padding(
                        vertical = UiConstants.Spacing.large,
                        horizontal = UiConstants.Spacing.large,
                    ),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
            ) {
                var maxArtistsStatsHeight: Int? by rememberSaveable {
                    mutableStateOf(null)
                }

                generalUiStatistics?.musicStatistics?.toUiSpec()?.forEach { spec ->
                    val weight = if (musicStatistics == null) .5f else .3f

                    SettingsStatisticsMusicStatisticsCard(
                        modifier = Modifier
                            .weight(weight)
                            .onGloballyPositioned {
                                maxArtistsStatsHeight = max(it.size.height, maxArtistsStatsHeight ?: 0)
                            },
                        spec = spec,
                    )
                }
                musicStatistics?.let {
                    SettingsStatisticsListStatisticsCard(
                        modifier = Modifier
                            .weight(.4f)
                            .then(
                                maxArtistsStatsHeight?.let { height ->
                                    Modifier.height(height.toDp())
                                } ?: Modifier
                            ),
                        listStatistics = it,
                        maxItems = 4,
                    )
                }
            }
        }
    }

    fun LazyListScope.firstMediumCards(
        generalUiStatistics: PeriodGeneralUiStatistics?,
    ) {
        item {
            Row(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .padding(
                        vertical = UiConstants.Spacing.large,
                        horizontal = UiConstants.Spacing.large,
                    ),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
            ) {
                generalUiStatistics?.musicStatistics?.toUiSpec()?.forEach { spec ->
                    SettingsStatisticsMusicStatisticsCard(
                        modifier = Modifier
                            .weight(1f),
                        spec = spec,
                    )
                }
            }
        }
    }

    fun LazyListScope.otherCards(
        listStatistics: List<ListStatistics>,
        itemsPerRow: Int,
    ) {
        item {
            SettingsStatisticsListStatisticsListView(
                modifier = Modifier
                    .padding(
                        horizontal = UiConstants.Spacing.large,
                    ),
                listStatistics = listStatistics,
                itemsPerRow = itemsPerRow,
            )
        }
    }

    fun LazyListScope.musicStatisticsCards(
        generalUiStatistics: PeriodGeneralUiStatistics?,
        modifier: Modifier = Modifier,
    ) {
        item {
            Box(
                modifier = modifier
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus)
                ) {
                    generalUiStatistics?.musicStatistics?.toUiSpec()?.forEach { spec ->
                        SettingsStatisticsMusicStatisticsCard(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            spec = spec,
                        )
                    }
                }
            }
        }
    }

    fun LazyListScope.listStatisticsHeader(
        statistics: List<ListStatistics>,
        selectedType: ListStatistics.Type,
        onTypeChange: (ListStatistics.Type) -> Unit,
    ) {
        stickyHeader {
            val choicesOption: ListStatistics.Choices? = statistics
                .find { it.type == selectedType } as? ListStatistics.Choices

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoulSearchingColorTheme.colorScheme.background)
                    .padding(
                        bottom = UiConstants.Spacing.mediumPlus,
                    ),
            ) {
                SettingsStatisticsListStatisticsHeader(
                    statistics = statistics,
                    selectedType = selectedType,
                    onTypeChange = onTypeChange,
                )
                choicesOption?.takeIf { it.choices.size > 1 }?.let {
                    SoulChoiceButton(
                        modifier = Modifier
                            .padding(
                                start = UiConstants.Spacing.large,
                            ),
                        choices = choicesOption.choices.map { type ->
                            SoulChoiceButtonData(
                                icon = null,
                                data = type,
                                title = type.title,
                            )
                        },
                        currentChoiceName = it.currentChoice.title,
                        onClick = it.onChoiceChange,
                    )
                }
            }
        }
    }

    fun LazyListScope.listStatisticsItems(
        list: LazyPagingItems<StatisticsUiElement>,
    ) {
        if (list.itemCount == 0) {
            emptyContent()
        } else {
            items(
                count = list.itemCount,
                key = { list[it]?.id ?: Uuid.random() },
                contentType = { SECTION_LIST_CONTENT_TYPE }
            ) { index ->
                list[index]?.let {
                    LinearPreviewComposable(
                        modifier = Modifier.animateItem(
                            placementSpec = null,
                        ),
                        title = it.title,
                        text = it.text,
                        cover = it.cover,
                        onLongClick = null,
                        onClick = it.onClick,
                        padding = PaddingValues(
                            vertical = UiConstants.Spacing.medium,
                            horizontal = UiConstants.Spacing.large,
                        )
                    )
                }
            }
        }
    }

    private const val SECTION_LIST_CONTENT_TYPE: String = "SECTION_LIST_CONTENT_TYPE"
    private const val EMPTY_CONTENT_CONTENT_TYPE: String = "EMPTY_CONTENT_CONTENT_TYPE"
}

package com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButton
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButtonData
import com.github.enteraname74.soulsearching.coreui.composable.SoulCard
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics
import kotlin.math.min

@Composable
fun SettingsStatisticsListStatisticsCard(
    listStatistics: ListStatistics,
    maxItems: Int,
    modifier: Modifier = Modifier,
) {
    val choicesOption: ListStatistics.Choices? = (listStatistics as? ListStatistics.Choices)?.takeIf { it.choices.size > 1 }
    val list = listStatistics.list.collectAsLazyPagingItems()
    val max = min(maxItems, list.itemCount)

    SoulCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = UiConstants.Spacing.medium,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        top = if (choicesOption == null) UiConstants.Spacing.large else UiConstants.Spacing.medium,
                        start = UiConstants.Spacing.large,
                        end = UiConstants.Spacing.large,
                        bottom = if (choicesOption == null) UiConstants.Spacing.medium else 0.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
            ) {
                SoulText(
                    modifier = Modifier
                        .weight(1f),
                    text = listStatistics.type.sectionTitle,
                    color = SoulSearchingColorTheme.colorScheme.text,
                    style = UiConstants.Typography.bodyTitle,
                )
                choicesOption?.let {
                    SoulChoiceButton(
                        choices = choicesOption.choices.map { type ->
                            SoulChoiceButtonData(
                                icon = null,
                                data = type,
                                title = type.title,
                            )
                        },
                        currentChoiceName = it.currentChoice.title,
                        onClick = it.onChoiceChange,
                        colors = SoulButtonDefaults.surfaceHigh(),
                    )
                }
            }
            if (list.itemCount == 0) {
                NoElementView()
            } else {
                for (index in 0 until max) {
                    list.peek(index)?.let {
                        LinearPreviewComposable(
                            title = it.title,
                            text = it.text,
                            cover = it.cover,
                            onClick = it.onClick,
                            onLongClick = null,
                            padding = PaddingValues(
                                vertical = UiConstants.Spacing.medium,
                                horizontal = UiConstants.Spacing.large,
                            )
                        )
                    }
                }
            }
        }
    }
}
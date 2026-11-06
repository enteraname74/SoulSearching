package com.github.enteraname74.soulsearching.feature.settings.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.composable.SoulDivider
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_arrow_dropdown_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_all_period_label
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_month_period_label
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_title
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_year_period_label
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulBigTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarAction
import com.github.enteraname74.soulsearching.coreui.utils.isSmallWindow
import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsView
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodType

@Composable
fun SettingsStatisticsScreen(
    state: SettingsStatisticsState,
) {
    SoulScreen {
        Column {
            Header(state = state)
            SettingsStatisticsView(state = state)
        }
    }
}

@Composable
private fun Header(
    state: SettingsStatisticsState,
) {
    if (isSmallWindow()) {
        Column(
            modifier = Modifier
                .background(SoulSearchingColorTheme.colorScheme.background)
                .padding(
                    bottom = UiConstants.Spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
        ) {
            SoulTopBar(
                title = CoreRes.string.statistics_title.toTextSpec(),
                leftAction = SoulTopBarAction.navigation(state.navigateBack),
            )
            PeriodButton(
                modifier = Modifier
                    .padding(
                        start = UiConstants.Spacing.large,
                    ),
                currentPeriod = state.selectedPeriod,
                allPeriods = state.availablePeriods,
                onPeriodChange = state.onNewPeriod,
                onPeriodTypeChange = state.onNewPeriodType,
                canExpand = true,
                onSimpleClick = { },
            )
        }
    } else {
        SoulBigTopBar(
            subTitle = null,
            title = CoreRes.string.statistics_title.toTextSpec(),
            leftAction = SoulTopBarAction.navigation(onClick = state.navigateBack),
            rightAction = object : SoulTopBarAction {
                override val content: @Composable (() -> Unit) = {
                    PeriodButton(
                        modifier = Modifier
                            .padding(
                                end = UiConstants.Spacing.large,
                            ),
                        currentPeriod = state.selectedPeriod,
                        allPeriods = state.availablePeriods,
                        onPeriodChange = state.onNewPeriod,
                        onPeriodTypeChange = state.onNewPeriodType,
                        canExpand = true,
                        onSimpleClick = { },
                    )
                }
            }
        )
    }
}

@Composable
fun PeriodButton(
    currentPeriod: Period,
    allPeriods: List<Period>,
    onPeriodChange: (Period) -> Unit,
    onPeriodTypeChange: (PeriodType) -> Unit,
    canExpand: Boolean,
    onSimpleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val iconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(UiConstants.AnimationDuration.normal),
    )

    Box(
        modifier = modifier,
    ) {
        SoulButton(
            onClick = {
                if (canExpand) {
                    isExpanded = true
                } else {
                    isExpanded = false
                    onSimpleClick()
                }
            },
            shape = SoulButtonDefaults.LIGHT_ROUND_SHAPE,
            colors = SoulButtonDefaults.secondaryColors(),
            contentPadding = SoulButtonDefaults.contentPadding(
                horizontal = UiConstants.Spacing.medium,
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SoulText(
                    modifier = Modifier
                        .weight(
                            weight = 1f,
                            fill = false,
                        ),
                    text = SoulTexts.period(currentPeriod),
                    color = SoulSearchingColorTheme.colorScheme.text,
                    style = UiConstants.Typography.bodyTitle,
                    textAlign = TextAlign.Center
                )
                SoulIcon(
                    modifier = Modifier
                        .rotate(iconRotation),
                    icon = CoreRes.drawable.ic_arrow_dropdown_filled,
                    color = SoulSearchingColorTheme.colorScheme.text,
                )
            }

        }
        DropdownMenu(
            expanded = isExpanded,
            containerColor = SoulSearchingColorTheme.colorScheme.surface,
            onDismissRequest = { isExpanded = false },
        ) {

            MenuItem(
                title = CoreRes.string.statistics_all_period_label.toTextSpec(),
                contentColor = SoulSearchingColorTheme.colorScheme.text,
                onClick = {
                    onPeriodTypeChange(PeriodType.All)
                    isExpanded = false
                },
            )
            MenuItem(
                title = CoreRes.string.statistics_year_period_label.toTextSpec(),
                contentColor = SoulSearchingColorTheme.colorScheme.text,
                onClick = {
                    onPeriodTypeChange(PeriodType.Year)
                    isExpanded = false
                },
            )
            MenuItem(
                title = CoreRes.string.statistics_month_period_label.toTextSpec(),
                contentColor = SoulSearchingColorTheme.colorScheme.text,
                onClick = {
                    onPeriodTypeChange(PeriodType.Month)
                    isExpanded = false
                },
            )
            if (allPeriods.isNotEmpty()) {
                SoulDivider(
                    color = SoulSearchingColorTheme.colorScheme.textSecondary,
                    padding = PaddingValues.Zero,
                )
            }
            allPeriods.forEach { period ->
                MenuItem(
                    title = SoulTexts.period(period),
                    contentColor = SoulSearchingColorTheme.colorScheme.text,
                    onClick = {
                        onPeriodChange(period)
                        isExpanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    title: SoulTextSpec,
    onClick: () -> Unit,
    contentColor: Color,
) {
    DropdownMenuItem(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand),
        text = {
            SoulText(
                text = title,
                color = contentColor,
                style = UiConstants.Typography.body,
            )
        },
        onClick = onClick,
    )
}
package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.composable.SoulDivider
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodType
import kotlinx.coroutines.launch

@Composable
internal fun SettingsStatisticsPeriodsView(
    currentPeriod: Period,
    allPeriods: List<Period>,
    onPeriodChange: (Period) -> Unit,
    onPeriodTypeChange: (PeriodType) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPeriodPos = remember(allPeriods, currentPeriod) {
        allPeriods.indexOf(currentPeriod)
    }

    val pagerState = remember(allPeriods) {
        object : PagerState(currentPage = currentPeriodPos) {
            override val pageCount: Int = allPeriods.size
        }
    }

    LaunchedEffect(currentPeriodPos) {
        if (currentPeriodPos >= 0 && pagerState.settledPage != currentPeriodPos) {
            pagerState.animateScrollToPage(currentPeriodPos)
        }
    }

    LaunchedEffect(pagerState.settledPage, pagerState.isScrollInProgress) {
        if (pagerState.settledPage == currentPeriodPos || pagerState.isScrollInProgress) return@LaunchedEffect
        allPeriods.getOrNull(pagerState.settledPage)?.let {
            onPeriodChange(it)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        // The page width controls the distance between the centers of two
        // periods. Keep it smaller than half the viewport so more of the
        // previous and next buttons is visible around the selected one.
        val pageWidth = maxWidth * 0.4f

        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(
                start = (maxWidth - pageWidth) / 2,
                end = (maxWidth - pageWidth) / 2,
                bottom = UiConstants.Spacing.medium,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) { pos ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                PeriodButton(
                    currentPeriod = allPeriods[pos],
                    allPeriods = allPeriods,
                    onPeriodChange = onPeriodChange,
                    onPeriodTypeChange = onPeriodTypeChange,
                    canExpand = pagerState.targetPage == pos,
                    onSimpleClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = pos)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PeriodButton(
    currentPeriod: Period,
    allPeriods: List<Period>,
    onPeriodChange: (Period) -> Unit,
    onPeriodTypeChange: (PeriodType) -> Unit,
    canExpand: Boolean,
    onSimpleClick: () -> Unit,
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val contentColorAlpha by animateFloatAsState(
        targetValue = if (canExpand) 1f else UiConstants.ALPHA_DISABLED,
        animationSpec = tween(
            durationMillis = UiConstants.AnimationDuration.short,
        ),
    )
    val contentColor = SoulSearchingColorTheme.colorScheme.onPrimary.copy(
        alpha = contentColorAlpha,
    )

    Box {
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
            colors = SoulButtonColors(
                contentColor = contentColor,
                containerColor = Color.Transparent,
            ),
        ) {
            Text(
                text = strings.period(currentPeriod),
                color = contentColor,
                style = UiConstants.Typography.bodyTitle,
                textAlign = TextAlign.Center
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary,
            onDismissRequest = { isExpanded = false },
        ) {

            MenuItem(
                title = strings.statisticsAllPeriodLabel,
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                onClick = {
                    onPeriodTypeChange(PeriodType.All)
                    isExpanded = false
                },
            )
            MenuItem(
                title = strings.statisticsYearPeriodLabel,
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                onClick = {
                    onPeriodTypeChange(PeriodType.Year)
                    isExpanded = false
                },
            )
            MenuItem(
                title = strings.statisticsMonthPeriodLabel,
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                onClick = {
                    onPeriodTypeChange(PeriodType.Month)
                    isExpanded = false
                },
            )
            if (allPeriods.isNotEmpty()) {
                SoulDivider(
                    color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                    padding = PaddingValues.Zero,
                )
            }
            allPeriods.forEach { period ->
                MenuItem(
                    title = strings.period(period),
                    contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
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
    title: String,
    onClick: () -> Unit,
    contentColor: Color,
) {
    DropdownMenuItem(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand),
        text = {
            Text(
                text = title,
                color = contentColor,
                style = UiConstants.Typography.body,
            )
        },
        onClick = onClick,
    )
}

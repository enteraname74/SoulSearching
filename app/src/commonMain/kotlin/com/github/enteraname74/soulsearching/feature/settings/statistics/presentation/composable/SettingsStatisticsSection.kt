package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.ListenedElement

@Composable
fun SettingsStatisticsSection(
    title: String,
    elements: List<ListenedElement>
) {
    val windowSize = rememberWindowSize()

    when (windowSize) {
        WindowSize.Small -> {
            SmallScreen(
                title = title,
                elements = elements,
            )
        }

        else -> {
            LargeScreen(
                title = title,
                elements = elements,
            )
        }
    }
}

@Composable
private fun SmallScreen(
    title: String,
    elements: List<ListenedElement>
) {
    LazyColumnCompat(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = UiConstants.Spacing.large),
        contentPadding = PaddingValues(
            horizontal = UiConstants.Spacing.large,
        ),
    ) {
        with(SettingsStatisticsSectionFactory) {
            title(title = title)
            if (elements.isNotEmpty()) {
                header(
                    element = elements.first(),
                    modifier = Modifier
                        .padding(vertical = UiConstants.Spacing.large)
                )
                list(elements.drop(1))
            } else {
                emptyContent()
            }
            item {
                SoulPlayerSpacer()
            }
        }
    }
}

@Composable
private fun LargeScreen(
    title: String,
    elements: List<ListenedElement>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = UiConstants.Spacing.veryLarge
            )
            .padding(top = UiConstants.Spacing.veryLarge)
    ) {
        SettingsStatisticsSectionTitle(title = title)
        if (elements.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    SettingsStatisticsSectionHeader(
                        element = elements.first(),
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    LazyColumnCompat {
                        with(SettingsStatisticsSectionFactory) {
                            if (elements.isNotEmpty()) {
                                list(elements.drop(1))
                            } else {
                                emptyContent()
                            }
                            item {
                                SoulPlayerSpacer()
                            }
                        }
                    }
                }
            }
        } else {
            NoElementView()
        }
    }
}
package com.github.enteraname74.soulsearching.feature.appinit.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.app.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.navigation.SoulPageIndicator
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.domain.model.TabData
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi


@Composable
fun FetchingMusicTabLayoutComposable(
    modifier: Modifier = Modifier
) {
    val pagerScreens = listOf(
        TabData(
            title = strings.completeApplicationTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.completeApplicationTitle,
                    description = strings.completeApplicationText,
                    image = Res.drawable.system_dark_theme_main
                )
            }
        ),
        TabData(
            title = strings.quickAccessTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.quickAccessTitle,
                    description = strings.quickAccessText,
                    image = Res.drawable.perso_first_example
                )
            }
        ),
        TabData(
            title = strings.modifyElementsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.modifyElementsTitle,
                    description = strings.modifyElementsText,
                    image = Res.drawable.modify_album,
                )
            }
        ),
        TabData(
            title = strings.dynamicThemeFeatureTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.dynamicThemeFeatureTitle,
                    description = strings.dynamicThemeFeatureText,
                    image = Res.drawable.dynamic_main,
                )
            }
        ),
        TabData(
            title = strings.manageFoldersTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.manageFoldersTitle,
                    description = strings.manageFoldersText,
                    image = Res.drawable.folders_settings,
                )
            }
        ),
        TabData(
            title = strings.addNewMusicsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.addNewMusicsTitle,
                    description = strings.addNewMusicsText,
                    image = Res.drawable.add_new_songs_settings,
                )
            }
        ),
        TabData(
            title = strings.personalizeMainPageTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.personalizeMainPageTitle,
                    description = strings.personalizeMainPageText,
                    image = Res.drawable.perso_third_example,
                )
            }
        ),
    )

    val pagerState = rememberPagerState(
        pageCount = { pagerScreens.size }
    )

    val coroutineScope = rememberCoroutineScope()
    val windowSize = rememberWindowSize()

    when {
        windowSize != WindowSize.Small -> {
            Row(
                modifier = modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
            ) {
                VerticalPager(
                    modifier = Modifier.weight(1F),
                    state = pagerState,
                    pageSpacing = UiConstants.Spacing.veryLarge,
                    contentPadding = PaddingValues(UiConstants.Spacing.veryLarge)
                ) {
                    pagerScreens[it].screen()
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    pagerScreens.forEachIndexed { index, _ ->
                        SoulPageIndicator(
                            isSelected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        index
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
        else -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
            ) {
                HorizontalPager(
                    modifier = Modifier.weight(1F),
                    state = pagerState,
                    pageSpacing = UiConstants.Spacing.veryLarge,
                    contentPadding = PaddingValues(UiConstants.Spacing.veryLarge)
                ) {
                    pagerScreens[it].screen()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pagerScreens.forEachIndexed { index, _ ->
                        SoulPageIndicator(
                            isSelected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        index
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
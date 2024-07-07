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
import androidx.compose.ui.composed
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.Drawables
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FetchingMusicTabLayoutComposable(modifier: Modifier = Modifier) {
    val pagerScreens = listOf(
        PagerScreen(
            title = strings.completeApplicationTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.completeApplicationTitle,
                    description = strings.completeApplicationText,
                    imagePath = Drawables.systemDarkThemeMain
                )
            }
        ),
        PagerScreen(
            title = strings.quickAccessTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.quickAccessTitle,
                    description = strings.quickAccessText,
                    imagePath = Drawables.persoFirstExample
                )
            }
        ),
        PagerScreen(
            title = strings.modifyElementsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.modifyElementsTitle,
                    description = strings.modifyElementsText,
                    imagePath = Drawables.modifyAlbum
                )
            }
        ),
        PagerScreen(
            title = strings.dynamicThemeFeatureTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.dynamicThemeFeatureTitle,
                    description = strings.dynamicThemeFeatureText,
                    imagePath = Drawables.dynamicMain
                )
            }
        ),
        PagerScreen(
            title = strings.manageFoldersTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.manageFoldersTitle,
                    description = strings.manageFoldersText,
                    imagePath = Drawables.foldersSettings
                )
            }
        ),
        PagerScreen(
            title = strings.addNewMusicsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.addNewMusicsTitle,
                    description = strings.addNewMusicsText,
                    imagePath = Drawables.addNewSongsSettings
                )
            }
        ),
        PagerScreen(
            title = strings.personalizeMainPageTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.personalizeMainPageTitle,
                    description = strings.personalizeMainPageText,
                    imagePath = Drawables.persoThirdExample
                )
            }
        ),
    )

    val pagerState = rememberPagerState(
        pageCount = { pagerScreens.size }
    )

    val coroutineScope = rememberCoroutineScope()

    when (SoulSearchingContext.orientation) {
        ScreenOrientation.HORIZONTAL -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = UiConstants.Spacing.small)
                    .composed {
                        modifier
                    },
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
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    pagerScreens.forEachIndexed { index, _ ->
                        AppFeatureDotComposable(
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
                modifier = Modifier
                    .padding(bottom = UiConstants.Spacing.small)
                    .composed {
                        modifier
                    },
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
                        AppFeatureDotComposable(
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
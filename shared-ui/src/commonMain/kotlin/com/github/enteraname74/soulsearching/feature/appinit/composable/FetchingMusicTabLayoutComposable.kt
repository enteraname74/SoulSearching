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
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.*
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.perso_first_example
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.system_dark_theme_main
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun FetchingMusicTabLayoutComposable(modifier: Modifier = Modifier) {
    val pagerScreens = listOf(
        PagerScreen(
            title = strings.completeApplicationTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.completeApplicationTitle,
                    description = strings.completeApplicationText,
                    image = Res.drawable.system_dark_theme_main
                )
            }
        ),
        PagerScreen(
            title = strings.quickAccessTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.quickAccessTitle,
                    description = strings.quickAccessText,
                    image = Res.drawable.perso_first_example
                )
            }
        ),
        PagerScreen(
            title = strings.modifyElementsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.modifyElementsTitle,
                    description = strings.modifyElementsText,
                    image = Res.drawable.modify_album,
                )
            }
        ),
        PagerScreen(
            title = strings.dynamicThemeFeatureTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.dynamicThemeFeatureTitle,
                    description = strings.dynamicThemeFeatureText,
                    image = Res.drawable.dynamic_main,
                )
            }
        ),
        PagerScreen(
            title = strings.manageFoldersTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.manageFoldersTitle,
                    description = strings.manageFoldersText,
                    image = Res.drawable.folders_settings,
                )
            }
        ),
        PagerScreen(
            title = strings.addNewMusicsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.addNewMusicsTitle,
                    description = strings.addNewMusicsText,
                    image = Res.drawable.add_new_songs_settings,
                )
            }
        ),
        PagerScreen(
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
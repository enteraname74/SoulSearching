package com.github.soulsearching.composables.appfeatures

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
import com.github.soulsearching.Constants
import com.github.soulsearching.Drawables
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.composables.AppFeatureDotComposable
import com.github.soulsearching.model.TabRowItem
import com.github.soulsearching.strings
import com.github.soulsearching.types.ScreenOrientation
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FetchingMusicTabLayoutComposable(modifier: Modifier = Modifier) {
    val tabRowItems = listOf(
        TabRowItem(
            title = strings.completeApplicationTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.completeApplicationTitle,
                    description = strings.completeApplicationText,
                    imagePath = Drawables.systemDarkThemeMain
                )
            }
        ),
        TabRowItem(
            title = strings.quickAccessTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.quickAccessTitle,
                    description = strings.quickAccessText,
                    imagePath = Drawables.persoFirstExample
                )
            }
        ),
        TabRowItem(
            title = strings.modifyElementsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.modifyElementsTitle,
                    description = strings.modifyElementsText,
                    imagePath = Drawables.modifyAlbum
                )
            }
        ),
        TabRowItem(
            title = strings.dynamicThemeFeatureTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.dynamicThemeFeatureTitle,
                    description = strings.dynamicThemeFeatureText,
                    imagePath = Drawables.dynamicMain
                )
            }
        ),
        TabRowItem(
            title = strings.manageFoldersTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.manageFoldersTitle,
                    description = strings.manageFoldersText,
                    imagePath = Drawables.foldersSettings
                )
            }
        ),
        TabRowItem(
            title = strings.addNewMusicsTitle,
            screen = {
                AppFeatureComposable(
                    title = strings.addNewMusicsTitle,
                    description = strings.addNewMusicsText,
                    imagePath = Drawables.addNewSongsSettings
                )
            }
        ),
        TabRowItem(
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
        pageCount = { tabRowItems.size }
    )

    val coroutineScope = rememberCoroutineScope()

    when (SoulSearchingContext.orientation) {
        ScreenOrientation.HORIZONTAL -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = Constants.Spacing.small)
                    .composed {
                        modifier
                    },
                horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
            ) {
                VerticalPager(
                    modifier = Modifier.weight(1F),
                    state = pagerState,
                    pageSpacing = Constants.Spacing.veryLarge,
                    contentPadding = PaddingValues(Constants.Spacing.veryLarge)
                ) {
                    tabRowItems[it].screen()
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    tabRowItems.forEachIndexed { index, _ ->
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
                    .padding(bottom = Constants.Spacing.small)
                    .composed {
                        modifier
                    },
                verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
            ) {
                HorizontalPager(
                    modifier = Modifier.weight(1F),
                    state = pagerState,
                    pageSpacing = Constants.Spacing.veryLarge,
                    contentPadding = PaddingValues(Constants.Spacing.veryLarge)
                ) {
                    tabRowItems[it].screen()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabRowItems.forEachIndexed { index, _ ->
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
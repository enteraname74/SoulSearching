package com.github.soulsearching.composables

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.model.TabRowItem
import com.github.soulsearching.states.*
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun FetchingMusicTabLayoutComposable(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {

    val tabRowItems = listOf(
        TabRowItem(
            title = stringResource(id = R.string.complete_application_title),
            screen = {
                AppFeatureComposable(
                    title = stringResource(id = R.string.complete_application_title),
                    description = stringResource(id = R.string.complete_application_text),
                    imageId = R.drawable.system_dark_theme_main
                )
            }
        ),
        TabRowItem(
            title = stringResource(id = R.string.quick_access_title),
            screen = {
                AppFeatureComposable(
                    title = stringResource(id = R.string.quick_access_title),
                    description = stringResource(id = R.string.quick_access_text),
                    imageId = R.drawable.perso_first_example
                )
            }
        ),
        TabRowItem(
            title = stringResource(id = R.string.modify_elements_title),
            screen = {
                AppFeatureComposable(
                    title = stringResource(id = R.string.modify_elements_title),
                    description = stringResource(id = R.string.modify_elements_text),
                    imageId = R.drawable.modify_album
                )
            }
        ),
        TabRowItem(
            title = stringResource(id = R.string.dynamic_theme_feature_title),
            screen = {
                AppFeatureComposable(
                    title = stringResource(id = R.string.dynamic_theme_feature_title),
                    description = stringResource(id = R.string.dynamic_theme_feature_text),
                    imageId = R.drawable.dynamic_main
                )
            }
        ),
        TabRowItem(
            title = stringResource(id = R.string.manage_folders_title),
            screen = {
                AppFeatureComposable(
                    title = stringResource(id = R.string.manage_folders_title),
                    description = stringResource(id = R.string.manage_folders_text),
                    imageId = R.drawable.folders_settings
                )
            }
        ),
        TabRowItem(
            title = stringResource(id = R.string.add_new_musics_title),
            screen = {
                AppFeatureComposable(
                    title = stringResource(id = R.string.add_new_musics_title),
                    description = stringResource(id = R.string.add_new_musics_text),
                    imageId = R.drawable.add_new_songs_settings
                )
            }
        ),
        TabRowItem(
            title = stringResource(id = R.string.personalize_main_page_title),
            screen = {
                AppFeatureComposable(
                    title = stringResource(id = R.string.personalize_main_page_title),
                    description = stringResource(id = R.string.personalize_main_page_text),
                    imageId = R.drawable.perso_third_example
                )
            }
        ),
    )

    val coroutineScope = rememberCoroutineScope()

    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
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
                    count = tabRowItems.size,
                    state = pagerState,
                    itemSpacing = Constants.Spacing.veryLarge,
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
                    count = tabRowItems.size,
                    state = pagerState,
                    itemSpacing = Constants.Spacing.veryLarge,
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
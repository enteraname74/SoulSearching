package com.github.enteraname74.soulsearching.feature.onboarding.appinitsongfetching.composable

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
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.domain.model.TabData
import kotlinx.coroutines.launch
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun FetchingMusicTabLayoutComposable(
    modifier: Modifier = Modifier
) {
    val pagerScreens = listOf(
        TabData(
            title = CoreRes.string.complete_application_title.toTextSpec(),
            screen = {
                AppFeatureComposable(
                    title = CoreRes.string.complete_application_title.toTextSpec(),
                    description = CoreRes.string.complete_application_text.toTextSpec(),
                    image = Res.drawable.system_dark_theme_main
                )
            }
        ),
        TabData(
            title = CoreRes.string.quick_access_title.toTextSpec(),
            screen = {
                AppFeatureComposable(
                    title = CoreRes.string.quick_access_title.toTextSpec(),
                    description = CoreRes.string.quick_access_text.toTextSpec(),
                    image = Res.drawable.perso_first_example
                )
            }
        ),
        TabData(
            title = CoreRes.string.modify_elements_title.toTextSpec(),
            screen = {
                AppFeatureComposable(
                    title = CoreRes.string.modify_elements_title.toTextSpec(),
                    description = CoreRes.string.modify_elements_text.toTextSpec(),
                    image = Res.drawable.modify_album,
                )
            }
        ),
        TabData(
            title = CoreRes.string.dynamic_theme_feature_title.toTextSpec(),
            screen = {
                AppFeatureComposable(
                    title = CoreRes.string.dynamic_theme_feature_title.toTextSpec(),
                    description = CoreRes.string.dynamic_theme_feature_text.toTextSpec(),
                    image = Res.drawable.dynamic_main,
                )
            }
        ),
        TabData(
            title = CoreRes.string.manage_folders_title.toTextSpec(),
            screen = {
                AppFeatureComposable(
                    title = CoreRes.string.manage_folders_title.toTextSpec(),
                    description = CoreRes.string.manage_folders_text.toTextSpec(),
                    image = Res.drawable.folders_settings,
                )
            }
        ),
        TabData(
            title = CoreRes.string.add_new_musics_title.toTextSpec(),
            screen = {
                AppFeatureComposable(
                    title = CoreRes.string.add_new_musics_title.toTextSpec(),
                    description = CoreRes.string.add_new_musics_text.toTextSpec(),
                    image = Res.drawable.add_new_songs_settings,
                )
            }
        ),
        TabData(
            title = CoreRes.string.personalize_main_page_title.toTextSpec(),
            screen = {
                AppFeatureComposable(
                    title = CoreRes.string.personalize_main_page_title.toTextSpec(),
                    description = CoreRes.string.personalize_main_page_text.toTextSpec(),
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

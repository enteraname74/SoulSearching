package com.github.enteraname74.soulsearching.feature.appinit.songfetching

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.github.enteraname74.soulsearching.composables.ProgressIndicatorComposable
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.isDark
import com.github.enteraname74.soulsearching.coreui.image.SoulSearchingLogo
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.appinit.composable.FetchingMusicTabLayoutComposable
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.state.AppInitSongFetchingNavigationState
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.state.AppInitSongFetchingState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceScreen

class AppInitSongFetchingScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel: AppInitSongFetchingViewModel = koinScreenModel()
        val state: AppInitSongFetchingState by screenModel.state.collectAsState()
        val navigationState: AppInitSongFetchingNavigationState by screenModel.navigationState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchInit {
            screenModel.fetchSongs()
        }

        LaunchedEffect(navigationState) {
            when (navigationState) {
                AppInitSongFetchingNavigationState.Idle -> {
                    /*no-op*/
                }
                AppInitSongFetchingNavigationState.ToMultipleArtists -> {
                    navigator.replaceAll(MultipleArtistsChoiceScreen(emptyList()))
                    screenModel.consumeNavigation()
                }
            }
        }

        FetchingMusicsComposable(state = state)
    }


    @Composable
    private fun FetchingMusicsComposable(
        state: AppInitSongFetchingState,
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = state.currentProgression,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            label = "PROGRESS_BAR_FETCHING_MUSICS_COMPOSABLE"
        )

        SoulSearchingContext.setSystemBarsColor(
            statusBarColor = Color.Transparent,
            navigationBarColor = Color.Transparent,
            isUsingDarkIcons = !SoulSearchingColorTheme.colorScheme.primary.isDark()
        )

        val windowSize = rememberWindowSize()
        SoulScreen(
            modifier = Modifier
                .navigationBarsPadding()
                .statusBarsPadding()
        ) {
            when {
                windowSize != WindowSize.Small -> {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = UiConstants.Spacing.medium)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1F),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f),
                                contentAlignment = Alignment.BottomCenter,
                            ) {
                                SoulSearchingLogo()
                            }
                            ProgressIndicatorComposable(
                                modifier = Modifier
                                    .weight(1f),
                                progress = animatedProgress,
                                progressMessage = strings.searchingSongsFromYourDevice,
                                subText = state.currentFolder,
                            )
                        }
                        FetchingMusicTabLayoutComposable(
                            modifier = Modifier
                                .weight(1F)
                                .padding(end = UiConstants.Spacing.large)
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = UiConstants.Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SoulSearchingLogo()
                        ProgressIndicatorComposable(
                            progress = animatedProgress,
                            progressMessage = strings.searchingSongsFromYourDevice,
                            subText = state.currentFolder,
                        )
                        FetchingMusicTabLayoutComposable()
                    }
                }
            }
        }
    }
}

@Composable
fun AppInitSongFetchingFeature() {
    Navigator(
        screen = AppInitSongFetchingScreen(),
        onBackPressed = { false }
    ) { navigator ->
        SlideTransition(
            navigator = navigator,
        ) { screen ->
            screen.Content()
        }
    }
}
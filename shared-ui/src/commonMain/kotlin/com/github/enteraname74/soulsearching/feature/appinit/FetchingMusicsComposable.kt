package com.github.enteraname74.soulsearching.feature.appinit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.composables.ProgressIndicatorComposable
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.isDark
import com.github.enteraname74.soulsearching.coreui.image.SoulSearchingLogo
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.appinit.composable.FetchingMusicTabLayoutComposable
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FetchingMusicsComposable(
    mainPageViewModel: MainPageViewModel
) {
    var isFetchingMusics by rememberSaveable {
        mutableStateOf(false)
    }
    var progress by rememberSaveable {
        mutableFloatStateOf(0F)
    }

    var currentFolder: String? by rememberSaveable {
        mutableStateOf(null)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
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
                        SoulSearchingLogo()
                        ProgressIndicatorComposable(
                            progress = animatedProgress,
                            progressMessage = strings.searchingSongsFromYourDevice,
                            subText = currentFolder,
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
                        subText = currentFolder,
                    )
                    FetchingMusicTabLayoutComposable()
                }
            }
        }
    }

    if (!isFetchingMusics) {
        LaunchedEffect(key1 = "FetchingMusics") {
            CoroutineScope(Dispatchers.IO).launch {
                isFetchingMusics = true
                mainPageViewModel.fetchMusics(
                    updateProgress = { progression, folder ->
                        progress = progression
                        currentFolder = folder
                    }
                )
            }
        }
    }
}
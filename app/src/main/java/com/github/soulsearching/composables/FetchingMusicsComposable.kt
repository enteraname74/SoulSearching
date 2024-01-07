package com.github.soulsearching.composables

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.AllMusicsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FetchingMusicsComposable(
    finishAddingMusicsAction: () -> Unit,
    allMusicsViewModel: AllMusicsViewModel
) {
    var isFetchingMusics by rememberSaveable {
        mutableStateOf(false)
    }
    var progress by rememberSaveable {
        mutableFloatStateOf(0F)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "PROGRESS_BAR_FETCHING_MUSICS_COMPOSABLE"
    )

    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val pagerState = rememberPagerState()

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DynamicColor.primary)
            ) {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SoulSearchingLogo()
                    ProgressIndicatorComposable(
                        progress = animatedProgress,
                        progressMessage = stringResource(id = R.string.searching_songs_from_your_device)
                    )
                }
                FetchingMusicTabLayoutComposable(
                    modifier = Modifier.weight(1F),
                    pagerState = pagerState
                )
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DynamicColor.primary)
                    .padding(top = Constants.Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SoulSearchingLogo()
                ProgressIndicatorComposable(
                    progress = animatedProgress,
                    progressMessage = stringResource(id = R.string.searching_songs_from_your_device)
                )
                FetchingMusicTabLayoutComposable(
                    pagerState = pagerState
                )
            }
        }
    }

    if (!isFetchingMusics) {
        LaunchedEffect(key1 = "FetchingMusics") {
            CoroutineScope(Dispatchers.IO).launch {
                isFetchingMusics = true
                allMusicsViewModel.fetchMusics(
                    context = context,
                    updateProgress = {
                        progress = it
                    },
                    finishAction = finishAddingMusicsAction
                )
            }
        }
    }
}
package com.github.soulsearching.composables

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.utils.MusicUtils
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.ui.theme.DynamicColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FetchingMusicsComposable(
    finishAddingMusicsAction: () -> Unit,
    addingMusicAction: (Music, Bitmap?) -> Unit,
    createFavoritePlaylistAction: () -> Unit
) {
    var isFetchingMusics by rememberSaveable {
        mutableStateOf(false)
    }
    var progress by rememberSaveable {
        mutableStateOf(0F)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
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
                MusicUtils.fetchMusics(
                    context = context,
                    updateProgress = {
                        progress = it
                    },
                    addingMusicAction = addingMusicAction,
                    finishAction = finishAddingMusicsAction
                )
                createFavoritePlaylistAction()
            }
        }
    }
}
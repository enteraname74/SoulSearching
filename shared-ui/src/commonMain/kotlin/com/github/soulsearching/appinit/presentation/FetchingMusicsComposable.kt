package com.github.soulsearching.appinit.presentation

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
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.composables.ProgressIndicatorComposable
import com.github.soulsearching.composables.SoulSearchingLogo
import com.github.soulsearching.strings.strings
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.appinit.presentation.composable.FetchingMusicTabLayoutComposable
import com.github.soulsearching.domain.model.types.ScreenOrientation
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    when (SoulSearchingContext.orientation) {
        ScreenOrientation.HORIZONTAL -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
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
                        progressMessage = strings.searchingSongsFromYourDevice
                    )
                }
                FetchingMusicTabLayoutComposable(
                    modifier = Modifier.weight(1F)
                )
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
                    .padding(top = Constants.Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SoulSearchingLogo()
                ProgressIndicatorComposable(
                    progress = animatedProgress,
                    progressMessage = strings.searchingSongsFromYourDevice
                )
                FetchingMusicTabLayoutComposable()
            }
        }
    }

    if (!isFetchingMusics) {
        LaunchedEffect(key1 = "FetchingMusics") {
            CoroutineScope(Dispatchers.IO).launch {
                isFetchingMusics = true
                allMusicsViewModel.handler.fetchMusics(
                    updateProgress = {
                        progress = it
                    },
                    finishAction = finishAddingMusicsAction
                )
            }
        }
    }
}
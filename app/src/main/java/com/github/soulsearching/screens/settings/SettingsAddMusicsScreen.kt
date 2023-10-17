package com.github.soulsearching.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.settings.FetchingNewMusicsComposable
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.AddMusicsViewModel

@Composable
fun SettingsAddMusicsScreen(
    addMusicsViewModel: AddMusicsViewModel,
    finishAction: () -> Unit
) {
    val addMusicsState by addMusicsViewModel.state.collectAsState()
    val context = LocalContext.current

    BackHandler {
        finishAction()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.add_musics_title),
            leftAction = finishAction
        )
        if (addMusicsState.isFetchingMusics) {
            var progress by rememberSaveable {
                mutableStateOf(0F)
            }
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )
            FetchingNewMusicsComposable(progressIndicator = animatedProgress)
            LaunchedEffect(key1 = "FetchingMusics") {
                addMusicsViewModel.fetchNewMusics(context) { progress = it }
            }
        } else {
            if (addMusicsState.fetchedMusics.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_new_musics),
                        color = DynamicColor.onPrimary
                    )
                }
            } else {
                LazyColumn {
                    items(addMusicsState.fetchedMusics) {
                        MusicItemComposable(
                            musicCover = it.cover?.cover,
                            music = it.music,
                            onClick = {},
                            onLongClick = {}
                        )
                    }
                }
            }
        }
    }
}
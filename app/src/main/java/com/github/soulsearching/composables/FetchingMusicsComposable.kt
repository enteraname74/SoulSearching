package com.github.soulsearching.composables

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.MusicUtils
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.ui.theme.DynamicColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DynamicColor.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SoulSearchingLogo()
                ProgressIndicatorComposable(
                    progress = animatedProgress
                )
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DynamicColor.primary)
                    .padding(top = Constants.Spacing.large),
            ) {
                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    SoulSearchingLogo()
                }
                Box(modifier = Modifier.align(Alignment.Center)) {
                    ProgressIndicatorComposable(
                        progress = animatedProgress
                    )
                }
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
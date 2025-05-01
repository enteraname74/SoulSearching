package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.composable.LoadingComposable

@Composable
fun SettingsAddMusicsSavingScreen(
    progress: Float,
) {
    // We disable back handler:
    SoulBackHandler{
        /*no-op*/
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        SoulTopBar(
            title = strings.addMusicsTitle,
            leftAction = null,
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                label = "ANIMATED_PROGRESS_SAVING_MUSICS_SETTINGS_ADD_MUSICS_VIEW"
            )
            LoadingComposable(
                progressIndicator = animatedProgress,
                progressMessage = strings.savingNewMusics,
                subText = null,
            )
        }
    }
}
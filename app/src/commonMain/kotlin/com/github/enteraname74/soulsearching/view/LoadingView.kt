package com.github.enteraname74.soulsearching.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulCircularProgressIndicator
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
fun LoadingView(
    loadingManager: LoadingManager = injectElement(),
) {
    val state: Boolean by loadingManager.state.collectAsState()

    AnimatedVisibility(
        visible = state,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = UiConstants.AnimationDuration.short
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = UiConstants.AnimationDuration.short
            )
        ),
    ) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            )
        ) {
            FullScreenLoadingModifier()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .disableFocus(),
                contentAlignment = Alignment.Center,
            ) {
                SoulCircularProgressIndicator()
            }
        }
    }
}

@Composable
internal expect fun FullScreenLoadingModifier()

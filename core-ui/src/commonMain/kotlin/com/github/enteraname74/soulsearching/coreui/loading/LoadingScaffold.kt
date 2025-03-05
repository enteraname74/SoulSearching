package com.github.enteraname74.soulsearching.coreui.loading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.SoulCircularProgressIndicator
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus

@Composable
fun LoadingScaffold(
    loadingManager: LoadingManager,
    content: @Composable (isLoading: Boolean) -> Unit,
) {
    val state: Boolean by loadingManager.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        content(state)
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
            LoadingView()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BottomSheetDefaults.ScrimColor)
            .disableFocus(),
        contentAlignment = Alignment.Center,
    ) {
        SoulCircularProgressIndicator()
    }
}
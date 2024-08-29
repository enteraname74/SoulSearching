package com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.composables.ProgressIndicatorComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulSearchingLogo
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

@Composable
fun LoadingComposable(
    progressIndicator: Float,
    progressMessage: String
) {
    val windowSize = rememberWindowSize()
    when {
        windowSize != WindowSize.Small -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SoulSearchingLogo()
                ProgressIndicatorComposable(
                    progress = progressIndicator,
                    progressMessage = progressMessage
                )
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
                    .padding(top = UiConstants.Spacing.large),
            ) {
                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    SoulSearchingLogo()
                }
                Box(modifier = Modifier.align(Alignment.Center)) {
                    ProgressIndicatorComposable(
                        progress = progressIndicator,
                        progressMessage = progressMessage
                    )
                }
            }
        }
    }
}
package com.github.soulsearching.composables.setting

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.ProgressIndicatorComposable
import com.github.soulsearching.composables.SoulSearchingLogo
import com.github.soulsearching.theme.DynamicColor

@Composable
fun LoadingComposable(
    progressIndicator: Float,
    progressMessage: String
) {
    val configuration = LocalConfiguration.current

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
                    progress = progressIndicator,
                    progressMessage = progressMessage
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
                        progress = progressIndicator,
                        progressMessage = progressMessage
                    )
                }
            }
        }
    }
}
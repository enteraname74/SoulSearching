package com.github.soulsearching.composables.settings

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun FetchingNewMusicsComposable(
    progressIndicator: Float
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
                    progress = progressIndicator
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
                        progress = progressIndicator
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressIndicatorComposable(
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(Constants.Spacing.medium),
            text = stringResource(id = R.string.searching_songs_from_your_device),
            color = DynamicColor.onPrimary,
            textAlign = TextAlign.Center
        )
        LinearProgressIndicator(
            progress = progress,
            trackColor = DynamicColor.secondary,
            color = DynamicColor.onSecondary
        )
    }

}

@Composable
fun SoulSearchingLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.ImageSize.veryLarge),
            painter = painterResource(id = R.drawable.ic_saxophone_svg),
            contentDescription = stringResource(id = R.string.app_logo),
            colorFilter = ColorFilter.tint(color = DynamicColor.onPrimary)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            color = DynamicColor.onPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
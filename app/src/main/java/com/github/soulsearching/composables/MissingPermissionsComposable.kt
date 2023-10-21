package com.github.soulsearching.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun MissingPermissionsComposable() {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DynamicColor.primary)
                    .padding(Constants.Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SoulSearchingLogo()
                Text(
                    text = stringResource(id = R.string.missing_permissions),
                    textAlign = TextAlign.Center,
                    color = DynamicColor.onPrimary
                )
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DynamicColor.primary)
                    .padding(
                        top = Constants.Spacing.large,
                        bottom = Constants.Spacing.medium,
                        start = Constants.Spacing.medium,
                        end = Constants.Spacing.medium
                    ),
            ) {
                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    SoulSearchingLogo()
                }
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(id = R.string.missing_permissions),
                        textAlign = TextAlign.Center,
                        color = DynamicColor.onPrimary
                    )
                }
            }
        }
    }
}
package com.github.soulsearching.appinit.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.image.SoulSearchingLogo
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation

@Composable
fun MissingPermissionsComposable() {
    when (SoulSearchingContext.orientation) {
        ScreenOrientation.HORIZONTAL -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
                    .padding(UiConstants.Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SoulSearchingLogo()
                Text(
                    text = strings.missingPermissions,
                    textAlign = TextAlign.Center,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
                    .padding(
                        top = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.medium,
                        start = UiConstants.Spacing.medium,
                        end = UiConstants.Spacing.medium
                    ),
            ) {
                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    SoulSearchingLogo()
                }
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = strings.missingPermissions,
                        textAlign = TextAlign.Center,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
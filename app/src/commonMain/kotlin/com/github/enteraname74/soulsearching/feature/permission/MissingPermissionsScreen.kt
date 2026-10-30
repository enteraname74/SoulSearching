package com.github.enteraname74.soulsearching.feature.permission

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulSearchingLogo
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

@Composable
fun MissingPermissionsScreen() {
    val windowSize = rememberWindowSize()
    SoulScreen(
        modifier = Modifier
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        when {
            windowSize != WindowSize.Small -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(UiConstants.Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SoulSearchingLogo()
                    SoulText(
                        text = CoreRes.string.missing_permissions.toTextSpec(),
                        textAlign = TextAlign.Center,
                        color = SoulSearchingColorTheme.colorScheme.text
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                        SoulText(
                            text = CoreRes.string.missing_permissions.toTextSpec(),
                            textAlign = TextAlign.Center,
                            color = SoulSearchingColorTheme.colorScheme.text
                        )
                    }
                }
            }
        }
    }
}

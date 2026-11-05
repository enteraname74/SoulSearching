package com.github.enteraname74.soulsearching.coreui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulCircularProgressIndicator
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction

@Composable
fun SoulLoadingScreen(
    navigateBack: () -> Unit,
    title: SoulTextSpec? = null,
    text: SoulTextSpec? = null,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                leftAction = TopBarNavigationAction(
                    onClick = navigateBack,
                ),
                title = title,
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SoulCircularProgressIndicator()
                text?.let {
                    SoulText(
                        modifier = Modifier
                            .padding(
                                top = UiConstants.Spacing.medium,
                            ),
                        text = it,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun SoulLoadingScreen(
    text: SoulTextSpec? = null,
) {
    SoulScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SoulCircularProgressIndicator()
            text?.let {
                SoulText(
                    modifier = Modifier
                        .padding(
                            top = UiConstants.Spacing.medium,
                        ),
                    text = it,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

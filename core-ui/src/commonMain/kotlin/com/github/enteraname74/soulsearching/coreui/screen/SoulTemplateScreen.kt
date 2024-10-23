package com.github.enteraname74.soulsearching.coreui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec

@Composable
fun SoulTemplateScreen(
    leftAction: TopBarActionSpec,
    icon: ImageVector,
    text: String,
    buttonAction: () -> Unit,
    buttonText: String,
    title: String? = null,
    rightAction: TopBarActionSpec? = null,
) {
    SoulScreen(
        modifier = Modifier
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            SoulTopBar(
                title = title,
                leftAction = leftAction,
                rightAction = rightAction,
            )
            TemplateScreenContent(
                icon = icon,
                text = text,
                buttonText = buttonText,
                onClick = buttonAction,
            )
        }
    }
}

@Composable
private fun ColumnScope.TemplateScreenContent(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    buttonText: String,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SoulIcon(
                icon = icon,
                size = UiConstants.ImageSize.veryLarge,
            )
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
            SoulButton(
                onClick = onClick
            ) {
                Text(
                    text = buttonText,
                    textAlign = TextAlign.Center,
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
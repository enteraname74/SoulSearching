package com.github.enteraname74.soulsearching.coreui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec

@Composable
fun SoulTemplateScreen(
    leftAction: TopBarActionSpec,
    icon: ImageVector,
    text: String,
    buttonSpec: TemplateScreenButtonSpec?,
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
            SoulTemplateComposable(
                modifier = Modifier
                    .weight(1f),
                icon = icon,
                text = text,
                buttonSpec = buttonSpec,
            )
        }
    }
}

@Composable
fun SoulTemplateComposable(
    icon: ImageVector,
    text: String,
    iconSize: Dp = UiConstants.ImageSize.veryLarge,
    buttonSpec: TemplateScreenButtonSpec?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = UiConstants.Spacing.medium,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SoulIcon(
                icon = icon,
                size = iconSize,
            )
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
            buttonSpec?.let { spec ->
                SoulButton(
                    onClick = spec.onClick,
                    colors = spec.colors(),
                ) {
                    Text(
                        text = spec.text,
                        textAlign = TextAlign.Center,
                        color = SoulSearchingColorTheme.colorScheme.onSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

data class TemplateScreenButtonSpec(
    val text: String,
    val onClick: () -> Unit,
    val colors: @Composable () -> SoulButtonColors = { SoulButtonDefaults.secondaryColors() },
)
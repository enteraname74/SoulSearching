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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center,
            ) {
                SoulTemplateComposable(
                    icon = icon,
                    text = text,
                    buttonSpec = buttonSpec,
                )
            }
        }
    }
}

@Composable
fun SoulTemplateComposable(
    icon: ImageVector,
    text: String,
    buttonSpec: TemplateScreenButtonSpec?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
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
        buttonSpec?.let { spec ->
            SoulButton(
                onClick = spec.onClick,
                colors = spec.colors(),
                text = spec.text,
            )
        }
    }
}

data class TemplateScreenButtonSpec(
    val text: String,
    val onClick: () -> Unit,
    val colors: @Composable () -> SoulButtonColors = { SoulButtonDefaults.secondaryColors() }
)
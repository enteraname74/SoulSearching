package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec

@Composable
fun StartTopBar(
    title: String,
    rightAction: TopBarActionSpec,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            color = contentColor,
            style = UiConstants.Typography.bodyMediumTitle,
            overflow = TextOverflow.Ellipsis,
        )
        SoulIconButton(
            icon = rightAction.icon,
            contentDescription = rightAction.contentDescription,
            onClick = rightAction.onClick,
            enabled = rightAction.isEnabled,
            size = UiConstants.ImageSize.medium,
            colors = SoulButtonDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = contentColor
            ),
        )
    }
}
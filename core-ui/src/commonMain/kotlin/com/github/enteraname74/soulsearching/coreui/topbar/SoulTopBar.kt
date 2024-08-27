package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    leftAction: TopBarActionSpec,
    rightAction: TopBarActionSpec? = null,
    colors: TopBarColors = SoulTopBarDefaults.colors(),
) {
    Row(
        modifier = modifier
            .then(
                Modifier
                    .fillMaxWidth()
                    .background(colors.containerColor)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = leftAction.onClick,
            enabled = leftAction.isEnabled,
        ) {
            Icon(
                imageVector = leftAction.icon,
                contentDescription = leftAction.contentDescription,
                tint = colors.contentColor,
            )
        }
        title?.let {
            Text(
                modifier = Modifier.weight(1f),
                text = it,
                maxLines = 2,
                fontSize = 18.sp,
                color = colors.contentColor,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        if (rightAction != null) {
            IconButton(
                onClick = rightAction.onClick,
                enabled = rightAction.isEnabled,
            ) {
                Icon(
                    imageVector = rightAction.icon,
                    contentDescription = strings.headerBarRightButton,
                    tint = colors.contentColor,
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

object SoulTopBarDefaults {
    @Composable
    fun colors(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.primary,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
    ): TopBarColors =
        TopBarColors(
            contentColor = contentColor,
            containerColor = containerColor,
        )
}

data class TopBarColors(
    val containerColor: Color,
    val contentColor: Color,
)

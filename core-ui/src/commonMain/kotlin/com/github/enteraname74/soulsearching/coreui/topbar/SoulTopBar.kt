package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    leftAction: TopBarActionSpec?,
    rightAction: TopBarActionSpec? = null,
    isElevated: Boolean = false,
    colors: TopBarColors = SoulTopBarDefaults.primary(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.containerColorWithElevation(isElevated))
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (leftAction != null) {
            SoulIconButton(
                icon = leftAction.icon,
                contentDescription = leftAction.contentDescription,
                colors = SoulButtonDefaults.colors(
                    contentColor = colors.contentColorWithElevation(isElevated),
                    containerColor = Color.Transparent,
                ),
                onClick = leftAction.onClick,
                enabled = leftAction.isEnabled
            )
        } else {
            Spacer(modifier = Modifier.size(TOP_BAR_BUTTON_SIZE))
        }
        title?.let {
            Text(
                modifier = Modifier.weight(1f),
                text = it,
                maxLines = 2,
                style = UiConstants.Typography.bodyMediumTitle,
                color = colors.contentColorWithElevation(isElevated),
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        if (rightAction != null) {
            SoulIconButton(
                icon = rightAction.icon,
                contentDescription = rightAction.contentDescription,
                colors = SoulButtonDefaults.colors(
                    contentColor = colors.contentColorWithElevation(isElevated),
                    containerColor = Color.Transparent,
                ),
                onClick = rightAction.onClick,
                enabled = rightAction.isEnabled
            )
        } else {
            Spacer(modifier = Modifier.size(TOP_BAR_BUTTON_SIZE))
        }
    }
}

object SoulTopBarDefaults {
    @Composable
    fun primary(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.primary,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
        elevatedContentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
        elevatedContainerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    ): TopBarColors =
        TopBarColors(
            contentColor = contentColor,
            containerColor = containerColor,
            elevatedContainerColor = elevatedContainerColor,
            elevatedContentColor = elevatedContentColor,
        )

    @Composable
    fun secondary(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
        elevatedContentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
        elevatedContainerColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    ): TopBarColors =
        TopBarColors(
            contentColor = contentColor,
            containerColor = containerColor,
            elevatedContainerColor = elevatedContainerColor,
            elevatedContentColor = elevatedContentColor,
        )
}

fun checkTopBarElevation(lazyListState: LazyListState): Boolean =
    lazyListState.firstVisibleItemScrollOffset > 0

data class TopBarColors(
    val containerColor: Color,
    val contentColor: Color,
    val elevatedContainerColor: Color,
    val elevatedContentColor: Color,
) {
    fun contentColorWithElevation(isElevated: Boolean): Color =
        if (isElevated) elevatedContentColor else contentColor

    fun containerColorWithElevation(isElevated: Boolean): Color =
        if (isElevated) elevatedContainerColor else containerColor
}

private val TOP_BAR_BUTTON_SIZE: Dp = 48.dp

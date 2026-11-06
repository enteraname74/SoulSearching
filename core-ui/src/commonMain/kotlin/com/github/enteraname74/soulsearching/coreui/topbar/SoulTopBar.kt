package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

@Composable
fun SoulTopBar(
    modifier: Modifier = Modifier,
    title: SoulTextSpec? = null,
    showTitle: Boolean = true,
    leftAction: SoulTopBarAction?,
    rightAction: SoulTopBarAction? = null,
    withStatusBarPadding: Boolean = true,
    colors: SoulTopBarColors = SoulTopBarDefaults.primary(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .disableFocus()
            .background(colors.containerColor)
            .chainIf(withStatusBarPadding) {
                Modifier.statusBarsPadding()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (leftAction != null) {
            leftAction.content()
        } else {
            Spacer(modifier = Modifier.size(TOP_BAR_BUTTON_SIZE))
        }
        title?.let {
            AnimatedVisibility(
                modifier = Modifier.weight(1f),
                visible = showTitle,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = UiConstants.AnimationDuration.shortPlus
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = UiConstants.AnimationDuration.shortPlus
                    )
                ),
            ) {
                SoulText(
                    text = it,
                    maxLines = 2,
                    style = UiConstants.Typography.bodyMediumTitle,
                    color = colors.contentColor,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (rightAction != null) {
            rightAction.content()
        } else {
            Spacer(modifier = Modifier.size(TOP_BAR_BUTTON_SIZE))
        }
    }
}

private val TOP_BAR_BUTTON_SIZE: Dp = 48.dp

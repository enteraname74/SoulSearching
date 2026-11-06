package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.back_button
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_arrow_back
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SoulBigTopBar(
    title: SoulTextSpec,
    leftAction: SoulBigTopBarAction?,
    modifier: Modifier = Modifier,
    withStatusBarPadding: Boolean = true,
    rightAction: SoulBigTopBarAction? = null,
    subTitle: SoulTextSpec? = null,
    description: SoulTextSpec? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .disableFocus()
            .background(SoulSearchingColorTheme.colorScheme.background)
            .chainIf(withStatusBarPadding) {
                Modifier.statusBarsPadding()
            },
    ) {
        leftAction?.content()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
        ) {
            HeaderText(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = UiConstants.Spacing.large,
                        end = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.medium,
                    ),
                title = title,
                subTitle = subTitle,
                description = description,
            )
            rightAction?.content()
        }
    }
}

@Composable
private fun HeaderText(
    title: SoulTextSpec,
    subTitle: SoulTextSpec?,
    description: SoulTextSpec?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        subTitle?.let {
            SoulText(
                text = it,
                color = SoulSearchingColorTheme.colorScheme.textSecondary,
                style = UiConstants.Typography.bodySmall,
            )
        }
        SoulText(
            text = title,
            color = SoulSearchingColorTheme.colorScheme.text,
            style = if (isLargeWindow()) {
                UiConstants.Typography.title
            } else {
                UiConstants.Typography.titleSmall
            },
        )
        description?.let {
            SoulText(
                text = it,
                color = SoulSearchingColorTheme.colorScheme.textSecondary,
                style = UiConstants.Typography.bodySmall,
            )
        }
    }
}

interface SoulBigTopBarAction {
    val content: @Composable () -> Unit

    data class IconButton(
        val icon: DrawableResource,
        val contentDescription: SoulTextSpec,
        val colors: SoulButtonColors,
        val enabled: Boolean,
        val onClick: () -> Unit,
    ) : SoulBigTopBarAction {
        override val content: @Composable (() -> Unit) = {
            SoulIconButton(
                icon = icon,
                contentDescription = contentDescription.string,
                colors = colors,
                onClick = onClick,
                enabled = enabled,
            )
        }
    }

    companion object {
        @Composable
        fun navigation(
            colors: SoulButtonColors = SoulButtonDefaults.primaryTint(),
            enabled: Boolean = true,
            onClick: () -> Unit,
        ): IconButton =
            IconButton(
                icon = CoreRes.drawable.ic_arrow_back,
                contentDescription = CoreRes.string.back_button.toTextSpec(),
                colors = colors,
                enabled = enabled,
                onClick = onClick,
            )
    }
}
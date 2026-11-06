package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow

@Composable
fun SoulBigTopBar(
    title: SoulTextSpec?,
    leftAction: SoulTopBarAction?,
    modifier: Modifier = Modifier,
    withStatusBarPadding: Boolean = true,
    rightAction: SoulTopBarAction? = null,
    subTitle: SoulTextSpec? = null,
    description: SoulTextSpec? = null,
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
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            leftAction?.content()
            HeaderText(
                modifier = Modifier
                    .padding(
                        start = UiConstants.Spacing.large,
                        end = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.medium,
                    ),
                title = title,
                subTitle = subTitle,
                description = description,
                colors = colors,
            )
        }
        rightAction?.content()
    }
}

@Composable
private fun HeaderText(
    title: SoulTextSpec?,
    subTitle: SoulTextSpec?,
    description: SoulTextSpec?,
    modifier: Modifier = Modifier,
    colors: SoulTopBarColors,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        subTitle?.let {
            SoulText(
                text = it,
                color = colors.secondaryContent,
                style = UiConstants.Typography.bodySmall,
            )
        }
        title?.let {
            SoulText(
                text = title,
                color = colors.contentColor,
                style = if (isLargeWindow()) {
                    UiConstants.Typography.title
                } else {
                    UiConstants.Typography.titleSmall
                },
            )
        }
        description?.let {
            SoulText(
                text = it,
                color = colors.secondaryContent,
                style = UiConstants.Typography.bodySmall,
            )
        }
    }
}
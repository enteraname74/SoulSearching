package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

@Composable
fun SoulAdaptiveTopBar(
    title: SoulTextSpec?,
    leftAction: SoulTopBarAction?,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
    withStatusBarPadding: Boolean = true,
    rightAction: SoulTopBarAction? = null,
    subTitle: SoulTextSpec? = null,
    description: SoulTextSpec? = null,
    colors: SoulTopBarColors = SoulTopBarDefaults.primary(),
    bigOnWindowSize: WindowSize = WindowSize.Medium,
) {
    if (rememberWindowSize().maxValue >= bigOnWindowSize.maxValue) {
        SoulBigTopBar(
            title = title,
            leftAction = leftAction,
            modifier = modifier,
            withStatusBarPadding = withStatusBarPadding,
            rightAction = rightAction,
            subTitle = subTitle,
            description = description,
            colors = colors,
        )
    } else {
        SoulTopBar(
            modifier = modifier,
            title = title,
            showTitle = showTitle,
            leftAction = leftAction,
            rightAction = rightAction,
            withStatusBarPadding = withStatusBarPadding,
            colors = colors
        )
    }
}
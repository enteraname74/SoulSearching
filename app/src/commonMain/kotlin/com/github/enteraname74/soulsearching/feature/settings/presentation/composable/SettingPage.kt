package com.github.enteraname74.soulsearching.feature.settings.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.topbar.SoulAdaptiveTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarAction

@Composable
fun SettingPage(
    navigateBack: () -> Unit,
    title: SoulTextSpec,
    modifier: Modifier = Modifier,
    subTitle: SoulTextSpec? = null,
    description: SoulTextSpec? = null,
    verticalPadding: Dp = 0.dp,
    rightAction: SoulTopBarAction? = null,
    contentPadding: PaddingValues = PaddingValues(
        all = 0.dp
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit
) {
    SoulScreen(
        modifier = modifier,
    ) {
        Column {
            SoulAdaptiveTopBar(
                title = title,
                subTitle = subTitle,
                description = description,
                leftAction = SoulTopBarAction.navigation(
                    onClick = navigateBack,
                ),
                rightAction = rightAction,
            )
            LazyColumnCompat(
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(verticalPadding),
                horizontalAlignment = horizontalAlignment,
            ) {
                content()
                item {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}

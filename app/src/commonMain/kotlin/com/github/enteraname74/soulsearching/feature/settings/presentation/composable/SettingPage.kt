package com.github.enteraname74.soulsearching.feature.settings.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction

@Composable
fun SettingPage(
    navigateBack: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(
        all = 0.dp
    ),
    content: LazyListScope.() -> Unit
) {
    SoulScreen(
        modifier = modifier,
    ) {
        Column {
            SoulTopBar(
                title = title,
                leftAction = TopBarNavigationAction(
                    onClick = navigateBack,
                )
            )
            LazyColumnCompat(
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(verticalPadding),
            ) {
                content()
                item {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}
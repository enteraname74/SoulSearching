package com.github.enteraname74.soulsearching.feature.settings.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction

@Composable
fun SettingPage(
    navigateBack: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = UiConstants.Spacing.veryHuge,
    contentPadding: PaddingValues = PaddingValues(
        all = UiConstants.Spacing.veryLarge
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
            LazyColumn(
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
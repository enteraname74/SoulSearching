package com.github.enteraname74.soulsearching.feature.settings.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.checkTopBarElevation

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

    val lazyListState = rememberLazyListState()

    SoulScreen(
        modifier = modifier,
    ) {
        Column {
            SoulTopBar(
                title = title,
                leftAction = TopBarNavigationAction(
                    onClick = navigateBack,
                ),
                isElevated = checkTopBarElevation(lazyListState = lazyListState),
            )
            LazyColumn(
                state = lazyListState,
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
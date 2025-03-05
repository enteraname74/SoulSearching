package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun NavigationPanel(
    rows: List<NavigationRowSpec>,
) {
    Column(
        modifier = Modifier
            .background(color = SoulSearchingColorTheme.colorScheme.secondary)
            .fillMaxHeight()
            .statusBarsPadding()
            .width(PanelWidth)
    ) {
        LazyColumnCompat {
            items(
                key = { it.title },
                contentType = { NAVIGATION_CONTENT_TYPE },
                items = rows
            ) { row ->
                NavigationRow(navigationRowSpec = row)
            }
        }
    }
}

private val PanelWidth: Dp = 300.dp
private const val NAVIGATION_CONTENT_TYPE = "NAVIGATION_CONTENT_TYPE"
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
import androidx.compose.ui.zIndex
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.settings.SettingPage

@Composable
fun NavigationPanel(
    currentRoute: NavKey?,
    rows: List<NavigationRowSpec>,
) {
    Column(
        modifier = Modifier
            .zIndex(1f)
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
                NavigationRow(
                    navigationRowSpec = row,
                    isSelected = isRowSelected(
                        row = row,
                        currentRoute = currentRoute,
                    )
                )
            }
        }
    }
}

private fun isRowSelected(
    row: NavigationRowSpec,
    currentRoute: NavKey?,
): Boolean =
    when (row) {
        is NavigationRowSpec.MainTabElement -> row.isCurrentPage && currentRoute is MainPageDestination
        is NavigationRowSpec.Settings -> currentRoute is SettingPage
    }

private val PanelWidth: Dp = 300.dp
private const val NAVIGATION_CONTENT_TYPE = "NAVIGATION_CONTENT_TYPE"

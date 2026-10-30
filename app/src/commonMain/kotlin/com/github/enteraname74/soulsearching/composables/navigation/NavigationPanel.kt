package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.app_name
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.search.all.SearchAllDestination
import com.github.enteraname74.soulsearching.feature.settings.SettingPage

@Composable
fun NavigationPanel(
    currentRoute: NavKey?,
    rows: List<NavigationRowSpec>,
) {
    Column(
        modifier = Modifier
            .zIndex(1f)
            .background(color = SoulSearchingColorTheme.colorScheme.surfaceLow)
            .fillMaxHeight()
            .statusBarsPadding()
            .width(NavigationPanelUiUtils.PanelWidth)
    ) {
        LazyColumnCompat {
            item {
                SoulText(
                    modifier = Modifier
                        .padding(
                            top = UiConstants.Spacing.mediumPlus,
                            start = UiConstants.Spacing.large,
                            end = UiConstants.Spacing.large,
                            bottom = UiConstants.Spacing.large,
                        ),
                    text = CoreRes.string.app_name.toTextSpec(),
                    color = SoulSearchingColorTheme.colorScheme.text,
                    style = UiConstants.Typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
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
        is NavigationRowSpec.Search -> false
    }

private const val NAVIGATION_CONTENT_TYPE = "NAVIGATION_CONTENT_TYPE"

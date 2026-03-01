package com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingDarkLightThemes
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingTheme
import com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.domain.SettingsThemeSelectionViewModel
import com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.presentation.composable.SettingsThemeItem
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.theme.DefaultThemeSettings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsThemeSelectionRoute(
    navigateBack: () -> Unit,
    navigateToSeed: () -> Unit,
) {
    val viewModel: SettingsThemeSelectionViewModel = koinViewModel()
    val defaultThemeSettings: DefaultThemeSettings by viewModel.state.collectAsState()

    DataScreen(
        defaultThemeSettings = defaultThemeSettings,
        navigateBack = navigateBack,
        onThemeSelected = viewModel::selectTheme,
        setForceLightTheme = viewModel::forceLightTheme,
        setForceDarkTheme = viewModel::forceDarkTheme,
        navigateToSeed = navigateToSeed,
    )
}

@Composable
private fun DataScreen(
    defaultThemeSettings: DefaultThemeSettings,
    navigateBack: () -> Unit,
    navigateToSeed: () -> Unit,
    setForceLightTheme: (Boolean) -> Unit,
    setForceDarkTheme: (Boolean) -> Unit,
    onThemeSelected: (themeId: SoulSearchingTheme) -> Unit,
) {
    SettingPage(
        navigateBack = navigateBack,
        title = strings.themeSelectionTitle
    ) {
        item {
            SoulMenuElement(
                title = strings.extractedColorTitle,
                subTitle = strings.extractedColorText,
                leadIcon = Icons.Rounded.Colorize,
                onClick = navigateToSeed,
            )
        }
        item {
            SoulMenuSwitch(
                title = strings.forceLightThemeTitle,
                isChecked = defaultThemeSettings.forceLightTheme,
                toggleAction = {
                    setForceLightTheme(!defaultThemeSettings.forceLightTheme)
                },
                padding = PaddingValues(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.medium,
                )
            )
        }
        item {
            SoulMenuSwitch(
                title = strings.forceDarkThemeTitle,
                isChecked = defaultThemeSettings.forceDarkTheme,
                toggleAction = {
                    setForceDarkTheme(!defaultThemeSettings.forceDarkTheme)
                },
                padding = PaddingValues(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.medium,
                )
            )
        }
        items(
            key = { it.id },
            contentType = { THEME_CONTENT_TYPE },
            items = SoulSearchingDarkLightThemes.themes,
        ) { theme ->
            SettingsThemeItem(
                theme = theme,
                isSelected = theme.id == defaultThemeSettings.themeId,
                onClick = { onThemeSelected(theme.id) }
            )
        }
    }
}

private val THEME_CONTENT_TYPE = "THEME_CONTENT_TYPE"
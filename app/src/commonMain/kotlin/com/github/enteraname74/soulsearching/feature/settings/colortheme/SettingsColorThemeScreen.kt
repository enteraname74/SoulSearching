package com.github.enteraname74.soulsearching.feature.settings.colortheme

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_format_paint_filled
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeType
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.ColorCard
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.PersonalizedColorThemeCard
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.theme.ColorThemeSettings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsColorThemeRoute(
    navigateBack: () -> Unit,
    toThemeSelection: () -> Unit,
) {
    val viewModel: SettingsColorThemeViewModel = koinViewModel()
    val state: SettingsColorThemeState by viewModel.colorThemeSettingsState.collectAsState()

    SettingsColorThemeScreenView(
        state = state,
        updateColorTheme = viewModel::updateColorTheme,
        navigateBack = navigateBack,
        navigateToThemeSelection = toThemeSelection,
        togglePersonalizedDynamicPlayerTheme = viewModel::togglePersonalizedDynamicPlayerTheme,
        togglePersonalizedDynamicPlaylistTheme = viewModel::togglePersonalizedDynamicPlaylistTheme,
        togglePersonalizedDynamicOtherViewsTheme = viewModel::togglePersonalizedDynamicOtherViewsTheme,
    )
}

@Composable
fun SettingsColorThemeScreenView(
    navigateBack: () -> Unit,
    state: SettingsColorThemeState,
    updateColorTheme: (Int) -> Unit,
    togglePersonalizedDynamicPlayerTheme: () -> Unit,
    togglePersonalizedDynamicPlaylistTheme: () -> Unit,
    togglePersonalizedDynamicOtherViewsTheme: () -> Unit,
    navigateToThemeSelection: () -> Unit,
) {

    SettingPage(
        navigateBack = navigateBack,
        title = strings.colorThemeTitle,
        verticalPadding = UiConstants.Spacing.medium,
    ) {
        item {
            SoulMenuElement(
                title = strings.themeSelectionTitle,
                subTitle = strings.themeSelectionText,
                leadIcon = CoreRes.drawable.ic_format_paint_filled,
                onClick = navigateToThemeSelection,
            )
        }
        item {
            ColorCard(
                modifier = Modifier
                    .padding(
                        start = UiConstants.Spacing.large,
                        end = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.mediumPlus,
                    ),
                title = strings.systemThemeTitle,
                text = strings.systemThemeText,
                onClick = { updateColorTheme(ColorThemeType.SYSTEM) },
                isSelected = state.colorThemeSettings is ColorThemeSettings.FromSystem,
            )
        }
        item {
            ColorCard(
                modifier = Modifier
                    .padding(
                        start = UiConstants.Spacing.large,
                        end = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.mediumPlus,
                    ),
                title = strings.dynamicThemeTitle,
                text = strings.dynamicThemeText,
                onClick = { updateColorTheme(ColorThemeType.DYNAMIC) },
                isSelected = state.colorThemeSettings is ColorThemeSettings.DynamicTheme,
            )
        }
        item {
            PersonalizedColorThemeCard(
                modifier = Modifier
                    .padding(
                        horizontal = UiConstants.Spacing.large,
                    ),
                onClick = { updateColorTheme(ColorThemeType.PERSONALIZED) },
                isSelected = state.colorThemeSettings is ColorThemeSettings.Personalized,
                hasPlayerTheme = state.hasPersonalizedDynamicPlayerTheme,
                hasPlaylistTheme = state.hasPersonalizedDynamicPlaylistTheme,
                hasOtherViewsTheme = state.hasPersonalizedDynamicOtherTheme,
                togglePersonalizedDynamicPlayerTheme = togglePersonalizedDynamicPlayerTheme,
                togglePersonalizedDynamicPlaylistTheme = togglePersonalizedDynamicPlaylistTheme,
                togglePersonalizedDynamicOtherViewsTheme = togglePersonalizedDynamicOtherViewsTheme
            )
        }
    }
}

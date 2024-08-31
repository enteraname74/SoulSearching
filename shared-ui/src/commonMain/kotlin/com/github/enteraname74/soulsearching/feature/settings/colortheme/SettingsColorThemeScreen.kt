package com.github.enteraname74.soulsearching.feature.settings.colortheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeType
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.ColorThemeCard
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.PersonalizedColorThemeCard
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.*
import com.github.enteraname74.soulsearching.theme.ColorThemeSettings
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Represent the view of the color theme screen.
 */
class SettingsColorThemeScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SettingsColorThemeViewModel = getScreenModel()
        val state: SettingsColorThemeState by screenModel.colorThemeSettingsState.collectAsState()

        SettingsColorThemeScreenView(
            state = state,
            updateColorTheme = screenModel::updateColorTheme,
            navigateBack = {
                navigator.pop()
            },
            togglePersonalizedDynamicPlayerTheme = screenModel::togglePersonalizedDynamicPlayerTheme,
            togglePersonalizedDynamicPlaylistTheme = screenModel::togglePersonalizedDynamicPlaylistTheme,
            togglePersonalizedDynamicOtherViewsTheme = screenModel::togglePersonalizedDynamicOtherViewsTheme,
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SettingsColorThemeScreenView(
    navigateBack: () -> Unit,
    state: SettingsColorThemeState,
    updateColorTheme: (Int) -> Unit,
    togglePersonalizedDynamicPlayerTheme: () -> Unit,
    togglePersonalizedDynamicPlaylistTheme: () -> Unit,
    togglePersonalizedDynamicOtherViewsTheme: () -> Unit,
) {

    SettingPage(
        navigateBack = navigateBack,
        title = strings.colorThemeTitle,
        verticalPadding = UiConstants.Spacing.veryLarge,
        contentPadding = PaddingValues(
            all = UiConstants.Spacing.veryLarge,
        )
    ) {
        item {
            ColorThemeCard(
                title = strings.dynamicThemeTitle,
                text = strings.dynamicThemeText,
                onClick = { updateColorTheme(ColorThemeType.DYNAMIC) },
                isSelected = state.colorThemeSettings is ColorThemeSettings.DynamicTheme,
                firstImage = Res.drawable.dynamic_main,
                secondImage = Res.drawable.dynamic_player,
            )
        }
        item {
            ColorThemeCard(
                title = strings.systemThemeTitle,
                text = strings.systemThemeText,
                onClick = { updateColorTheme(ColorThemeType.SYSTEM) },
                isSelected = state.colorThemeSettings is ColorThemeSettings.FromSystem,
                firstImage = if (isSystemInDarkTheme()) {
                    Res.drawable.system_dark_theme_main
                } else {
                    Res.drawable.system_light_theme_main
                },
                secondImage = if (isSystemInDarkTheme()) {
                    Res.drawable.system_dark_theme_player
                } else {
                    Res.drawable.system_light_theme_player
                }
            )
        }
        item {
            PersonalizedColorThemeCard(
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
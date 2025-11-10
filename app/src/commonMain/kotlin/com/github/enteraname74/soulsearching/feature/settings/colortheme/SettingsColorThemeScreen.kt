package com.github.enteraname74.soulsearching.feature.settings.colortheme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeType
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.ColorThemeCard
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.PersonalizedColorThemeCard
import com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.presentation.SettingsThemeSelectionScreen
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.app.generated.resources.Res
import com.github.enteraname74.soulsearching.app.generated.resources.dynamic_main
import com.github.enteraname74.soulsearching.app.generated.resources.dynamic_player
import com.github.enteraname74.soulsearching.theme.ColorThemeSettings

/**
 * Represent the view of the color theme screen.
 */
class SettingsColorThemeScreen: Screen, SettingPage {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SettingsColorThemeViewModel = koinScreenModel()
        val state: SettingsColorThemeState by screenModel.colorThemeSettingsState.collectAsState()

        SettingsColorThemeScreenView(
            state = state,
            updateColorTheme = screenModel::updateColorTheme,
            navigateBack = {
                navigator.pop()
            },
            navigateToThemeSelection = {
                navigator.safePush(
                    SettingsThemeSelectionScreen()
                )
            },
            togglePersonalizedDynamicPlayerTheme = screenModel::togglePersonalizedDynamicPlayerTheme,
            togglePersonalizedDynamicPlaylistTheme = screenModel::togglePersonalizedDynamicPlaylistTheme,
            togglePersonalizedDynamicOtherViewsTheme = screenModel::togglePersonalizedDynamicOtherViewsTheme,
        )
    }
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
        verticalPadding = UiConstants.Spacing.veryLarge,
        contentPadding = PaddingValues(
            all = UiConstants.Spacing.veryLarge,
        )
    ) {
        item {
            SoulMenuElement(
                title = strings.themeSelectionTitle,
                subTitle = strings.themeSelectionText,
                icon = Icons.Rounded.FormatPaint,
                onClick = navigateToThemeSelection,
                padding = PaddingValues(0.dp)
            )
        }
        item {
            ColorThemeCard(
                title = strings.dynamicThemeTitle,
                text = strings.dynamicThemeText,
                onClick = { updateColorTheme(ColorThemeType.DYNAMIC) },
                isSelected = state.colorThemeSettings is ColorThemeSettings.DynamicTheme,
                images = listOf(
                    Res.drawable.dynamic_main,
                    Res.drawable.dynamic_player
                ),
            )
        }
        item {
            ColorThemeCard(
                title = strings.systemThemeTitle,
                text = strings.systemThemeText,
                onClick = { updateColorTheme(ColorThemeType.SYSTEM) },
                isSelected = state.colorThemeSettings is ColorThemeSettings.FromSystem,
                images = emptyList(),
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
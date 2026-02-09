package com.github.enteraname74.soulsearching.feature.settings.personalisation.music

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsMusicViewPersonalisationRoute(
    navigateBack: () -> Unit,
) {

    SettingsMusicViewPersonalisationScreenView(
        navigateBack = navigateBack,
    )
}

@Composable
private fun SettingsMusicViewPersonalisationScreenView(
    navigateBack: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    SettingPage(
        navigateBack = navigateBack,
        title = strings.musics,
        verticalPadding = UiConstants.Spacing.large,
    ) {
        item {
            SoulMenuSwitch(
                title = strings.showMusicsByMonths,
                toggleAction = { viewSettingsManager.toggleMusicsByMonthsVisibility() },
                isChecked = viewSettingsManager.areMusicsByMonthsShown
            )
        }
    }
}
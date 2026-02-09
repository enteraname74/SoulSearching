package com.github.enteraname74.soulsearching.feature.settings.personalisation.album

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsAlbumViewPersonalisationRoute(
    navigateBack: () -> Unit,
) {
    SettingsAlbumViewPersonalisationScreenView(
        navigateBack = navigateBack,
    )
}

@Composable
private fun SettingsAlbumViewPersonalisationScreenView(
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
                title = strings.showAlbumTrackNumber,
                toggleAction = { viewSettingsManager.toggleShowAlbumTrackNumber() },
                isChecked = viewSettingsManager.shouldShowAlbumTrackNumber
            )
        }
    }
}
package com.github.enteraname74.soulsearching.feature.settings.personalisation.music

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

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
        title = CoreRes.string.musics.toTextSpec(),
        verticalPadding = UiConstants.Spacing.large,
    ) {
        item {
            SoulMenuSwitch(
                title = CoreRes.string.show_musics_by_months.toTextSpec(),
                toggleAction = { viewSettingsManager.toggleMusicsByMonthsVisibility() },
                isChecked = viewSettingsManager.areMusicsByMonthsShown
            )
        }
    }
}

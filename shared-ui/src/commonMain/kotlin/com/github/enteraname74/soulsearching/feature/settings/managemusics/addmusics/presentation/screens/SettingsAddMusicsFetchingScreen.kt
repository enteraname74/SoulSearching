package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun SettingsAddMusicsFetchingScreen(
    navigateBack: () -> Unit,
) {
    SoulLoadingScreen(
        navigateBack = navigateBack,
        title = strings.addMusicsTitle,
        text = strings.searchingSongsFromYourDevice,
    )
}
package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsAddMusicsFetchingScreen(
    navigateBack: () -> Unit,
) {
    SoulLoadingScreen(
        navigateBack = navigateBack,
        title = CoreRes.string.add_musics_title.toTextSpec(),
        text = CoreRes.string.searching_songs_from_your_device.toTextSpec(),
    )
}

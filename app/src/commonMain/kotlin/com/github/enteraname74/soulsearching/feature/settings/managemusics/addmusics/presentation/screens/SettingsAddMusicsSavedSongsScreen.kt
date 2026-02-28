package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_download_done
import com.github.enteraname74.soulsearching.coreui.screen.SoulTemplateScreen
import com.github.enteraname74.soulsearching.coreui.screen.TemplateScreenButtonSpec
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction

@Composable
fun SettingsAddMusicsSavedSongsScreen(
    navigateBack: () -> Unit,
    fetchSongs: () -> Unit,
) {
    SoulTemplateScreen(
        leftAction = TopBarNavigationAction(onClick = navigateBack),
        icon = CoreRes.drawable.ic_download_done,
        title = strings.addMusicsTitle,
        text = strings.songsSaved,
        buttonSpec = TemplateScreenButtonSpec(
            text = strings.searchForNewSongs,
            onClick = fetchSongs,
        ),
    )
}
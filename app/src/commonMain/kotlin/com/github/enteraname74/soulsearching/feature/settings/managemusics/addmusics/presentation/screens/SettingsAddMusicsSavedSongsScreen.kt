package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.runtime.Composable
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
        icon = Icons.Rounded.DownloadDone,
        title = strings.addMusicsTitle,
        text = strings.songsSaved,
        buttonSpec = TemplateScreenButtonSpec(
            text = strings.searchForNewSongs,
            onClick = fetchSongs,
        ),
    )
}
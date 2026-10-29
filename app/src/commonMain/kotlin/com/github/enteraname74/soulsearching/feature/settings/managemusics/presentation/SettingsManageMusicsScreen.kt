package com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_folder_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsManageMusicsRoute(
    finishAction: () -> Unit,
    navigateToFolders: () -> Unit,
    navigateToAddMusics: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    SettingPage(
        navigateBack = finishAction,
        title = CoreRes.string.manage_musics_title.toTextSpec(),
    ) {
        item {
            SoulMenuElement(
                title = CoreRes.string.used_folders_title.toTextSpec(),
                subTitle = CoreRes.string.used_folders_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_folder_filled,
                onClick = navigateToFolders
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.add_musics_title.toTextSpec(),
                subTitle = CoreRes.string.add_musics_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_music_note_filled,
                onClick = navigateToAddMusics
            )
        }
        item {
            SoulMenuSwitch(
                title = CoreRes.string.modify_music_file_title.toTextSpec(),
                subTitle = CoreRes.string.modify_music_file_text.toTextSpec(),
                toggleAction = {
                    viewSettingsManager.toggleMusicFileModification()
                },
                isChecked = viewSettingsManager.isMusicFileModificationOn
            )
        }
    }
}

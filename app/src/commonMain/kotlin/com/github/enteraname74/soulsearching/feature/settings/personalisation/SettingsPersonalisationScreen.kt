package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_album_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_house_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsPersonalisationRoute(
    navScope: SettingsPersonalisationNavigationScope,
) {
    SettingsPersonalisationScreenView(
        navigateBack = navScope.navigateBack,
        onMainPageClick = navScope.toMainPagePersonalisation,
        onMusicPageClick = navScope.toMusicViewPersonalisation,
        onPlayerPageClick = navScope.toPlayerPersonalisation,
        onAlbumViewPageClick = navScope.toAlbumViewPersonalisation,
    )
}

@Composable
private fun SettingsPersonalisationScreenView(
    navigateBack: () -> Unit,
    onMainPageClick: () -> Unit,
    onMusicPageClick: () -> Unit,
    onPlayerPageClick: () -> Unit,
    onAlbumViewPageClick: () -> Unit,
) {
    SettingPage(
        navigateBack = navigateBack,
        title = strings.personalizationTitle,
    ) {
        item {
            SoulMenuElement(
                title = strings.mainPageTitle,
                subTitle = strings.mainPageText,
                leadIcon = CoreRes.drawable.ic_house_filled,
                onClick = onMainPageClick
            )
        }
        item {
            SoulMenuElement(
                title = strings.musics,
                subTitle = strings.manageMusicsViewText,
                leadIcon = CoreRes.drawable.ic_music_note_filled,
                onClick = onMusicPageClick
            )
        }
        item {
            SoulMenuElement(
                title = strings.manageAlbumViewTitle,
                subTitle = strings.manageAlbumViewText,
                leadIcon = CoreRes.drawable.ic_album_filled,
                onClick = onAlbumViewPageClick,
            )
        }
        item {
            SoulMenuElement(
                title = strings.managePlayerTitle,
                subTitle = strings.managePlayerText,
                leadIcon = CoreRes.drawable.ic_play_filled,
                onClick = onPlayerPageClick
            )
        }
    }
}
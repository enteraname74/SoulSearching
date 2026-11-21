package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_album_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_house_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

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
        title = CoreRes.string.personalization_title.toTextSpec(),
    ) {
        item {
            SoulMenuElement(
                title = CoreRes.string.main_page_title.toTextSpec(),
                subTitle = CoreRes.string.main_page_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_house_filled,
                onClick = onMainPageClick
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.musics.toTextSpec(),
                subTitle = CoreRes.string.manage_musics_view_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_music_note_filled,
                onClick = onMusicPageClick
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.manage_album_view_title.toTextSpec(),
                subTitle = CoreRes.string.manage_album_view_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_album_filled,
                onClick = onAlbumViewPageClick,
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.manage_player_title.toTextSpec(),
                subTitle = CoreRes.string.manage_player_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_play_filled,
                onClick = onPlayerPageClick
            )
        }
    }
}

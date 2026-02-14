package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
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
                leadIcon = Icons.Rounded.House,
                onClick = onMainPageClick
            )
        }
        item {
            SoulMenuElement(
                title = strings.musics,
                subTitle = strings.manageMusicsViewText,
                leadIcon = Icons.Rounded.MusicNote,
                onClick = onMusicPageClick
            )
        }
        item {
            SoulMenuElement(
                title = strings.manageAlbumViewTitle,
                subTitle = strings.manageAlbumViewText,
                leadIcon = Icons.Rounded.MusicNote,
                onClick = onAlbumViewPageClick,
            )
        }
        item {
            SoulMenuElement(
                title = strings.managePlayerTitle,
                subTitle = strings.managePlayerText,
                leadIcon = Icons.Rounded.PlayArrow,
                onClick = onPlayerPageClick
            )
        }
    }
}
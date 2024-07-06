package com.github.soulsearching.settings.personalisation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.soulsearching.composables.SouTopBar
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.soulsearching.settings.presentation.composable.SettingsElement
import com.github.enteraname74.soulsearching.coreui.strings.strings

/**
 * Represent the view of the personalisation screen in the settings.
 */
class SettingsPersonalisationScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsPersonalisationScreenView(
            finishAction = {
                navigator.pop()
            },
            onMainPageClick = {
                navigator.push(
                    SettingsMainPagePersonalisationScreen()
                )
            },
            onMusicPageClick = {
                navigator.push(
                    SettingsMusicViewPersonalisationScreen()
                )
            },
            onPlayerPageClick = {
                navigator.push(
                    SettingsPlayerViewPersonalisationScreen()
                )
            }
        )
    }
}

@Composable
fun SettingsPersonalisationScreenView(
    finishAction: () -> Unit,
    onMainPageClick: () -> Unit,
    onMusicPageClick: () -> Unit,
    onPlayerPageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        SouTopBar(
            title = strings.personalizationTitle,
            leftAction = finishAction
        )
        LazyColumn {
            item {
                SettingsElement(
                    title = strings.mainPageTitle,
                    text = strings.mainPageText,
                    icon = Icons.Rounded.House,
                    clickAction = onMainPageClick
                )
            }
            item {
                SettingsElement(
                    title = strings.musics,
                    text = strings.manageMusicsViewText,
                    icon = Icons.Rounded.MusicNote,
                    clickAction = onMusicPageClick
                )
            }
            item {
                SettingsElement(
                    title = strings.managePlayerTitle,
                    text = strings.managePlayerText,
                    icon = Icons.Rounded.PlayArrow,
                    clickAction = onPlayerPageClick
                )
            }
            item {
                SoulPlayerSpacer()
            }
        }
    }
}
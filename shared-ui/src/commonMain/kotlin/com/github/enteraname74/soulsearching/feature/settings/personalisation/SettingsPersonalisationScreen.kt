package com.github.enteraname74.soulsearching.feature.settings.personalisation

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
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement

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

    @Composable
    private fun SettingsPersonalisationScreenView(
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
            SoulTopBar(
                title = strings.personalizationTitle,
                leftAction = TopBarNavigationAction(
                    onClick = finishAction,
                )
            )
            LazyColumn {
                item {
                    SettingsElement(
                        title = strings.mainPageTitle,
                        text = strings.mainPageText,
                        icon = Icons.Rounded.House,
                        onClick = onMainPageClick
                    )
                }
                item {
                    SettingsElement(
                        title = strings.musics,
                        text = strings.manageMusicsViewText,
                        icon = Icons.Rounded.MusicNote,
                        onClick = onMusicPageClick
                    )
                }
                item {
                    SettingsElement(
                        title = strings.managePlayerTitle,
                        text = strings.managePlayerText,
                        icon = Icons.Rounded.PlayArrow,
                        onClick = onPlayerPageClick
                    )
                }
                item {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}
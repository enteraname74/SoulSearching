package com.github.enteraname74.soulsearching.feature.settings.advanced

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpand
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceScreen
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

class SettingsAdvancedScreen: Screen, SettingPage {

    @Composable
    override fun Content() {
        val screenModel: SettingsAdvancedViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        val state: SettingsAdvancedState by screenModel.state.collectAsState()

        SettingsAdvancedComposable(
            state = state,
            navigateBack = navigator::pop,
            onToggleExpandReloadImage = screenModel::toggleImageReloadPanelExpandedState,
            togglePlaylistsCovers = screenModel::toggleDeletePlaylistsCovers,
            toggleArtistsCovers = screenModel::toggleReloadArtistsCovers,
            toggleAlbumsCovers = screenModel::toggleReloadAlbumsCovers,
            toggleMusicsCovers = screenModel::toggleReloadMusicsCovers,
            reloadImages = screenModel::reloadImages,
            toMultipleArtists = {
                navigator.safePush(
                    MultipleArtistsChoiceScreen(
                        mode = MultipleArtistsChoiceMode.GeneralCheck,
                    )
                )
            }
        )
    }

    @Composable
    private fun SettingsAdvancedComposable(
        state: SettingsAdvancedState,
        onToggleExpandReloadImage: () -> Unit,
        navigateBack: () -> Unit,
        toMultipleArtists: () -> Unit,
        toggleMusicsCovers: () -> Unit,
        togglePlaylistsCovers: () -> Unit,
        toggleAlbumsCovers: () -> Unit,
        toggleArtistsCovers: () -> Unit,
        reloadImages: () -> Unit
    ) {
        SettingPage(
            navigateBack = navigateBack,
            title = strings.advancedSettingsTitle,
        ) {
            item {
                SoulMenuExpand(
                    modifier = Modifier
                        .padding(
                            horizontal = UiConstants.Spacing.large,
                        ),
                    title = strings.reloadCoversTitle,
                    subTitle = strings.reloadCoversText,
                    clickAction = onToggleExpandReloadImage,
                    isExpanded = state.isImageReloadPanelExpanded,
                ) {
                    ReloadImagesContent(
                        togglePlaylists = togglePlaylistsCovers,
                        toggleAlbums = toggleAlbumsCovers,
                        toggleArtists = toggleArtistsCovers,
                        toggleMusics = toggleMusicsCovers,
                        reloadImages = reloadImages,
                        state = state,
                    )
                }
            }
            item {
                SoulMenuElement(
                    title = strings.splitMultipleArtistsTitle,
                    subTitle = strings.splitMultipleArtistsText,
                    icon = Icons.Rounded.Groups,
                    onClick = toMultipleArtists,
                )
            }
        }
    }

    @Composable
    private fun ReloadImagesContent(
        state: SettingsAdvancedState,
        toggleMusics: () -> Unit,
        togglePlaylists: () -> Unit,
        toggleAlbums: () -> Unit,
        toggleArtists: () -> Unit,
        reloadImages: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = UiConstants.Spacing.large,
                    end = UiConstants.Spacing.large,
                    bottom = UiConstants.Spacing.medium,
                )
        ) {
            SoulMenuSwitch(
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                title = strings.reloadMusicsCovers,
                toggleAction = toggleMusics,
                isChecked = state.shouldReloadSongsCovers,
                padding = PaddingValues(
                    bottom = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                title = strings.deletePlaylistsCovers,
                toggleAction = togglePlaylists,
                isChecked = state.shouldDeletePlaylistsCovers,
                padding = PaddingValues(
                    bottom = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                title = strings.reloadAlbumsCovers,
                toggleAction = toggleAlbums,
                isChecked = state.shouldReloadAlbumsCovers,
                padding = PaddingValues(
                    bottom = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                title = strings.reloadArtistsCovers,
                toggleAction = toggleArtists,
                isChecked = state.shouldReloadArtistsCovers,
                padding = PaddingValues(all = 0.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = UiConstants.Spacing.medium,
                    ),
                contentAlignment = Alignment.CenterEnd,
            ) {
                SoulButton(
                    onClick = reloadImages,
                    colors = SoulButtonDefaults.primaryColors(),
                ) {
                    Text(
                        text = strings.reloadCoversTitle,
                        textAlign = TextAlign.Center,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
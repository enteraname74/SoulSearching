package com.github.enteraname74.soulsearching.feature.settings.advanced

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpand
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuLeadingIconSpec
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceScreen
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist.SettingsArtistCoverMethodScreen
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedNavigationState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedPermissionState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedState
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import kotlinx.coroutines.delay

enum class SettingsAdvancedScreenFocusedElement {
    LyricsPermission,
    ReleasePermission,
}

class SettingsAdvancedScreen(
    private val focusedElement: SettingsAdvancedScreenFocusedElement? = null,
) : Screen, SettingPage {

    @Composable
    override fun Content() {
        val screenModel: SettingsAdvancedViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        val state: SettingsAdvancedState by screenModel.state.collectAsState()
        val permissionState: SettingsAdvancedPermissionState by screenModel.permissionState.collectAsState()
        val navigationState: SettingsAdvancedNavigationState by screenModel.navigationState.collectAsState()
        val dialogState: SoulDialog? by screenModel.dialogState.collectAsState()

        dialogState?.Dialog()

        LaunchedEffect(navigationState) {
            when (navigationState) {
                SettingsAdvancedNavigationState.Idle -> {
                    /*no-op*/
                }

                SettingsAdvancedNavigationState.ToMultipleArtists -> {
                    navigator.safePush(
                        MultipleArtistsChoiceScreen(
                            serializedMode = MultipleArtistsChoiceMode.GeneralCheck.serialize(),
                        )
                    )
                    screenModel.consumeNavigation()
                }
                SettingsAdvancedNavigationState.ToArtistCoverMethod -> {
                    navigator.safePush(
                        SettingsArtistCoverMethodScreen()
                    )
                    screenModel.consumeNavigation()
                }
            }
        }

        SettingsAdvancedComposable(
            state = state,
            permissionState = permissionState,
            navigateBack = navigator::pop,
            onAction = screenModel::onAction,
        )
    }

    @Composable
    private fun SettingsAdvancedComposable(
        state: SettingsAdvancedState,
        permissionState: SettingsAdvancedPermissionState,
        navigateBack: () -> Unit,
        onAction: (SettingsAdvancedAction) -> Unit,
    ) {

        var shouldAnimate by rememberSaveable {
            mutableStateOf(false)
        }

        val focusedColorAlpha by animateFloatAsState(
            targetValue = if (shouldAnimate) 1f else 0f,
            animationSpec = repeatable(
                iterations = FOCUSED_ANIMATION_TOTAL_OF_REPEAT,
                animation = tween(durationMillis = UiConstants.AnimationDuration.medium),
                repeatMode = RepeatMode.Reverse,
            ),
        )

        LaunchInit {
            delay(UiConstants.AnimationDuration.short.toLong())
            shouldAnimate = true
        }

        val focusedColor = SoulSearchingColorTheme.colorScheme.secondary

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
                    clickAction = { onAction(SettingsAdvancedAction.ToggleExpandReloadImage) },
                    isExpanded = state.isImageReloadPanelExpanded,
                ) {
                    ReloadImagesContent(
                        onAction = onAction,
                        state = state,
                    )
                }
            }
            item {
                SoulMenuElement(
                    title = strings.splitMultipleArtistsTitle,
                    subTitle = strings.splitMultipleArtistsText,
                    icon = Icons.Rounded.Groups,
                    onClick = { onAction(SettingsAdvancedAction.ToMultipleArtists) },
                )
            }
            item {
                SoulMenuElement(
                    title = strings.artistCoverMethodTitle,
                    subTitle = strings.artistCoverMethodText,
                    icon = Icons.Rounded.Groups,
                    onClick = { onAction(SettingsAdvancedAction.ToArtistCoverMethod) },
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .chainIf(focusedElement == SettingsAdvancedScreenFocusedElement.LyricsPermission) {
                            Modifier
                                .background(
                                    color = focusedColor.copy(alpha = focusedColorAlpha)
                                )
                        }
                ) {
                    SoulMenuSwitch(
                        title = strings.activateRemoteLyricsFetchTitle,
                        toggleAction = { onAction(SettingsAdvancedAction.ToggleLyricsPermission) },
                        isChecked = permissionState.isLyricsPermissionEnabled,
                        maxLines = Int.MAX_VALUE,
                        trailingIcon = SoulMenuLeadingIconSpec(
                            icon = Icons.Rounded.Info,
                            onClick = { onAction(SettingsAdvancedAction.ShowLyricsPermissionDialog) },
                        )
                    )
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .chainIf(focusedElement == SettingsAdvancedScreenFocusedElement.ReleasePermission) {
                            Modifier
                                .background(
                                    color = focusedColor.copy(alpha = focusedColorAlpha)
                                )
                        }
                ) {
                    SoulMenuSwitch(
                        title = strings.activateGithubReleaseFetchTitle,
                        toggleAction = { onAction(SettingsAdvancedAction.ToggleGithubReleaseFetchPermission) },
                        isChecked = permissionState.isGitHubReleaseFetchPermissionEnabled,
                        maxLines = Int.MAX_VALUE,
                        trailingIcon = SoulMenuLeadingIconSpec(
                            icon = Icons.Rounded.Info,
                            onClick = { onAction(SettingsAdvancedAction.ShowGitHubReleasePermissionDialog) },
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun ReloadImagesContent(
        state: SettingsAdvancedState,
        onAction: (SettingsAdvancedAction) -> Unit,
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
                toggleAction = { onAction(SettingsAdvancedAction.ToggleMusicsCover) },
                isChecked = state.shouldReloadSongsCovers,
                padding = PaddingValues(
                    bottom = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                title = strings.deletePlaylistsCovers,
                toggleAction = { onAction(SettingsAdvancedAction.TogglePlaylistsCovers) },
                isChecked = state.shouldDeletePlaylistsCovers,
                padding = PaddingValues(
                    bottom = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                title = strings.reloadAlbumsCovers,
                toggleAction = { onAction(SettingsAdvancedAction.ToggleAlbumsCovers) },
                isChecked = state.shouldReloadAlbumsCovers,
                padding = PaddingValues(
                    bottom = UiConstants.Spacing.small,
                ),
            )
            SoulMenuSwitch(
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                title = strings.reloadArtistsCovers,
                toggleAction = { onAction(SettingsAdvancedAction.ToggleArtistsCovers) },
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
                    onClick = { onAction(SettingsAdvancedAction.ReloadImages) },
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

private const val FOCUSED_ANIMATION_TOTAL_OF_REPEAT = 5
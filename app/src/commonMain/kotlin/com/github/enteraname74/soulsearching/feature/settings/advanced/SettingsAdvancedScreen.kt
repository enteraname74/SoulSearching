package com.github.enteraname74.soulsearching.feature.settings.advanced

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedNavigationState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedPermissionState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedState
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
enum class SettingsAdvancedScreenFocusedElement {
    LyricsPermission,
    ReleasePermission,
}

@Composable
fun SettingsAdvancedRoute(
    viewModel: SettingsAdvancedViewModel,
    onNavigation: (SettingsAdvancedNavigationState) -> Unit,
) {
    val state: SettingsAdvancedState by viewModel.state.collectAsState()
    val permissionState: SettingsAdvancedPermissionState by viewModel.permissionState.collectAsState()
    val navigationState: SettingsAdvancedNavigationState by viewModel.navigationState.collectAsState()
    val dialogState: SoulDialog? by viewModel.dialogState.collectAsState()

    dialogState?.Dialog()

    LaunchedEffect(navigationState) {
        onNavigation(navigationState)
        viewModel.consumeNavigation()
    }

    SettingsAdvancedComposable(
        state = state,
        permissionState = permissionState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun SettingsAdvancedComposable(
    state: SettingsAdvancedState,
    permissionState: SettingsAdvancedPermissionState,
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
        navigateBack = { onAction(SettingsAdvancedAction.NavigateBack) },
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
                leadIcon = Icons.Rounded.Groups,
                onClick = { onAction(SettingsAdvancedAction.ToMultipleArtists) },
            )
        }
        item {
            SoulMenuElement(
                title = strings.artistCoverMethodTitle,
                subTitle = strings.artistCoverMethodText,
                leadIcon = Icons.Rounded.Image,
                onClick = { onAction(SettingsAdvancedAction.ToArtistCoverMethod) },
            )
        }
        item {
            Box(
                modifier = Modifier
                    .chainIf(state.focusedElement == SettingsAdvancedScreenFocusedElement.LyricsPermission) {
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
                    .chainIf(state.focusedElement == SettingsAdvancedScreenFocusedElement.ReleasePermission) {
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

private const val FOCUSED_ANIMATION_TOTAL_OF_REPEAT = 5
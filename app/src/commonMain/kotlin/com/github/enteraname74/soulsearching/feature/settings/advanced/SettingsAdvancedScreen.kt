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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.domain.model.SoulPlatform
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_groups_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_image_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_info_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpand
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuLeadingIconSpec
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedNavigationState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedPermissionState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedState
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

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
        delay(UiConstants.AnimationDuration.short.toLong().milliseconds)
        shouldAnimate = true
    }

    val focusedColor = SoulSearchingColorTheme.colorScheme.surface

    SettingPage(
        navigateBack = { onAction(SettingsAdvancedAction.NavigateBack) },
        title = CoreRes.string.advanced_settings_title.toTextSpec(),
    ) {
        item {
            SoulMenuExpand(
                modifier = Modifier
                    .padding(
                        horizontal = UiConstants.Spacing.large,
                    )
                    .clip(RoundedCornerShape(size = 10.dp))
                    .background(color = SoulSearchingColorTheme.colorScheme.surface),
                title = CoreRes.string.reload_covers_title.toTextSpec(),
                subTitle = CoreRes.string.reload_covers_text.toTextSpec(),
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
                title = CoreRes.string.split_multiple_artists_title.toTextSpec(),
                subTitle = CoreRes.string.split_multiple_artists_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_groups_filled,
                onClick = { onAction(SettingsAdvancedAction.ToMultipleArtists) },
            )
        }
        if (SoulPlatformUtils.platform != SoulPlatform.Web) {
            item {
                SoulMenuElement(
                    title = CoreRes.string.artist_cover_method_title.toTextSpec(),
                    subTitle = CoreRes.string.artist_cover_method_text.toTextSpec(),
                    leadIcon = CoreRes.drawable.ic_image_filled,
                    onClick = { onAction(SettingsAdvancedAction.ToArtistCoverMethod) },
                )
            }
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
                    title = CoreRes.string.activate_remote_lyrics_fetch_title.toTextSpec(),
                    toggleAction = { onAction(SettingsAdvancedAction.ToggleLyricsPermission) },
                    isChecked = permissionState.isLyricsPermissionEnabled,
                    maxLines = Int.MAX_VALUE,
                    trailingIcon = SoulMenuLeadingIconSpec(
                        icon = CoreRes.drawable.ic_info_filled,
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
                    title = CoreRes.string.activate_github_release_fetch_title.toTextSpec(),
                    toggleAction = { onAction(SettingsAdvancedAction.ToggleGithubReleaseFetchPermission) },
                    isChecked = permissionState.isGitHubReleaseFetchPermissionEnabled,
                    maxLines = Int.MAX_VALUE,
                    trailingIcon = SoulMenuLeadingIconSpec(
                        icon = CoreRes.drawable.ic_info_filled,
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
                bottom = UiConstants.Spacing.medium,
            )
    ) {
        SoulMenuSwitch(
            titleColor = SoulSearchingColorTheme.colorScheme.text,
            textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
            title = CoreRes.string.reload_musics_covers.toTextSpec(),
            toggleAction = { onAction(SettingsAdvancedAction.ToggleMusicsCover) },
            isChecked = state.shouldReloadSongsCovers,
            padding = PaddingValues(
                start = UiConstants.Spacing.large,
                end = UiConstants.Spacing.large,
                bottom = UiConstants.Spacing.small,
            ),
        )
        SoulMenuSwitch(
            titleColor = SoulSearchingColorTheme.colorScheme.text,
            textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
            title = CoreRes.string.delete_playlists_covers.toTextSpec(),
            toggleAction = { onAction(SettingsAdvancedAction.TogglePlaylistsCovers) },
            isChecked = state.shouldDeletePlaylistsCovers,
            padding = PaddingValues(
                start = UiConstants.Spacing.large,
                end = UiConstants.Spacing.large,
                bottom = UiConstants.Spacing.small,
            ),
        )
        SoulMenuSwitch(
            titleColor = SoulSearchingColorTheme.colorScheme.text,
            textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
            title = CoreRes.string.delete_collections_covers.toTextSpec(),
            toggleAction = { onAction(SettingsAdvancedAction.ToggleCollectionsCovers) },
            isChecked = state.shouldDeleteCollectionsCovers,
            padding = PaddingValues(
                start = UiConstants.Spacing.large,
                end = UiConstants.Spacing.large,
                bottom = UiConstants.Spacing.small,
            ),
        )
        SoulMenuSwitch(
            titleColor = SoulSearchingColorTheme.colorScheme.text,
            textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
            title = CoreRes.string.reload_albums_covers.toTextSpec(),
            toggleAction = { onAction(SettingsAdvancedAction.ToggleAlbumsCovers) },
            isChecked = state.shouldReloadAlbumsCovers,
            padding = PaddingValues(
                start = UiConstants.Spacing.large,
                end = UiConstants.Spacing.large,
                bottom = UiConstants.Spacing.small,
            ),
        )
        SoulMenuSwitch(
            titleColor = SoulSearchingColorTheme.colorScheme.text,
            textColor = SoulSearchingColorTheme.colorScheme.textSecondary,
            title = CoreRes.string.reload_artists_covers.toTextSpec(),
            toggleAction = { onAction(SettingsAdvancedAction.ToggleArtistsCovers) },
            isChecked = state.shouldReloadArtistsCovers,
            padding = PaddingValues(
                start = UiConstants.Spacing.large,
                end = UiConstants.Spacing.large,
                bottom = UiConstants.Spacing.small,
            ),
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
                SoulText(
                    text = CoreRes.string.reload_covers_title.toTextSpec(),
                    textAlign = TextAlign.Center,
                    color = SoulSearchingColorTheme.colorScheme.text,
                    fontSize = 15.sp
                )
            }
        }
    }
}

private const val FOCUSED_ANIMATION_TOTAL_OF_REPEAT = 5

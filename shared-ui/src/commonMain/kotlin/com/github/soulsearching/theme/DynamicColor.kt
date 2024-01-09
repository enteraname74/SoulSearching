package com.github.soulsearching.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.Constants
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.utils.SettingsUtils

/**
 * Dynamic colors used in the application.
 */
object DynamicColor {
    val primary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (SettingsUtils.settingsViewModel.handler.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.primary
            } else if (SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlaylistThemeOn()
                && SettingsUtils.settingsViewModel.handler.playlistCover != null
            ) {
                val playlistPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
                    image = SettingsUtils.settingsViewModel.handler.playlistCover
                )
                ColorPaletteUtils.getDynamicPrimaryColor(
                    playlistPalette?.rgb
                )
            } else if (
                SettingsUtils.settingsViewModel.handler.isDynamicThemeOn() ||
                SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicOtherViewsThemeOn()
            ) {
                ColorPaletteUtils.getDynamicPrimaryColor(
                    baseColor = PlayerUtils.playerViewModel.handler.currentColorPalette?.rgb
                )
            } else {
                MaterialTheme.colorScheme.primary
            },
            tween(Constants.AnimationDuration.short),
            label = "PRIMARY_DYNAMIC_COLOR"
        ).value

    val onPrimary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (SettingsUtils.settingsViewModel.handler.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.onPrimary
            } else if (
                (
                        (SettingsUtils.settingsViewModel.handler.isDynamicThemeOn() ||
                                SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicOtherViewsThemeOn())
                                && PlayerUtils.playerViewModel.handler.currentColorPalette != null
                        )
                || (
                        SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlaylistThemeOn()
                                && SettingsUtils.settingsViewModel.handler.playlistCover != null
                        )
            ) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onPrimary
            },
            tween(Constants.AnimationDuration.short),
            label = "ON_PRIMARY_DYNAMIC_COLOR"
        ).value

    val secondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (SettingsUtils.settingsViewModel.handler.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.secondary
            } else if (SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlaylistThemeOn()
                && SettingsUtils.settingsViewModel.handler.playlistCover != null
            ) {
                val playlistPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
                    image = SettingsUtils.settingsViewModel.handler.playlistCover
                )
                ColorPaletteUtils.getDynamicSecondaryColor(
                    playlistPalette?.rgb
                )
            } else if (SettingsUtils.settingsViewModel.handler.isDynamicThemeOn()
                || SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicOtherViewsThemeOn()
            ) {
                ColorPaletteUtils.getDynamicSecondaryColor(
                    baseColor = PlayerUtils.playerViewModel.handler.currentColorPalette?.rgb
                )
            }  else {
                MaterialTheme.colorScheme.secondary
            },
            tween(Constants.AnimationDuration.short),
            label = "SECONDARY_DYNAMIC_COLOR"
        ).value

    val onSecondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (SettingsUtils.settingsViewModel.handler.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.onSecondary
            } else if (
                (
                        (SettingsUtils.settingsViewModel.handler.isDynamicThemeOn() ||
                                SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicOtherViewsThemeOn())
                                && PlayerUtils.playerViewModel.handler.currentColorPalette != null
                        )
                || (
                        SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlaylistThemeOn()
                                && SettingsUtils.settingsViewModel.handler.playlistCover != null
                        )
            ) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSecondary
            },
            tween(Constants.AnimationDuration.short),
            label = "ON_SECONDARY_DYNAMIC_COLOR"
        ).value

    val subText: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (SettingsUtils.settingsViewModel.handler.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.outline
            } else if (
                (
                        (SettingsUtils.settingsViewModel.handler.isDynamicThemeOn() ||
                                SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicOtherViewsThemeOn())
                                && PlayerUtils.playerViewModel.handler.currentColorPalette != null
                        )
                || (
                        SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlaylistThemeOn()
                                && SettingsUtils.settingsViewModel.handler.playlistCover != null
                        )
            ) {
                Color.LightGray
            } else {
                MaterialTheme.colorScheme.outline
            },
            tween(Constants.AnimationDuration.short),
            label = "SUB_TEXT_DYNAMIC_COLOR"
        ).value
}
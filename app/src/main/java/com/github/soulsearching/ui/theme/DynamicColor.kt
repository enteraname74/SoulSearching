package com.github.soulsearching.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.utils.ColorPaletteUtils
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.classes.utils.SettingsUtils

/**
 * Dynamic colors used in the application.
 */
object DynamicColor {
    val primary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.primary
            } else if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlaylistThemeOn()
                && SettingsUtils.settingsViewModel.playlistPalette != null
            ) {
                ColorPaletteUtils.getDynamicPrimaryColor(
                    SettingsUtils.settingsViewModel.playlistPalette?.rgb
                )
            } else if (
                SettingsUtils.settingsViewModel.isDynamicThemeOn() ||
                SettingsUtils.settingsViewModel.isPersonalizedDynamicOtherViewsThemeOn()
            ) {
                ColorPaletteUtils.getDynamicPrimaryColor()
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
            if (SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.onPrimary
            } else if (
                (
                        (SettingsUtils.settingsViewModel.isDynamicThemeOn() ||
                                SettingsUtils.settingsViewModel.isPersonalizedDynamicOtherViewsThemeOn())
                                && PlayerUtils.playerViewModel.currentColorPalette != null
                        )
                || (
                        SettingsUtils.settingsViewModel.isPersonalizedDynamicPlaylistThemeOn()
                                && SettingsUtils.settingsViewModel.playlistPalette != null
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
            if (SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.secondary
            } else if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlaylistThemeOn()
                && SettingsUtils.settingsViewModel.playlistPalette != null
            ) {
                ColorPaletteUtils.getDynamicSecondaryColor(
                    SettingsUtils.settingsViewModel.playlistPalette!!.rgb
                )
            } else if (SettingsUtils.settingsViewModel.isDynamicThemeOn()
                || SettingsUtils.settingsViewModel.isPersonalizedDynamicOtherViewsThemeOn()
            ) {
                ColorPaletteUtils.getDynamicSecondaryColor()
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
            if (SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.onSecondary
            } else if (
                (
                        (SettingsUtils.settingsViewModel.isDynamicThemeOn() ||
                                SettingsUtils.settingsViewModel.isPersonalizedDynamicOtherViewsThemeOn())
                                && PlayerUtils.playerViewModel.currentColorPalette != null
                        )
                || (
                        SettingsUtils.settingsViewModel.isPersonalizedDynamicPlaylistThemeOn()
                                && SettingsUtils.settingsViewModel.playlistPalette != null
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
            if (SettingsUtils.settingsViewModel.forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.outline
            } else if (
                (
                        (SettingsUtils.settingsViewModel.isDynamicThemeOn() ||
                                SettingsUtils.settingsViewModel.isPersonalizedDynamicOtherViewsThemeOn())
                                && PlayerUtils.playerViewModel.currentColorPalette != null
                        )
                || (
                        SettingsUtils.settingsViewModel.isPersonalizedDynamicPlaylistThemeOn()
                                && SettingsUtils.settingsViewModel.playlistPalette != null
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
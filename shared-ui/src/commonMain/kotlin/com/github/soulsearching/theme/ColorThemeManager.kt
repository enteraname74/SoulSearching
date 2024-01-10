package com.github.soulsearching.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.palette.graphics.Palette
import com.github.soulsearching.Constants
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.types.ColorThemeType
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.SettingsUtils

/**
 * Manage the color theme of the application.
 */
class ColorThemeManager(
    private val settings: SoulSearchingSettings
) {
    var colorThemeType by mutableIntStateOf(ColorThemeType.SYSTEM)
    var isDynamicPlayerThemeSelected by mutableStateOf(false)
    var isDynamicPlaylistThemeSelected by mutableStateOf(false)
    var isDynamicOtherViewsThemeSelected by mutableStateOf(false)

    var playlistCover by mutableStateOf<ImageBitmap?>(null)
    var forceBasicThemeForPlaylists by mutableStateOf(false)
    var currentColorPalette by mutableStateOf<Palette.Swatch?>(null)

    init {
        initializeColorThemeManager()
    }

    /**
     * Initialize the color theme manager.
     */
    private fun initializeColorThemeManager() {
        with(settings) {
            colorThemeType = getInt(
                SoulSearchingSettings.COLOR_THEME_KEY, ColorThemeType.DYNAMIC
            )
            isDynamicPlayerThemeSelected = settings.getBoolean(
                SoulSearchingSettings.DYNAMIC_PLAYER_THEME, false
            )
            isDynamicPlaylistThemeSelected = settings.getBoolean(
                SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME, false
            )
            isDynamicOtherViewsThemeSelected = settings.getBoolean(
                SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME, false
            )
        }
    }

    /**
     * Check if the dynamic theme is on.
     */
    private fun isDynamicThemeOn(): Boolean {
        return colorThemeType == ColorThemeType.DYNAMIC
    }

    /**
     * Define the current playlist cover.
     */
    fun setNewPlaylistCover(playlistImage: ImageBitmap?) {
        playlistCover = playlistImage
    }

    /**
     * Check if the personalized dynamic playlist theme is off.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic playlist theme is NOT selected
     */
    fun isPersonalizedDynamicPlaylistThemeOff(): Boolean {
        return colorThemeType == ColorThemeType.PERSONALIZED && !isDynamicPlaylistThemeSelected
    }

    /**
     * Check if the personalized dynamic player theme is on.
     * The conditions are :
     * - App color theme is on personalized && The dynamic playlist theme is selected
     * - Or the color theme is on dynamic.
     */
    fun isPersonalizedDynamicPlayerThemeOn(): Boolean {
        return (colorThemeType == ColorThemeType.PERSONALIZED && isDynamicPlayerThemeSelected) || colorThemeType == ColorThemeType.DYNAMIC
    }

    /**
     * Check if the personalized dynamic playlist theme is on.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic playlist theme is selected
     */
    fun isPersonalizedDynamicPlaylistThemeOn(): Boolean {
        return colorThemeType == ColorThemeType.PERSONALIZED && isDynamicPlaylistThemeSelected
    }

    /**
     * Check if the personalized dynamic other views theme is on.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic theme for other views is selected
     */
    fun isPersonalizedDynamicOtherViewsThemeOn(): Boolean {
        return colorThemeType == ColorThemeType.PERSONALIZED && isDynamicOtherViewsThemeSelected
    }

    private val primary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.primary
            } else if (isPersonalizedDynamicPlaylistThemeOn()
                && playlistCover != null
            ) {
                val playlistPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
                    image = playlistCover
                )
                ColorPaletteUtils.getDynamicPrimaryColor(
                    playlistPalette?.rgb
                )
            } else if (isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()
            ) {
                ColorPaletteUtils.getDynamicPrimaryColor(
                    baseColor = currentColorPalette?.rgb
                )
            } else {
                MaterialTheme.colorScheme.primary
            },
            tween(Constants.AnimationDuration.short),
            label = "PRIMARY_DYNAMIC_COLOR"
        ).value

    private val onPrimary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.onPrimary
            } else if (
                ((isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) && currentColorPalette != null)
                || (isPersonalizedDynamicPlaylistThemeOn() && playlistCover != null)
            ) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onPrimary
            },
            tween(Constants.AnimationDuration.short),
            label = "ON_PRIMARY_DYNAMIC_COLOR"
        ).value

    private val secondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.secondary
            } else if (isPersonalizedDynamicPlaylistThemeOn()
                && playlistCover != null
            ) {
                val playlistPalette = ColorPaletteUtils.getPaletteFromAlbumArt(image = playlistCover)
                ColorPaletteUtils.getDynamicSecondaryColor(playlistPalette?.rgb)

            } else if (isDynamicThemeOn() || SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicOtherViewsThemeOn()) {
                ColorPaletteUtils.getDynamicSecondaryColor(baseColor = currentColorPalette?.rgb)
            } else {
                MaterialTheme.colorScheme.secondary
            },
            tween(Constants.AnimationDuration.short),
            label = "SECONDARY_DYNAMIC_COLOR"
        ).value

    private val onSecondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.onSecondary
            } else if (
                ((isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) && currentColorPalette != null)
                || (isPersonalizedDynamicPlaylistThemeOn() && playlistCover != null)
            ) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSecondary
            },
            tween(Constants.AnimationDuration.short),
            label = "ON_SECONDARY_DYNAMIC_COLOR"
        ).value

    private val subText: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                MaterialTheme.colorScheme.outline
            } else if (
                ((isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) && currentColorPalette != null)
                || (isPersonalizedDynamicPlaylistThemeOn() && playlistCover != null)
            ) {
                Color.LightGray
            } else {
                MaterialTheme.colorScheme.outline
            },
            tween(Constants.AnimationDuration.short),
            label = "SUB_TEXT_DYNAMIC_COLOR"
        ).value

    /**
     * Retrieve
     */
    @Composable
    fun getColorTheme(): SoulSearchingPalette = SoulSearchingPalette(
        primary = primary,
        secondary = secondary,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        subText = subText
    )
}
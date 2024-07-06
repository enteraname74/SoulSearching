package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.palette.graphics.Palette
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.utils.ColorPaletteUtils
import com.github.enteraname74.domain.settings.SoulSearchingSettings

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

    private var playlistCover by mutableStateOf<ImageBitmap?>(null)

    /**
     * Define if the device theme should be forced when in a playlist view (artist, album, playlist)
     */
    var forceBasicThemeForPlaylists by mutableStateOf(false)
    var currentColorPalette by mutableStateOf<Palette.Swatch?>(null)

    private val primary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                SoulSearchingColorTheme.defaultTheme.primary
            } else if (isPersonalizedDynamicPlaylistThemeOn() && playlistCover != null) {
                val playlistPalette = ColorPaletteUtils.getPaletteFromAlbumArt(image = playlistCover)
                ColorPaletteUtils.getDynamicPrimaryColor(playlistPalette?.rgb)
            } else if (isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) {
                ColorPaletteUtils.getDynamicPrimaryColor(baseColor = currentColorPalette?.rgb)
            } else {
                SoulSearchingColorTheme.defaultTheme.primary
            },
            tween(UiConstants.AnimationDuration.short),
            label = "PRIMARY_DYNAMIC_COLOR"
        ).value

    private val onPrimary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                SoulSearchingColorTheme.defaultTheme.onPrimary
            } else if (
                ((isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) && currentColorPalette != null)
                || (isPersonalizedDynamicPlaylistThemeOn() && playlistCover != null)
            ) {
                Color.White
            } else {
                SoulSearchingColorTheme.defaultTheme.onPrimary
            },
            tween(UiConstants.AnimationDuration.short),
            label = "ON_PRIMARY_DYNAMIC_COLOR"
        ).value

    private val secondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                SoulSearchingColorTheme.defaultTheme.secondary
            } else if (isPersonalizedDynamicPlaylistThemeOn()
                && playlistCover != null
            ) {
                val playlistPalette = ColorPaletteUtils.getPaletteFromAlbumArt(image = playlistCover)
                ColorPaletteUtils.getDynamicSecondaryColor(playlistPalette?.rgb)

            } else if (isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) {
                ColorPaletteUtils.getDynamicSecondaryColor(baseColor = currentColorPalette?.rgb)
            } else {
                SoulSearchingColorTheme.defaultTheme.secondary
            },
            tween(UiConstants.AnimationDuration.short),
            label = "SECONDARY_DYNAMIC_COLOR"
        ).value

    private val onSecondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                SoulSearchingColorTheme.defaultTheme.onSecondary
            } else if (
                ((isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) && currentColorPalette != null)
                || (isPersonalizedDynamicPlaylistThemeOn() && playlistCover != null)
            ) {
                Color.White
            } else {
                SoulSearchingColorTheme.defaultTheme.onSecondary
            },
            tween(UiConstants.AnimationDuration.short),
            label = "ON_SECONDARY_DYNAMIC_COLOR"
        ).value

    private val subText: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (forceBasicThemeForPlaylists) {
                SoulSearchingColorTheme.defaultTheme.subText
            } else if (
                ((isDynamicThemeOn() || isPersonalizedDynamicOtherViewsThemeOn()) && currentColorPalette != null)
                || (isPersonalizedDynamicPlaylistThemeOn() && playlistCover != null)
            ) {
                Color.LightGray
            } else {
                SoulSearchingColorTheme.defaultTheme.subText
            },
            tween(UiConstants.AnimationDuration.short),
            label = "SUB_TEXT_DYNAMIC_COLOR"
        ).value

    init {
        initializeColorThemeManager()
    }

    /**
     * Toggle dynamic theme for player.
     */
    fun toggleDynamicPlayerTheme() {
        isDynamicPlayerThemeSelected = !isDynamicPlayerThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYER_THEME,
            value = isDynamicPlayerThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for playlist.
     */
    fun toggleDynamicPlaylistTheme() {
        isDynamicPlaylistThemeSelected = !isDynamicPlaylistThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME,
            value = isDynamicPlaylistThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for other views (everything except playlists and player view).
     */
    fun toggleDynamicOtherViewsTheme() {
        isDynamicOtherViewsThemeSelected = !isDynamicOtherViewsThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME,
            value = isDynamicOtherViewsThemeSelected
        )
    }

    /**
     * Update the type of color theme used in the application.
     */
    fun updateColorTheme(newTheme: Int) {
        colorThemeType = newTheme
        settings.setInt(
            key = SoulSearchingSettings.COLOR_THEME_KEY,
            value = newTheme
        )
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
     * Remove playlist theme.
     */
    fun removePlaylistTheme() {
        setNewPlaylistCover(null)
        forceBasicThemeForPlaylists = false
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
    private fun isPersonalizedDynamicOtherViewsThemeOn(): Boolean {
        return colorThemeType == ColorThemeType.PERSONALIZED && isDynamicOtherViewsThemeSelected
    }

    /**
     * Retrieve the color theme of the application.
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
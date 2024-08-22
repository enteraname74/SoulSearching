package com.github.enteraname74.soulsearching.theme

import androidx.compose.ui.graphics.ImageBitmap
import androidx.palette.graphics.Palette
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeType
import com.github.enteraname74.soulsearching.coreui.theme.color.DynamicColorThemeBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingPalette
import com.github.enteraname74.soulsearching.coreui.utils.ColorPaletteUtils
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

sealed interface ColorThemeSettings {
    data object FromSystem : ColorThemeSettings
    data object DynamicTheme : ColorThemeSettings
    data class Personalized(
        val hasDynamicPlayer: Boolean,
        val hasDynamicPlaylists: Boolean,
        val hasDynamicOtherViews: Boolean,
    ) : ColorThemeSettings

    fun canShowDynamicOtherViewsTheme(): Boolean =
        this is DynamicTheme ||
                (this as? Personalized)?.hasDynamicOtherViews == true

    fun canShowDynamicPlayerTheme(): Boolean =
        this is DynamicTheme ||
                (this as? Personalized)?.hasDynamicPlayer == true

    fun canShowDynamicPlaylistsTheme(): Boolean =
        (this as? Personalized)?.hasDynamicPlaylists == true
}

/**
 * Manage the color theme of the application.
 */
class ColorThemeManager(
    settings: SoulSearchingSettings,
    playerViewManager: PlayerViewManager,
) {
    var isInDarkMode: Boolean
        get() = isInDarkTheme.value
        set(value) {
            isInDarkTheme.value = value
        }
    private val isInDarkTheme: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val palette: MutableStateFlow<Palette.Swatch?> = MutableStateFlow(null)
    private val playlistDetailCover: MutableStateFlow<PlaylistDetailCover?> = MutableStateFlow(null)

    val currentColorThemeSettings: StateFlow<ColorThemeSettings> = combine(
        settings.getFlowOn(
            key = SoulSearchingSettings.COLOR_THEME_KEY,
            defaultValue = COLOR_THEME_DEFAULT,
        ),
        settings.getFlowOn(
            key = SoulSearchingSettings.DYNAMIC_PLAYER_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_PLAYER_DEFAULT
        ),
        settings.getFlowOn(
            key = SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_PLAYLISTS_DEFAULT
        ),
        settings.getFlowOn(
            key = SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_OTHER_VIEWS_DEFAULT
        ),
    ) { colorThemeType, hasDynamicPlayer, hasDynamicPlaylists, hasDynamicOtherViews ->
        when (colorThemeType) {
            ColorThemeType.DYNAMIC -> ColorThemeSettings.DynamicTheme
            ColorThemeType.PERSONALIZED  -> ColorThemeSettings.Personalized(
                hasDynamicPlayer = hasDynamicPlayer,
                hasDynamicPlaylists = hasDynamicPlaylists,
                hasDynamicOtherViews = hasDynamicOtherViews
            )

            else -> ColorThemeSettings.FromSystem
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ColorThemeSettings.DynamicTheme,
    )

    val appColorPalette: StateFlow<SoulSearchingPalette?> = combine(
        currentColorThemeSettings,
        palette,
        playlistDetailCover,
        isInDarkTheme,
        playerViewManager.state,
    ) { colorThemeSettings, palette, playlistDetailCover, isInDarkTheme, playerViewState ->
        when {
            /*
            If the player view is expanded, we follow its theme
             */
            playerViewState == BottomSheetStates.EXPANDED ->
                buildExpandedPlayerTheme(
                    palette = palette,
                    colorThemeSettings = colorThemeSettings,
                    isInDarkTheme = isInDarkTheme,
                )

            /*
            If we are on a PlaylistDetailScreen (playlist palette used),
            we base the color to the playlist panel
             */
            playlistDetailCover != null  -> {
                buildPlaylistTheme(
                    colorThemeSettings = colorThemeSettings,
                    playlistDetailCover = playlistDetailCover,
                    isInDarkTheme = isInDarkTheme,
                    palette = palette,
                )
            }

            /*
            Dynamic theme enabled for the other views
             */
            colorThemeSettings.canShowDynamicOtherViewsTheme() ->
                buildDynamicTheme(
                    palette = palette,
                    isInDarkTheme = isInDarkTheme,
                )

            else -> SoulSearchingColorTheme.fromTheme(isInDarkTheme = isInDarkTheme)
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    val playerColorTheme: StateFlow<SoulSearchingPalette?> = combine(
        currentColorThemeSettings,
        palette,
        playlistDetailCover,
        isInDarkTheme,
        playerViewManager.state,
    ) { colorThemeSettings, palette, playlistDetailCover, isInDarkTheme, playerViewState ->
        when(playerViewState) {
            BottomSheetStates.EXPANDED -> buildExpandedPlayerTheme(
                palette = palette,
                colorThemeSettings = colorThemeSettings,
                isInDarkTheme = isInDarkTheme,
            )
            else -> buildMinimisedPlayerTheme(
                palette = palette,
                playlistDetailCover = playlistDetailCover,
                colorThemeSettings = colorThemeSettings,
                isInDarkTheme = isInDarkTheme,
            )
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    val playlistsColorTheme: StateFlow<SoulSearchingPalette?> = combine(
        currentColorThemeSettings,
        palette,
        playlistDetailCover,
        isInDarkTheme
    ) { colorThemeSettings, palette, playlistDetailCover, isInDarkTheme ->
        when {
            // If personalized theme is enabled for playlist
            colorThemeSettings.canShowDynamicPlaylistsTheme() ->
                buildPlaylistTheme(
                    colorThemeSettings = colorThemeSettings,
                    playlistDetailCover = playlistDetailCover,
                    isInDarkTheme = isInDarkTheme,
                    palette = palette,
                )
            // Else we choose the classic dynamic theme if enabled
            colorThemeSettings is ColorThemeSettings.DynamicTheme ->
                buildDynamicTheme(
                    palette = palette,
                    isInDarkTheme = isInDarkTheme,
                )

            else -> SoulSearchingColorTheme.fromTheme(isInDarkTheme = isInDarkTheme)
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    private fun buildMinimisedPlayerTheme(
        palette: Palette.Swatch?,
        playlistDetailCover: PlaylistDetailCover?,
        colorThemeSettings: ColorThemeSettings,
        isInDarkTheme: Boolean,
    ): SoulSearchingPalette =
        /*
        Follows globally the same logic as the main app theme when minimised.
         */
        when {
            /*
            If we are on a PlaylistDetailScreen,
            we base the color to the playlist panel
             */
            playlistDetailCover != null -> {
                buildPlaylistTheme(
                    palette = palette,
                    colorThemeSettings = colorThemeSettings,
                    playlistDetailCover = playlistDetailCover,
                    isInDarkTheme = isInDarkTheme,
                )
            }

            /*
            Dynamic theme enabled for the other views
             */
            colorThemeSettings.canShowDynamicOtherViewsTheme() ->
                buildDynamicTheme(
                    palette = palette,
                    isInDarkTheme = isInDarkTheme,
                )

            else -> SoulSearchingColorTheme.fromTheme(isInDarkTheme = isInDarkTheme)
        }

    private fun buildExpandedPlayerTheme(
        palette: Palette.Swatch?,
        colorThemeSettings: ColorThemeSettings,
        isInDarkTheme: Boolean,
    ): SoulSearchingPalette =
        when {
            colorThemeSettings.canShowDynamicPlayerTheme() ->
                buildDynamicTheme(
                    palette = palette,
                    isInDarkTheme = isInDarkTheme,
                )
            else -> SoulSearchingColorTheme.fromTheme(isInDarkTheme = isInDarkTheme)
        }

    private fun buildPlaylistTheme(
        colorThemeSettings: ColorThemeSettings,
        playlistDetailCover: PlaylistDetailCover?,
        palette: Palette.Swatch?,
        isInDarkTheme: Boolean
    ): SoulSearchingPalette =
        if (playlistDetailCover is PlaylistDetailCover.Cover && colorThemeSettings.canShowDynamicPlaylistsTheme()) {
            buildDynamicTheme(
                palette = playlistDetailCover.palette,
                isInDarkTheme = isInDarkTheme,
            )
        } else if (colorThemeSettings.canShowDynamicOtherViewsTheme() && playlistDetailCover == null) {
            // To fix abrupt transition
            buildDynamicTheme(
                palette = palette,
                isInDarkTheme = isInDarkTheme,
            )
        } else {
            SoulSearchingColorTheme.fromTheme(isInDarkTheme = isInDarkTheme)
        }

    private fun buildDynamicTheme(palette: Palette.Swatch?, isInDarkTheme: Boolean): SoulSearchingPalette =
        palette?.rgb?.let { paletteRgb ->
            DynamicColorThemeBuilder.buildDynamicTheme(paletteRgb = paletteRgb)
        } ?: SoulSearchingColorTheme.fromTheme(isInDarkTheme = isInDarkTheme)


    fun setCurrentCover(cover: ImageBitmap?) {
        palette.value = ColorPaletteUtils.getPaletteFromAlbumArt(
            image = cover
        )
    }

    /**
     * Define the current playlist cover.
     */
    fun setNewPlaylistCover(playlistDetailCover: PlaylistDetailCover) {
        this.playlistDetailCover.value = playlistDetailCover
    }

    /**
     * Remove playlist theme.
     */
    fun removePlaylistTheme() {
        playlistDetailCover.value = null
    }

    companion object {
        const val COLOR_THEME_DEFAULT = 0
        const val PERSONALIZED_DYNAMIC_PLAYER_DEFAULT = false
        const val PERSONALIZED_DYNAMIC_PLAYLISTS_DEFAULT = false
        const val PERSONALIZED_DYNAMIC_OTHER_VIEWS_DEFAULT = false
    }
}

sealed interface PlaylistDetailCover {
    data object NoCover : PlaylistDetailCover
    data class Cover(val playlistImage: ImageBitmap) : PlaylistDetailCover {
        val palette = ColorPaletteUtils.getPaletteFromAlbumArt(
            image = playlistImage
        )
    }

    companion object {
        fun fromImageBitmap(imageBitmap: ImageBitmap?): PlaylistDetailCover =
            if (imageBitmap == null) {
                NoCover
            } else {
                Cover(playlistImage = imageBitmap)
            }
    }
}
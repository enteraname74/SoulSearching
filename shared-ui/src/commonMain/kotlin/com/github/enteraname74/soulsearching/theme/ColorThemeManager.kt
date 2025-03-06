package com.github.enteraname74.soulsearching.theme

import androidx.compose.ui.graphics.ImageBitmap
import androidx.palette.graphics.Palette
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.theme.color.*
import com.github.enteraname74.soulsearching.coreui.utils.ColorPaletteUtils
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

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
    private val currentPlayerMusicPalette: MutableStateFlow<Palette.Swatch?> = MutableStateFlow(null)
    private val playlistDetailCover: MutableStateFlow<PlaylistDetailCover?> = MutableStateFlow(null)

    val currentDefaultThemeSettings: StateFlow<DefaultThemeSettings> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.USED_COLOR_THEME_ID_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.FORCE_DARK_THEME_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.FORCE_LIGHT_THEME_KEY),
    ) { themeId, forceDarkTheme, forceLightTheme ->
        DefaultThemeSettings(
            themeId = SoulSearchingTheme.from(id = themeId),
            forceDarkTheme = forceDarkTheme,
            forceLightTheme = forceLightTheme,
        )
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = DefaultThemeSettings(
            themeId = SoulSearchingTheme.from(
                id = settings.get(SoulSearchingSettingsKeys.ColorTheme.USED_COLOR_THEME_ID_KEY)
            ),
            forceDarkTheme = settings.get(SoulSearchingSettingsKeys.ColorTheme.FORCE_DARK_THEME_KEY),
            forceLightTheme = settings.get(SoulSearchingSettingsKeys.ColorTheme.FORCE_LIGHT_THEME_KEY),
        ),
    )

    val currentColorThemeSettings: StateFlow<ColorThemeSettings> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.COLOR_THEME_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYER_THEME),
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYLIST_THEME),
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_OTHER_VIEWS_THEME),
    ) { colorThemeType, hasDynamicPlayer, hasDynamicPlaylists, hasDynamicOtherViews ->
        when (colorThemeType) {
            ColorThemeType.DYNAMIC -> ColorThemeSettings.DynamicTheme
            ColorThemeType.PERSONALIZED -> ColorThemeSettings.Personalized(
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
        currentPlayerMusicPalette,
        playlistDetailCover,
        isInDarkTheme,
        playerViewManager.state,
        currentDefaultThemeSettings,
    ) {

        val colorThemeSettings: ColorThemeSettings = it[0] as ColorThemeSettings
        val currentPlayerMusicPalette: Palette.Swatch? = it[1] as Palette.Swatch?
        val playlistDetailCover: PlaylistDetailCover? = it[2] as PlaylistDetailCover?
        val isInDarkTheme: Boolean = it[3] as Boolean
        val playerViewState: BottomSheetStates = it[4] as BottomSheetStates
        val defaultThemeSettings: DefaultThemeSettings = it[5] as DefaultThemeSettings

        when {
            /*
            If the player view is expanded, we follow its theme
             */
            playerViewState == BottomSheetStates.EXPANDED ->
                buildExpandedPlayerTheme(
                    currentPlayerMusicPalette = currentPlayerMusicPalette,
                    colorThemeSettings = colorThemeSettings,
                    isInDarkTheme = isInDarkTheme,
                    defaultThemeSettings = defaultThemeSettings,
                )

            /*
            If we are on a PlaylistDetailScreen (playlist palette used),
            we base the color to the playlist panel
             */
            playlistDetailCover != null -> {
                buildPlaylistTheme(
                    colorThemeSettings = colorThemeSettings,
                    playlistDetailCover = playlistDetailCover,
                    isInDarkTheme = isInDarkTheme,
                    currentPlayerMusicPalette = currentPlayerMusicPalette,
                    defaultThemeSettings = defaultThemeSettings,
                )
            }

            /*
            If we have the dynamic theme enabled for the others views
            (dynamic theme OR personalized with other view checked)
            we build a dynamic theme.
             */
            colorThemeSettings.canShowDynamicOtherViewsTheme() ->
                buildDynamicTheme(
                    palette = currentPlayerMusicPalette,
                    isInDarkTheme = isInDarkTheme,
                    defaultThemeSettings = defaultThemeSettings,
                )

            else -> fromTheme(
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    val playerColorTheme: StateFlow<SoulSearchingPalette?> = combine(
        currentColorThemeSettings,
        currentPlayerMusicPalette,
        playlistDetailCover,
        isInDarkTheme,
        playerViewManager.state,
        playerViewManager.targetState,
        currentDefaultThemeSettings,
    ) {
        val colorThemeSettings: ColorThemeSettings = it[0] as ColorThemeSettings
        val currentPlayerMusicPalette: Palette.Swatch? = it[1] as Palette.Swatch?
        val playlistDetailCover: PlaylistDetailCover? = it[2] as PlaylistDetailCover?
        val isInDarkTheme: Boolean = it[3] as Boolean
        val playerViewState: BottomSheetStates = it[4] as BottomSheetStates
        val playerTargetState: BottomSheetStates = it[5] as BottomSheetStates
        val defaultThemeSettings: DefaultThemeSettings = it[6] as DefaultThemeSettings

        when {
            playerViewState == BottomSheetStates.EXPANDED || playerTargetState == BottomSheetStates.EXPANDED -> {
                buildExpandedPlayerTheme(
                    currentPlayerMusicPalette = currentPlayerMusicPalette,
                    colorThemeSettings = colorThemeSettings,
                    isInDarkTheme = isInDarkTheme,
                    defaultThemeSettings = defaultThemeSettings,
                )
            }

            else -> buildMinimisedPlayerTheme(
                currentPlayerMusicPalette = currentPlayerMusicPalette,
                playlistDetailCover = playlistDetailCover,
                colorThemeSettings = colorThemeSettings,
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    val playlistsColorTheme: StateFlow<SoulSearchingPalette?> = combine(
        currentColorThemeSettings,
        currentPlayerMusicPalette,
        playlistDetailCover,
        isInDarkTheme,
        currentDefaultThemeSettings,
    ) { colorThemeSettings, currentPlayerMusicPalette, playlistDetailCover, isInDarkTheme, defaultThemeSettings ->
        when {
            // If personalized theme is enabled for playlist
            colorThemeSettings.canShowDynamicPlaylistsTheme() ->
                buildPlaylistTheme(
                    colorThemeSettings = colorThemeSettings,
                    playlistDetailCover = playlistDetailCover,
                    isInDarkTheme = isInDarkTheme,
                    currentPlayerMusicPalette = currentPlayerMusicPalette,
                    defaultThemeSettings = defaultThemeSettings,
                )
            // Else we choose the classic dynamic theme if enabled
            colorThemeSettings is ColorThemeSettings.DynamicTheme ->
                buildDynamicTheme(
                    palette = currentPlayerMusicPalette,
                    isInDarkTheme = isInDarkTheme,
                    defaultThemeSettings = defaultThemeSettings,
                )

            else -> fromTheme(
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    private fun buildMinimisedPlayerTheme(
        currentPlayerMusicPalette: Palette.Swatch?,
        playlistDetailCover: PlaylistDetailCover?,
        colorThemeSettings: ColorThemeSettings,
        isInDarkTheme: Boolean,
        defaultThemeSettings: DefaultThemeSettings,
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
                    currentPlayerMusicPalette = currentPlayerMusicPalette,
                    colorThemeSettings = colorThemeSettings,
                    playlistDetailCover = playlistDetailCover,
                    isInDarkTheme = isInDarkTheme,
                    defaultThemeSettings = defaultThemeSettings,
                )
            }

            /*
            Dynamic theme enabled for the other views
             */
            colorThemeSettings.canShowDynamicOtherViewsTheme() ->
                buildDynamicTheme(
                    palette = currentPlayerMusicPalette,
                    isInDarkTheme = isInDarkTheme,
                    defaultThemeSettings = defaultThemeSettings,
                )

            else -> fromTheme(
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        }

    private fun fromTheme(isInDarkTheme: Boolean, defaultThemeSettings: DefaultThemeSettings): SoulSearchingPalette {

        val theme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightThemes.fromId(
            defaultThemeSettings.themeId
        )

        if (defaultThemeSettings.forceDarkTheme) return theme.darkTheme
        if (defaultThemeSettings.forceLightTheme) return theme.lightTheme

        return theme.palette(isInDarkTheme = isInDarkTheme)
    }

    private fun buildExpandedPlayerTheme(
        currentPlayerMusicPalette: Palette.Swatch?,
        colorThemeSettings: ColorThemeSettings,
        isInDarkTheme: Boolean,
        defaultThemeSettings: DefaultThemeSettings
    ): SoulSearchingPalette =
        when {
            colorThemeSettings.canShowDynamicPlayerTheme() ->
                buildDynamicTheme(
                    palette = currentPlayerMusicPalette,
                    isInDarkTheme = isInDarkTheme,
                    defaultThemeSettings = defaultThemeSettings,
                )

            else -> fromTheme(
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        }

    private fun buildPlaylistTheme(
        colorThemeSettings: ColorThemeSettings,
        playlistDetailCover: PlaylistDetailCover?,
        currentPlayerMusicPalette: Palette.Swatch?,
        isInDarkTheme: Boolean,
        defaultThemeSettings: DefaultThemeSettings
    ): SoulSearchingPalette =
        // If we have enabled the dynamic theme for playlist, we use the playlist cover for building a theme
        if (colorThemeSettings.canShowDynamicPlaylistsTheme() && playlistDetailCover != null) {
            buildDynamicTheme(
                palette = (playlistDetailCover as? PlaylistDetailCover.Cover)?.palette,
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        } else if (colorThemeSettings.canShowDynamicOtherViewsTheme() && playlistDetailCover == null) {
            // To fix abrupt transition before the playlist cover is set and if we have a dynamic other view theme
            val playlistTheme = buildDynamicTheme(
                palette = currentPlayerMusicPalette,
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
            playlistTheme
        } else if (colorThemeSettings is ColorThemeSettings.DynamicTheme) {
            // If we are simply in the dynamic theme, we use it.
            buildDynamicTheme(
                palette = currentPlayerMusicPalette,
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        } else {
            fromTheme(
                isInDarkTheme = isInDarkTheme,
                defaultThemeSettings = defaultThemeSettings,
            )
        }

    private fun buildDynamicTheme(
        palette: Palette.Swatch?,
        isInDarkTheme: Boolean,
        defaultThemeSettings: DefaultThemeSettings
    ): SoulSearchingPalette =
        palette?.rgb?.let { paletteRgb ->
            DynamicColorThemeBuilder.buildDynamicTheme(paletteRgb = paletteRgb)
        } ?: fromTheme(
            isInDarkTheme = isInDarkTheme,
            defaultThemeSettings = defaultThemeSettings,
        )


    fun setCurrentCover(cover: ImageBitmap?) {
        currentPlayerMusicPalette.value = ColorPaletteUtils.getPaletteFromAlbumArt(
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
}
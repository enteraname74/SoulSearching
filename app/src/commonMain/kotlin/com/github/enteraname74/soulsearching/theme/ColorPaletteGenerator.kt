package com.github.enteraname74.soulsearching.theme

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorPaletteSeed
import com.kmpalette.PaletteState
import com.kmpalette.loader.ImageBitmapLoader
import com.kmpalette.palette.graphics.Palette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class ColorPaletteGenerator(
    settingsRepository: SettingsRepository,
    workDispatcher: WorkDispatcher,
) {
    private val cover: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
    private val colorPaletteSeed: Flow<ColorPaletteSeed> =
        settingsRepository.observeColorTheme().map { it.paletteSeed }.map { seedString ->
            ColorPaletteSeed.fromString(string = seedString) ?: ColorPaletteSeed.Default
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val paletteSwatch: StateFlow<Palette.Swatch?> =
        combine(
            cover,
            colorPaletteSeed,
        ) { cover, seed ->
            cover to seed
        }.mapLatest { (cover, seed) ->
            getPaletteFromAlbumArt(
                image = cover,
                seed = seed,
            )
        }.stateIn(
            scope = CoroutineScope(workDispatcher.dispatcher),
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    fun setCover(cover: ImageBitmap?) {
        this.cover.value = cover
    }

    /**
     * Tries to retrieve a palette from a potential image.
     */
    private suspend fun getPaletteFromAlbumArt(
        image: ImageBitmap?,
        seed: ColorPaletteSeed,
    ): Palette.Swatch? {
        if (image == null) {
            return null
        }
        val paletteState = object : PaletteState<ImageBitmap>() {
            override val loader: ImageBitmapLoader<ImageBitmap>
                get() = object : ImageBitmapLoader<ImageBitmap> {
                    override suspend fun load(input: ImageBitmap): ImageBitmap = input
                }
        }
        paletteState.generate(image)
        return paletteState.palette?.let { palette ->
            seed.toSwatch(palette = palette) ?: palette.dominantSwatch
        }
    }

    private fun ColorPaletteSeed.toSwatch(palette: Palette): Palette.Swatch? =
        when (this) {
            ColorPaletteSeed.DarkVibrant -> palette.darkVibrantSwatch
            ColorPaletteSeed.DarkMuted -> palette.darkMutedSwatch
            ColorPaletteSeed.LightMuted -> palette.lightMutedSwatch
            ColorPaletteSeed.LightVibrant -> palette.lightVibrantSwatch
            ColorPaletteSeed.Dominant -> palette.dominantSwatch
            ColorPaletteSeed.Muted -> palette.mutedSwatch
            ColorPaletteSeed.Vibrant -> palette.vibrantSwatch
        }
}

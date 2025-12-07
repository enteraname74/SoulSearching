package com.github.enteraname74.soulsearching.theme

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.ext.COLOR_PALETTE_SEED
import com.kmpalette.PaletteState
import com.kmpalette.loader.ImageBitmapLoader
import com.kmpalette.palette.graphics.Palette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ColorPaletteGenerator(
    settings: SoulSearchingSettings,
) {
    private val cover: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
    private val colorPaletteSeed: Flow<ColorPaletteSeed> =
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.COLOR_PALETTE_SEED).map { seedString ->
            ColorPaletteSeed.fromString(string = seedString) ?: ColorPaletteSeed.Default
        }
    val paletteSwatch: StateFlow<Palette.Swatch?> = combine(
        cover,
        colorPaletteSeed
    ) { cover, seed ->
        getPaletteFromAlbumArt(
            image = cover,
            seed = seed,
        )
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
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
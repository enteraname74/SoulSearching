package com.github.soulsearching.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.model.Music

interface PlayerViewModel {

    /**
     * Retrieve the current played song if any is playing.
     */
    fun getCurrentMusic(): Music?

    /**
     * Retrieve the current position of the current music in the played list.
     */
    fun getCurrentMusicPosition(): Int

    /**
     * Retrieve the current cover of the current music.
     * Be aware that a song can have no cover.
     */
    fun getCurrentMusicCover(): ImageBitmap?

    fun getCurrentColorPalette()
}
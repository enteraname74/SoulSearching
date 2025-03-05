package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import org.koin.core.component.KoinComponent
import kotlin.math.pow

abstract class PlayerNormalizer: KoinComponent {

    /**
     * Retrieves a normalized volume to use in the player.
     */
    suspend fun getVolumeMultiplier(
        music: Music,
        targetVolume: Float = -14f,
    ): Float? {
        val meanVolume: Float = getMeanVolume(music) ?: return null
        val gain = targetVolume - meanVolume
        return 10F.pow(gain / 20)
    }

    /**
     * Retrieves the mean volume of a given music
     */
    protected abstract suspend fun getMeanVolume(music: Music): Float?
}
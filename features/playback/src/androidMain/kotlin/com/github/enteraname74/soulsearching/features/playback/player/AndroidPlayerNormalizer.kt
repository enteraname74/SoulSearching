package com.github.enteraname74.soulsearching.features.playback.player

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.github.enteraname74.domain.model.Music

class AndroidPlayerNormalizer: PlayerNormalizer() {
    override suspend fun getMeanVolume(music: Music): Float? {
        val session: FFmpegSession = FFmpegKit.execute(
            """-i "${music.path}" -af "volumedetect" -f null /dev/null """
        )

        if (!ReturnCode.isSuccess(session.returnCode)) {
            return 0f
        }

        val meanVolume: Float? = session
            .output.split("\n")
            .find { it.contains("mean_volume") }
            ?.substringAfter("mean_volume:")
            ?.removeSuffix("dB")
            ?.trim()
            ?.toFloatOrNull()

        println("Got mean volume: $meanVolume")

        return meanVolume
    }
}
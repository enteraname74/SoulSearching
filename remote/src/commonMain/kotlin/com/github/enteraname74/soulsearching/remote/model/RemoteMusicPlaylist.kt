package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RemoteMusicPlaylist(
    @Serializable(with = UUIDSerializer::class)
    val musicId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val playlistId: UUID,
) {
    fun toMusicPlaylist(): MusicPlaylist =
        MusicPlaylist(
            musicId = musicId,
            playlistId = playlistId,
            dataMode = DataMode.Cloud,
        )
}

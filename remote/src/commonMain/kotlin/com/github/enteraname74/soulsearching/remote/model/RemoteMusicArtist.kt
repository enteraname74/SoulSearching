package com.github.enteraname74.soulsearching.remote.model
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RemoteMusicArtist(
    @Serializable(with = UUIDSerializer::class)
    val musicId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val artistId: UUID,
) {
    fun toMusicArtist(): MusicArtist =
        MusicArtist(
            musicId = musicId,
            artistId = artistId,
            dataMode = DataMode.Cloud,
        )
}

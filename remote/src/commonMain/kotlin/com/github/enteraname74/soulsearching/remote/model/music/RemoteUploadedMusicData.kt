package com.github.enteraname74.soulsearching.remote.model.music

import com.github.enteraname74.soulsearching.remote.model.album.RemoteAlbum
import com.github.enteraname74.soulsearching.remote.model.artist.RemoteArtist
import com.github.enteraname74.soulsearching.repository.model.UploadedMusicResult
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUploadedMusicData(
    val music: RemoteMusic,
    val album: RemoteAlbum,
    val artists: List<RemoteArtist>,
) {
    fun toUploadMusicData(): UploadedMusicResult.Data = UploadedMusicResult.Data(
        music = music.toMusic(),
        album = album.toAlbum(),
        artists = artists.map { it.toArtist() },
    )
}

package com.github.enteraname74.localdb.ext

import com.github.enteraname74.localdb.model.RoomMusicArtist
import com.github.enteraname74.localdb.model.toRoomMusicArtist
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.MusicArtist

/**
 * We need to ensure that music artists and music album artist are all linked to the music.
 */
internal fun Music.toRoomMusicArtists(): List<RoomMusicArtist> {
    val musicArtistLinks = artists.map { artist ->
        MusicArtist(
            musicId = musicId,
            artistId = artist.artistId,
        )
    }
    val albumArtistLink = MusicArtist(
        musicId = musicId,
        artistId = album.artist.artistId,
    )

    return (musicArtistLinks + albumArtistLink).distinct().map { it.toRoomMusicArtist() }
}

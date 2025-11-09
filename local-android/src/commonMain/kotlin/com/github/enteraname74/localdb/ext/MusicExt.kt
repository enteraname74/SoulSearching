package com.github.enteraname74.localdb.ext

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdb.model.RoomMusicArtist
import com.github.enteraname74.localdb.model.toRoomMusicArtist

internal fun Music.toRoomMusicArtists(): List<RoomMusicArtist> =
    artists.map { artist ->
        MusicArtist(
            musicId = musicId,
            artistId = artist.artistId,
        ).toRoomMusicArtist()
    }
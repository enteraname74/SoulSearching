package com.github.enteraname74.soulsearching.repository.datasource.musicartist

import com.github.enteraname74.domain.model.MusicArtist

interface MusicArtistRemoteDataSource {
    suspend fun fetchMusicArtistsFromCloud(): List<MusicArtist>
}
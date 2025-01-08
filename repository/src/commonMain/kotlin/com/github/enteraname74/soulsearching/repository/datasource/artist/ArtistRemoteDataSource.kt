package com.github.enteraname74.soulsearching.repository.datasource.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.SoulResult

interface ArtistRemoteDataSource {
    suspend fun fetchAllArtistOfUser(): SoulResult<List<Artist>>
}
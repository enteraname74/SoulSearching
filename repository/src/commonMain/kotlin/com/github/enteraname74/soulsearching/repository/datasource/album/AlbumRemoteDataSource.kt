package com.github.enteraname74.soulsearching.repository.datasource.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.SoulResult

interface AlbumRemoteDataSource {
    suspend fun fetchAllAlbumOfUser(): SoulResult<List<Album>>
}
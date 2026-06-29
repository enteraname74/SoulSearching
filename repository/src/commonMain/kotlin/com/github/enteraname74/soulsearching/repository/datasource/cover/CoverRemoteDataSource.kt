package com.github.enteraname74.soulsearching.repository.datasource.cover

import com.github.enteraname74.domain.model.Cover

interface CoverRemoteDataSource {
    suspend fun getRemoteCover(cover: Cover.Url): ByteArray?
}
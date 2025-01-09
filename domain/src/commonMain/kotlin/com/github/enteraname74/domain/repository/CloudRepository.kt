package com.github.enteraname74.domain.repository

interface CloudRepository {
    suspend fun clearLastUpdateDate()
}
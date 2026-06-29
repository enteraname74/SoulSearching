package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import kotlinx.coroutines.flow.Flow

class CloudPreferencesRepositoryImpl(
    private val dataSource: CloudPreferencesDataSource
) : CloudPreferencesRepository {
    override fun observeUrl(): Flow<String?> =
        dataSource.observeUrl()

    override suspend fun setUrl(url: String) {
        dataSource.setUrl(url)
    }
}
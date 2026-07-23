package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.UserInscriptionCode
import com.github.enteraname74.domain.repository.UserInscriptionCodeRepository
import com.github.enteraname74.soulsearching.repository.datasource.code.UserInscriptionCodeLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.code.UserInscriptionCodeRemoteDataSource
import kotlinx.coroutines.flow.Flow

class UserInscriptionCodeRepositoryImpl(
    private val localDataSource: UserInscriptionCodeLocalDataSource,
    private val remoteDataSource: UserInscriptionCodeRemoteDataSource,
) : UserInscriptionCodeRepository {
    override fun observeAll(): Flow<List<UserInscriptionCode>> =
        localDataSource.observeAll()

    override suspend fun fetchAll(): SoulResult<Unit> = SoulResult.runCatching {
        val codes: List<UserInscriptionCode> = remoteDataSource.fetchAll()
        localDataSource.setCodes(codes)
    }

    override suspend fun generateCode(): SoulResult<Unit> = SoulResult.runCatching {
        val code: UserInscriptionCode = remoteDataSource.generate()
        localDataSource.upsert(code)
    }

    override suspend fun deleteCode(code: UserInscriptionCode): SoulResult<Unit> = SoulResult.runCatching {
        remoteDataSource.delete(code = code.code)
        localDataSource.delete(code = code)
    }
}

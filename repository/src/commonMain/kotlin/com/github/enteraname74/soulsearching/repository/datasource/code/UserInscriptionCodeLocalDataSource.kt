package com.github.enteraname74.soulsearching.repository.datasource.code

import com.github.enteraname74.domain.model.user.UserInscriptionCode
import kotlinx.coroutines.flow.Flow

interface UserInscriptionCodeLocalDataSource {
    fun observeAll(): Flow<List<UserInscriptionCode>>
    suspend fun setCodes(codes: List<UserInscriptionCode>)
    suspend fun upsert(code: UserInscriptionCode)
    suspend fun delete(code: UserInscriptionCode)
}

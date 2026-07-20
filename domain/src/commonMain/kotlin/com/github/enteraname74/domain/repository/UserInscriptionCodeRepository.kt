package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.UserInscriptionCode
import kotlinx.coroutines.flow.Flow

interface UserInscriptionCodeRepository {
    fun observeAll(): Flow<List<UserInscriptionCode>>
    suspend fun fetchAll(): SoulResult<Unit>
    suspend fun generateCode(): SoulResult<Unit>
    suspend fun deleteCode(code: UserInscriptionCode): SoulResult<Unit>
}
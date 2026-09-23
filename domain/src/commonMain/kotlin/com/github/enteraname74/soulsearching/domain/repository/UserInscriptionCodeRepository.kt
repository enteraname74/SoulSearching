package com.github.enteraname74.soulsearching.domain.repository

import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.model.user.UserInscriptionCode
import kotlinx.coroutines.flow.Flow

interface UserInscriptionCodeRepository {
    fun observeAll(): Flow<List<UserInscriptionCode>>
    suspend fun fetchAll(): SoulResult<Unit>
    suspend fun generateCode(): SoulResult<Unit>
    suspend fun deleteCode(code: UserInscriptionCode): SoulResult<Unit>
}

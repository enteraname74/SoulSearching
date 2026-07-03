package com.github.enteraname74.soulsearching.repository.datasource.code

import com.github.enteraname74.domain.model.user.UserInscriptionCode
import kotlin.uuid.Uuid

interface UserInscriptionCodeRemoteDataSource {
    suspend fun generate(): UserInscriptionCode

    suspend fun fetchAll(): List<UserInscriptionCode>

    suspend fun delete(code: Uuid)
}

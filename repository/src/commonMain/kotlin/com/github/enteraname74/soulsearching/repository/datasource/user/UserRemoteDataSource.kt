package com.github.enteraname74.soulsearching.repository.datasource.user

import com.github.enteraname74.domain.model.User

interface UserRemoteDataSource {
    suspend fun signUp(
        username: String,
        password: String,
    ): User

    suspend fun signIn(
        username: String,
        password: String,
        code: String,
    ): User
}
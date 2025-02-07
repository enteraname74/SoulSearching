package com.github.enteraname74.soulsearching.repository.model

import com.github.enteraname74.domain.model.ConnectedUser

data class UserAuth(
    val user: ConnectedUser,
    val tokens: UserTokens,
)

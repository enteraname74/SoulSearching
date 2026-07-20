package com.github.enteraname74.domain.model.user

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserInscriptionCode(
    val code: Uuid,
)
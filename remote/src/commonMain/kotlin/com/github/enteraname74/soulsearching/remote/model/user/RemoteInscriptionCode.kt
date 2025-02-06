package com.github.enteraname74.soulsearching.remote.model.user

import com.github.enteraname74.domain.model.CloudInscriptionCode
import kotlinx.serialization.Serializable

@Serializable
data class RemoteInscriptionCode(
    val code: String,
) {
    fun toInscriptionCode(): CloudInscriptionCode =
        CloudInscriptionCode(code)
}

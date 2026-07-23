package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.user.UserInscriptionCode
import kotlin.uuid.Uuid

@Entity
data class RoomUserInscriptionCode(
    @PrimaryKey
    val code: Uuid,
) {
    fun toUserInscriptionCode(): UserInscriptionCode =
        UserInscriptionCode(code = code)
}

internal fun UserInscriptionCode.toRoomUserInscriptionCode(): RoomUserInscriptionCode =
    RoomUserInscriptionCode(code = code)

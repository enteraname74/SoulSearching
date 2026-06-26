package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.UserInscriptionCode
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

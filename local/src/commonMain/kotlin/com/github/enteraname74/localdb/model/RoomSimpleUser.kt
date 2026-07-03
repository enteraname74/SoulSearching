package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.UserType
import kotlin.uuid.Uuid

@Entity
data class RoomSimpleUser(
    @PrimaryKey
    val id: Uuid,
    val username: String,
    val type: UserType,
) {
    fun toSimpleUser(): SimpleUser =
        SimpleUser(
            id = id,
            username = username,
            type = type,
        )
}

internal fun SimpleUser.toRoomSimpleUser(): RoomSimpleUser =
    RoomSimpleUser(
        id = id,
        username = username,
        type = type,
    )

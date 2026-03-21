package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.User
import kotlin.uuid.Uuid

@Entity
data class RoomUser(
    @PrimaryKey val id: Uuid,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val type: User.Type,
) {
    fun toUser(): User =
        User(
            id = id,
            username = username,
            accessToken = accessToken,
            refreshToken = refreshToken,
            type = type,
        )
}

fun User.toRoomUser(): RoomUser =
    RoomUser(
        id = id,
        username = username,
        accessToken = accessToken,
        refreshToken = refreshToken,
        type = type,
    )

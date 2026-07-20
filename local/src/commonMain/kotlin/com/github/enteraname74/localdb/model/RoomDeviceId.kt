package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomDeviceId(
    @PrimaryKey val id: String = Id,
    val deviceId: String,
) {
    private companion object {
        const val Id: String = "RoomDeviceId"
    }
}

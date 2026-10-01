package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class RoomDeviceId(
    @PrimaryKey val id: String = Id,
    val deviceId: String,
) {
    private companion object {
        const val Id: String = "RoomDeviceId"
    }
}

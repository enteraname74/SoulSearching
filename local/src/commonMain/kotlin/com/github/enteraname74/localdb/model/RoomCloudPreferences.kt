package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomCloudPreferences(
    @PrimaryKey val id: String = Id,
    val url: String = "",
) {
    private companion object {
        const val Id: String = "RoomCloudPreferencesId"
    }
}

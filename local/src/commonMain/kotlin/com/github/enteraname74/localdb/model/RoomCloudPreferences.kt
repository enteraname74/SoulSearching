package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.CloudPreferences

@Entity
data class RoomCloudPreferences(
    @PrimaryKey val id: String = Id,
    val url: String = "",
    val lastSyncMillis: Long? = null,
) {
    private companion object {
        const val Id: String = "RoomCloudPreferencesId"
    }

    fun toCloudPreferences(): CloudPreferences =
        CloudPreferences(
            url = url,
            lastSyncMillis = lastSyncMillis,
        )
}

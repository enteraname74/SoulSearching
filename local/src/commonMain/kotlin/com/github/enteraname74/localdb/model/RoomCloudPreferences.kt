package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.soulsearching.domain.model.CloudPreferences

@Entity
data class RoomCloudPreferences(
    @PrimaryKey val id: String = Id,
    val url: String = "",
    val lastSyncMillis: Long? = null,
    val lastStatisticsSyncMillis: Long? = null,
) {
    companion object {
        const val Id: String = "RoomCloudPreferencesId"
    }

    fun toCloudPreferences(): CloudPreferences =
        CloudPreferences(
            url = url,
            lastSyncMillis = lastSyncMillis,
            lastStatisticsSyncMillis = lastStatisticsSyncMillis,
        )
}

fun CloudPreferences.toRoomCloudPreferences(): RoomCloudPreferences =
    RoomCloudPreferences(
        id = RoomCloudPreferences.Id,
        url = url.orEmpty(),
        lastSyncMillis = lastSyncMillis,
        lastStatisticsSyncMillis = lastStatisticsSyncMillis,
    )

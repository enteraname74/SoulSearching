package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.user.UserStorage
import com.github.enteraname74.domain.model.user.UserStorage.StorageType

@Entity
data class RoomUserStorage(
    @PrimaryKey
    val id: String,
    val max: StorageType,
    val current: Double,
) {
    fun toUserStorage(): UserStorage =
        UserStorage(
            max = max,
            current = current,
        )

    companion object {
        const val Id: String = "RoomUserStorageId"
    }
}

internal fun UserStorage.toRoomUserStorage(): RoomUserStorage =
    RoomUserStorage(
        id = RoomUserStorage.Id,
        max = max,
        current = current,
    )

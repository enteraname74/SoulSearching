package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomDeviceId
import kotlin.uuid.Uuid

@Dao
abstract class DeviceIdDao {
    @Upsert
    abstract suspend fun upsert(deviceId: RoomDeviceId)

    @Query("SELECT deviceId FROM RoomDeviceId LIMIT 1")
    abstract suspend fun getNullableDeviceId(): String?

    @Transaction
    open suspend fun getDeviceId(): String {
        val deviceId: String? = getNullableDeviceId()

        return if (deviceId == null) {
            val id = Uuid.random().toString()
            upsert(RoomDeviceId(deviceId = id))
            id
        } else {
            deviceId
        }
    }
}

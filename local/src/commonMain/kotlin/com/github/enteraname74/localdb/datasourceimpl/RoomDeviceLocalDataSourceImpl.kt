package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.soulsearching.repository.datasource.DeviceLocalDataSource

class RoomDeviceLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
): DeviceLocalDataSource {
    override suspend fun getDeviceId(): String =
        appDatabase.deviceIdDao.getDeviceId()
}

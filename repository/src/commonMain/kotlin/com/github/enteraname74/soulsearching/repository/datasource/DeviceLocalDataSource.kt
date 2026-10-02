package com.github.enteraname74.soulsearching.repository.datasource

interface DeviceLocalDataSource {
    suspend fun getDeviceId(): String
}

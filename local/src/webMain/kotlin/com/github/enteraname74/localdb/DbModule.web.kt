package com.github.enteraname74.localdb

import com.github.enteraname74.localdb.datasourceimpl.SettingsCloudPreferencesDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.SettingsDeviceLocalDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.SettingsUserLocalDataSourceImpl
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DeviceLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    singleOf(::SettingsCloudPreferencesDataSourceImpl) bind CloudPreferencesDataSource::class
    singleOf(::SettingsUserLocalDataSourceImpl) bind UserLocalDataSource::class
    singleOf(::SettingsDeviceLocalDataSourceImpl) bind DeviceLocalDataSource::class
    single { RoomPlatformBuilder() }
}

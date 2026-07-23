package com.github.enteraname74.localdb

import com.github.enteraname74.localdb.datasourceimpl.RoomCloudPreferencesDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomDeviceLocalDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomUserLocalDataSourceImpl
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DeviceLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal actual val platformModule: Module = module {
    single {
        RoomPlatformBuilder(
            context = androidApplication()
        )
    }
}

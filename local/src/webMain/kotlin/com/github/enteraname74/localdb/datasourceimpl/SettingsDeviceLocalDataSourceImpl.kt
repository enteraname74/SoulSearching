package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.settingElementOf
import com.github.enteraname74.soulsearching.repository.datasource.DeviceLocalDataSource
import kotlin.uuid.Uuid

class SettingsDeviceLocalDataSourceImpl(
    private val settings: SoulSearchingSettings,
) : DeviceLocalDataSource {
    private val DEVICE_ID: SoulSearchingSettingElement<String> = settingElementOf(
        key = "DEVICE_ID",
        defaultValue = Uuid.random().toString(),
    )

    override suspend fun getDeviceId(): String =
        settings.get(DEVICE_ID)
}
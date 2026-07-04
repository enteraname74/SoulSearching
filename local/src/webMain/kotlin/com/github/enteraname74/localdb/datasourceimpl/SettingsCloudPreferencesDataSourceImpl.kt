package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.CloudPreferences
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.settingElementOf
import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsCloudPreferencesDataSourceImpl(
    private val settings: SoulSearchingSettings,
) : CloudPreferencesDataSource {
    private val CLOUD_PREFERENCES: SoulSearchingSettingElement<String> = settingElementOf(
        key = "CLOUD_PREFERENCES",
        defaultValue = ""
    )

    private fun parseToCloudPreferences(json: String): CloudPreferences? =
        runCatching { SerializationUtils.deserialize<CloudPreferences>(json) }.getOrNull()

    private fun getCloudPreferences(): CloudPreferences =
        parseToCloudPreferences(settings.get(CLOUD_PREFERENCES)) ?: CloudPreferences(
            url = null,
            lastSyncMillis = null,
        )

    private fun setCloudPreferences(preferences: CloudPreferences) {
        settings.set(
            CLOUD_PREFERENCES.key,
            SerializationUtils.serialize(preferences)
        )
    }

    override fun observeUrl(): Flow<String?> =
        observePreferences().map { it?.url }

    override suspend fun setUrl(url: String) {
        val updatedPreferences = getCloudPreferences().copy(
            url = url,
        )
        setCloudPreferences(updatedPreferences)
    }

    override suspend fun setLastSyncMillis(millis: Long) {
        val updatedPreferences = getCloudPreferences().copy(
            lastSyncMillis = millis,
        )
        setCloudPreferences(updatedPreferences)
    }

    override fun observePreferences(): Flow<CloudPreferences?> =
        settings.getFlowOn(CLOUD_PREFERENCES).map { json ->
            parseToCloudPreferences(json)
        }

    override suspend fun clearLastSyncMillis() {
        settings.delete(CLOUD_PREFERENCES)
    }
}
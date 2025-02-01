package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CloudLocalDataSource(
    private val settings: SoulSearchingSettings,
) {
    fun getLastUpdateDate(): LocalDateTime? =
        settings.get(SoulSearchingSettingsKeys.Cloud.LAST_UPDATE_DATE).takeIf { it.isNotEmpty() }?.let {
            runCatching { LocalDateTime.parse(it) }.getOrNull()
        }

    fun getSearchMetadata(): Flow<Boolean> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.SEARCH_METADATA)

    fun setSearchMetadata(searchMetadata: Boolean) {
        settings.set(
            key = SoulSearchingSettingsKeys.Cloud.SEARCH_METADATA.key,
            value = searchMetadata,
        )
    }

    fun updateLastUpdateDate() {
        settings.set(
            SoulSearchingSettingsKeys.Cloud.LAST_UPDATE_DATE.key,
            LocalDateTime.now(ZoneOffset.UTC).toString(),
        )
    }

    fun clearLastUpdateDate() {
        settings.set(
            SoulSearchingSettingsKeys.Cloud.LAST_UPDATE_DATE.key,
            "",
        )
    }

    fun getAccessToken(): String =
        settings.get(SoulSearchingSettingsKeys.Cloud.ACCESS_TOKEN)
}
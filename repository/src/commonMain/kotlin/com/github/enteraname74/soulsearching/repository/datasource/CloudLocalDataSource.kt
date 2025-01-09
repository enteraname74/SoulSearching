package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CloudLocalDataSource(
    private val settings: SoulSearchingSettings,
) {
    fun getLastUpdateDate(): LocalDateTime? =
        settings.get(SoulSearchingSettingsKeys.Cloud.LAST_UPDATE_DATE).takeIf { it.isNotEmpty() }?.let {
            runCatching { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }.getOrNull()
        }

    fun updateLastUpdateDate() {
        settings.set(
            SoulSearchingSettingsKeys.Cloud.LAST_UPDATE_DATE.key,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        )
    }

    fun clearLastUpdateDate() {
        settings.set(
            SoulSearchingSettingsKeys.Cloud.LAST_UPDATE_DATE.key,
            "",
        )
    }
}
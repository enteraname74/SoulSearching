package com.github.enteraname74.soulsearching.remote.cloud

import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys

class CloudLocalDataSource(
    private val settings: SoulSearchingSettings
) {
    fun getHost(): String =
        settings.get(SoulSearchingSettingsKeys.Cloud.HOST)

    fun getToken(): String =
        settings.get(SoulSearchingSettingsKeys.Cloud.TOKEN)

    fun getUser(): User =
        User(
            username = settings.get(SoulSearchingSettingsKeys.Cloud.USERNAME),
            password = settings.get(SoulSearchingSettingsKeys.Cloud.PASSWORD)
        )
}
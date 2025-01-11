package com.github.enteraname74.soulsearching.remote.cloud

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.repository.model.UserTokens

class CloudLocalDataSource(
    private val settings: SoulSearchingSettings
) {
    fun getHost(): String =
        settings.get(SoulSearchingSettingsKeys.Cloud.HOST)

    fun getUserToken(): UserTokens =
        UserTokens(
            accessToken = settings.get(SoulSearchingSettingsKeys.Cloud.ACCESS_TOKEN),
            refreshToken = settings.get(SoulSearchingSettingsKeys.Cloud.REFRESH_TOKEN)
        )

    fun setUserToken(userToken: UserTokens) {
        settings.set(
            key = SoulSearchingSettingsKeys.Cloud.ACCESS_TOKEN.key,
            value = userToken.accessToken,
        )
        settings.set(
            key = SoulSearchingSettingsKeys.Cloud.REFRESH_TOKEN.key,
            value = userToken.refreshToken,
        )
    }
}
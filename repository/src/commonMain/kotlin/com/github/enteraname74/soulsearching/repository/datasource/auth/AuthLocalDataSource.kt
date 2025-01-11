package com.github.enteraname74.soulsearching.repository.datasource.auth

import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.repository.model.UserTokens
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class AuthLocalDataSource(
    private val settings: SoulSearchingSettings
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUser(): Flow<User?> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.USERNAME).mapLatest { username ->
            User(
                username = username,
                password = "",
            ).takeIf { it.isValid() }
        }

    fun setUser(
        user: User?,
    ) {
        settings.set(
            SoulSearchingSettingsKeys.Cloud.USERNAME.key,
            user?.username.orEmpty(),
        )
        settings.set(
            SoulSearchingSettingsKeys.Cloud.USERNAME.key,
            user?.username.orEmpty(),
        )
    }

    fun setUserTokens(userTokens: UserTokens) {
        settings.set(
            key = SoulSearchingSettingsKeys.Cloud.ACCESS_TOKEN.key,
            value = userTokens.accessToken,
        )
        settings.set(
            key = SoulSearchingSettingsKeys.Cloud.REFRESH_TOKEN.key,
            value = userTokens.refreshToken,
        )
    }
}
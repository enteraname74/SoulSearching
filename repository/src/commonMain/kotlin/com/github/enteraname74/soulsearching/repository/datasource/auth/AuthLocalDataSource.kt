package com.github.enteraname74.soulsearching.repository.datasource.auth

import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AuthLocalDataSource(
    private val settings: SoulSearchingSettings
) {
    fun getUser(): Flow<User?> =
        combine(
            settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.USERNAME),
            settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.PASSWORD),
        ) { username, password ->
            User(
                username = username,
                password = password
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

    fun setToken(token: String) {
        settings.set(
            key = SoulSearchingSettingsKeys.Cloud.TOKEN.key,
            value = token,
        )
    }
}
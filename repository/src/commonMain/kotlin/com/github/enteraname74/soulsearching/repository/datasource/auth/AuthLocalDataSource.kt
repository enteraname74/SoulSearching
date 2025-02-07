package com.github.enteraname74.soulsearching.repository.datasource.auth

import com.github.enteraname74.domain.model.ConnectedUser
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.repository.model.UserTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AuthLocalDataSource(
    private val settings: SoulSearchingSettings
) {

    fun getUser(): Flow<ConnectedUser?> =
        combine(
            settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.USERNAME),
            settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.USER_IS_ADMIN)
        ) { username, isAdmin ->
            ConnectedUser(
                username = username,
                isAdmin = isAdmin,
            ).takeIf { it.isValid() }
        }

    fun setUser(
        user: ConnectedUser?,
    ) {
        settings.set(
            SoulSearchingSettingsKeys.Cloud.USERNAME.key,
            user?.username.orEmpty(),
        )
        settings.set(
            SoulSearchingSettingsKeys.Cloud.USER_IS_ADMIN.key,
            user?.isAdmin ?: false,
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
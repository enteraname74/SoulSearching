package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.settingElementOf
import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomSimpleUser
import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class SettingsUserLocalDataSourceImpl(
    private val settings: SoulSearchingSettings,
    private val appDatabase: AppDatabase,
) : UserLocalDataSource {
    private val USER: SoulSearchingSettingElement<String> = settingElementOf(
        key = "USER",
        defaultValue = Uuid.random().toString(),
    )

    private fun parseToUser(json: String): User? =
        runCatching { SerializationUtils.deserialize<User>(json) }.getOrNull()

    override suspend fun upsert(user: User) {
        settings.set(
            key = USER.key,
            value = SerializationUtils.serialize(user)
        )
    }

    override suspend fun clear() {
        settings.delete(USER)
    }

    override fun observeUser(): Flow<User?> =
        settings.getFlowOn(USER).map { json ->
            parseToUser(json)
        }

    override suspend fun setUsers(users: List<SimpleUser>) {
        appDatabase.simpleUserDao.setUsers(
            users = users.map { it.toRoomSimpleUser() }
        )
    }

    override fun observeAll(): Flow<List<SimpleUser>> =
        appDatabase.simpleUserDao.observeAll().map { list ->
            list.map { it.toSimpleUser() }
        }

    override suspend fun deleteAllSimpleUsers() {
        appDatabase.simpleUserDao.clearAll()
    }

    override suspend fun delete(userId: Uuid) {
        appDatabase.simpleUserDao.delete(userId)
    }
}
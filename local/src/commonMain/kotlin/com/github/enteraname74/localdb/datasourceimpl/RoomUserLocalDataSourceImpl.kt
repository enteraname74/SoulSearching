package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.RoomUser
import com.github.enteraname74.localdb.model.toRoomSimpleUser
import com.github.enteraname74.localdb.model.toRoomUser
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class RoomUserLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
): UserLocalDataSource {
    override suspend fun upsert(user: User) {
        val existingUser: RoomUser? = appDatabase.userDao.observe().firstOrNull()

        if (existingUser != null && existingUser.id != user.id) {
            appDatabase.userDao.clear()
        }
        appDatabase.userDao.upsert(user.toRoomUser())
    }

    override suspend fun clear() {
        appDatabase.userDao.clear()
    }

    override fun observeUser(): Flow<User?> =
        appDatabase.userDao.observe().map { it?.toUser() }

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

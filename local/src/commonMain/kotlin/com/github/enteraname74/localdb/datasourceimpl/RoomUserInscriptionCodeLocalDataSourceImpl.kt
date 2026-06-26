package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.UserInscriptionCode
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomUserInscriptionCode
import com.github.enteraname74.soulsearching.repository.datasource.code.UserInscriptionCodeLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomUserInscriptionCodeLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
) : UserInscriptionCodeLocalDataSource {
    override fun observeAll(): Flow<List<UserInscriptionCode>> =
        appDatabase.userInscriptionCodeDao.observeAll().map { list ->
            list.map { it.toUserInscriptionCode() }
        }

    override suspend fun setCodes(codes: List<UserInscriptionCode>) {
        appDatabase.userInscriptionCodeDao.setCodes(
            codes = codes.map { it.toRoomUserInscriptionCode() }
        )
    }

    override suspend fun upsert(code: UserInscriptionCode) {
        appDatabase.userInscriptionCodeDao.upsertAll(listOf(code.toRoomUserInscriptionCode()))
    }

    override suspend fun delete(code: UserInscriptionCode) {
        appDatabase.userInscriptionCodeDao.delete(code.toRoomUserInscriptionCode())
    }
}
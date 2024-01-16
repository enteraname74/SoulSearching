package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.localdesktop.dao.PlayerMusicDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.MusicTable
import com.github.enteraname74.localdesktop.tables.PlayerMusicTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the PlayerMusicDao for Exposed.
 */
class ExposedPlayerMusicDaoImpl: PlayerMusicDao {
    override suspend fun insertPlayerMusic(playerMusic: PlayerMusic) {
        dbQuery {
            PlayerMusicTable.upsert {
                if (playerMusic.id != 0L) it[id] = playerMusic.id
                it[playerMusicId] = playerMusic.playerMusicId.toString()
            }
        }
    }

    override suspend fun deleteMusicFromPlayerList(musicId: UUID) {
        dbQuery {
            PlayerMusicTable.deleteWhere { playerMusicId eq musicId.toString()  }
        }
    }

    override suspend fun deleteAllPlayerMusic() {
        dbQuery {
            PlayerMusicTable.deleteAll()
        }
    }

    override suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>) {
        dbQuery {
            for(music in playlist) {
                insertPlayerMusic(music)
            }
        }
    }

    override suspend fun getAllPlayerMusics(): List<PlayerWithMusicItem> = dbQuery {
        PlayerMusicTable.join(
            otherTable = MusicTable,
            joinType = JoinType.INNER,
            onColumn = PlayerMusicTable.playerMusicId,
            otherColumn = MusicTable.musicId
        ).selectAll().map(ExposedUtils::resultRowToPlayerWithMusicItem)
    }

    override fun getAllPlayerMusicsAsFlow(): Flow<List<PlayerWithMusicItem>> = transaction {
        flowOf(
            PlayerMusicTable.join(
                otherTable = MusicTable,
                joinType = JoinType.INNER,
                onColumn = PlayerMusicTable.playerMusicId,
                otherColumn = MusicTable.musicId
            ).selectAll().map(ExposedUtils::resultRowToPlayerWithMusicItem)
        )
    }
}
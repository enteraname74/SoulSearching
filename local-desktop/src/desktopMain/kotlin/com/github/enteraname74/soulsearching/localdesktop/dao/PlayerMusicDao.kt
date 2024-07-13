package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.PlayerMusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toPlayerWithMusicItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class PlayerMusicDao {
    suspend fun upsert(playerMusic: PlayerMusic) {
        flowTransactionOn {
            PlayerMusicTable.upsert {
                if (playerMusic.id != 0L) it[id] = playerMusic.id
                it[playerMusicId] = playerMusic.playerMusicId
            }
        }
    }

    suspend fun delete(musicId: UUID) {
        flowTransactionOn {
            PlayerMusicTable.deleteWhere { playerMusicId eq musicId  }
        }
    }

    suspend fun deleteAll() {
        flowTransactionOn {
            PlayerMusicTable.deleteAll()
        }
    }

    suspend fun upsertAll(playlist: List<PlayerMusic>) {
        flowTransactionOn {
            PlayerMusicTable.batchUpsert(playlist) { playerMusic ->
                if (playerMusic.id != 0L) this[PlayerMusicTable.id] = playerMusic.id
                this[PlayerMusicTable.playerMusicId] = playerMusic.playerMusicId
            }
        }
    }

    fun getAll(): Flow<List<PlayerWithMusicItem>> = transaction {
        PlayerMusicTable.join(
            otherTable = MusicTable,
            joinType = JoinType.INNER,
            onColumn = PlayerMusicTable.playerMusicId,
            otherColumn = MusicTable.id
        ).selectAll()
            .asFlow()
            .mapResultRow { it.toPlayerWithMusicItem() }
            .map { it.filterNotNull() }
    }
}
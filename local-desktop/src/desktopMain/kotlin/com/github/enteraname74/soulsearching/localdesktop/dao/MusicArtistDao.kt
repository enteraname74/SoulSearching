package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.artistId
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.dataMode
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.id
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.musicId
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusicArtist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*


internal class MusicArtistDao {
    suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) {
        flowTransactionOn {
            MusicArtistTable.upsert {
                it[id] = musicArtist.id
                it[musicId] = musicArtist.musicId
                it[artistId] = musicArtist.artistId
                it[dataMode] = musicArtist.dataMode.value
            }
        }
    }

    suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        flowTransactionOn {
            MusicArtistTable.batchUpsert(musicArtists) {musicArtist ->
                this[id] = musicArtist.id
                this[musicId] = musicArtist.musicId
                this[artistId] = musicArtist.artistId
                this[dataMode] = musicArtist.dataMode.value
            }
        }
    }

    suspend fun getAll(dataMode: String): List<MusicArtist> = dbQuery {
        MusicArtistTable
            .selectAll()
            .where { MusicArtistTable.dataMode eq dataMode }
            .mapNotNull { it.toMusicArtist() }
    }

    suspend fun get(
        musicId: UUID,
        artistId: UUID,
    ): MusicArtist? = dbQuery {
        MusicArtistTable
            .selectAll()
            .where { MusicArtistTable.artistId eq artistId and (MusicArtistTable.musicId eq musicId) }
            .firstOrNull()
            ?.toMusicArtist()
    }

    suspend fun deleteAll(
        dataMode: String
    ) {
        flowTransactionOn {
            MusicArtistTable
                .deleteWhere { MusicArtistTable.dataMode eq dataMode }
        }
    }

    suspend fun delete(musicArtist: MusicArtist) = flowTransactionOn {
        MusicArtistTable.deleteWhere { musicId eq musicArtist.musicId and (artistId eq musicArtist.artistId) }
    }
}
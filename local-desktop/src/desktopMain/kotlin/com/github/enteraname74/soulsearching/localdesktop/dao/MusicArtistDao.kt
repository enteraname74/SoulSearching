package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.artistId
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.id
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.musicId
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusicArtist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


internal class MusicArtistDao {
    fun getAll(): List<MusicArtist> = transaction {
        MusicArtistTable.selectAll().mapNotNull { it.toMusicArtist() }
    }

    suspend fun delete(musicArtist: MusicArtist) = flowTransactionOn {
        MusicArtistTable.deleteWhere { musicId eq musicArtist.musicId and (artistId eq musicArtist.artistId) }
    }

    suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) {
        flowTransactionOn {
            MusicArtistTable.upsert {
                if (musicArtist.id != 0L) it[id] = musicArtist.id
                it[musicId] = musicArtist.musicId
                it[artistId] = musicArtist.artistId
            }
        }
    }

    suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        flowTransactionOn {
            MusicArtistTable.batchUpsert(musicArtists) {musicArtist ->
                if (musicArtist.id != 0L) this[id] = musicArtist.id
                this[musicId] = musicArtist.musicId
                this[artistId] = musicArtist.artistId
            }
        }
    }
}
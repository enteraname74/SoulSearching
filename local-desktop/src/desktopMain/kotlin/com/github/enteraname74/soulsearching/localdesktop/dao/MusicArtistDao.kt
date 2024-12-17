package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.artistId
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.id
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.musicId
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusicAlbum
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusicArtist
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID


internal class MusicArtistDao {
    fun getAll(): List<MusicArtist> = transaction {
        MusicArtistTable.selectAll().mapNotNull { it.toMusicArtist() }
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

    suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) {
        flowTransactionOn {
            MusicArtistTable.update({ MusicArtistTable.musicId eq musicId}) {
                it[artistId] = newArtistId
            }
        }
    }

    suspend fun deleteMusicFromArtist(musicId: UUID) {
        flowTransactionOn {
            MusicArtistTable.deleteWhere { MusicArtistTable.musicId eq musicId }
        }
    }

    suspend fun getArtistIdFromMusicId(musicId: UUID): UUID? = dbQuery {
        MusicArtistTable
            .selectAll()
            .where { MusicArtistTable.musicId eq musicId }
            .map{ it[artistId].value }
            .firstOrNull()
    }
}
package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicAlbumTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicAlbumTable.albumId
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicAlbumTable.id
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicAlbumTable.musicId
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusicAlbum
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class MusicAlbumDao {
    fun getAll(): List<MusicAlbum> = transaction {
        MusicAlbumTable.selectAll().mapNotNull { it.toMusicAlbum() }
    }

    suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) {
        flowTransactionOn {
            MusicAlbumTable.upsert {
                it[id] = musicAlbum.id
                it[musicId] = musicAlbum.musicId
                it[albumId] = musicAlbum.albumId
            }
        }
    }

    suspend fun upsertAll(musicAlbums: List<MusicAlbum>) {
        flowTransactionOn {
            MusicAlbumTable.batchUpsert(musicAlbums) {musicAlbum ->
                this[id] = musicAlbum.id
                this[musicId] = musicAlbum.musicId
                this[albumId] = musicAlbum.albumId
            }
        }
    }

    suspend fun deleteMusicFromAlbum(musicId: UUID) {
        flowTransactionOn {
            MusicAlbumTable.deleteWhere { MusicAlbumTable.musicId eq musicId }
        }
    }

    suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) {
        flowTransactionOn {
            MusicAlbumTable.update({ MusicAlbumTable.musicId eq musicId }) {
                it[albumId] = newAlbumId
            }
        }
    }

    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        flowTransactionOn {
            MusicAlbumTable.update({ albumId eq legacyAlbumId }) {
                it[albumId] = newAlbumId
            }
        }
    }

    suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> = dbQuery {
        MusicAlbumTable
            .selectAll()
            .where { MusicAlbumTable.albumId eq albumId }
            .map { it[musicId].value }
    }

    suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? = dbQuery {
        MusicAlbumTable
            .selectAll()
            .where { MusicAlbumTable.musicId eq musicId }
            .map { it[albumId].value }
            .firstOrNull()
    }
}
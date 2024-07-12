package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicAlbumTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

internal class MusicAlbumDao {
    suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) {
        flowTransactionOn {
            MusicAlbumTable.upsert {
                if (musicAlbum.id != 0L) it[id] = musicAlbum.id
                it[musicId] = musicAlbum.musicId
                it[albumId] = musicAlbum.albumId
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
            MusicAlbumTable.update({ MusicAlbumTable.albumId eq legacyAlbumId }) {
                it[albumId] = newAlbumId
            }
        }
    }

    suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> = dbQuery {
        MusicAlbumTable
            .selectAll()
            .where { MusicAlbumTable.albumId eq albumId }
            .map { it[MusicAlbumTable.albumId].value }
    }

    suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? = dbQuery {
        MusicAlbumTable
            .select(MusicAlbumTable.albumId)
            .where { MusicAlbumTable.musicId eq musicId }
            .map { it[MusicAlbumTable.musicId].value }
            .firstOrNull()
    }
}
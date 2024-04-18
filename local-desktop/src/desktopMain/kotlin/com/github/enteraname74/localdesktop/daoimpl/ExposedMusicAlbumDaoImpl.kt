package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.localdesktop.dao.MusicAlbumDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.MusicAlbumTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the MusicAlbumDao for Exposed.
 */
class ExposedMusicAlbumDaoImpl : MusicAlbumDao {
    override suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) {
        dbQuery {
            MusicAlbumTable.upsert {
                if (musicAlbum.id != 0L) it[id] = musicAlbum.id
                it[musicId] = musicAlbum.musicId.toString()
                it[albumId] = musicAlbum.albumId.toString()
            }
        }
    }

    override suspend fun deleteMusicFromAlbum(musicId: UUID) {
        dbQuery {
            MusicAlbumTable.deleteWhere { MusicAlbumTable.musicId eq musicId.toString() }
        }
    }

    override suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) {
        dbQuery {
            MusicAlbumTable.update({ MusicAlbumTable.musicId eq musicId.toString() }) {
                it[albumId] = newAlbumId.toString()
            }
        }
    }

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        dbQuery {
            MusicAlbumTable.update({ MusicAlbumTable.albumId eq legacyAlbumId.toString() }) {
                it[albumId] = newAlbumId.toString()
            }
        }
    }

    override suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> = dbQuery {
        MusicAlbumTable
            .selectAll()
            .where { MusicAlbumTable.albumId eq albumId.toString() }
            .map { UUID.fromString(it[MusicAlbumTable.albumId]) }
    }

    override suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? = dbQuery {
        MusicAlbumTable
            .select(MusicAlbumTable.albumId)
            .where { MusicAlbumTable.musicId eq musicId.toString() }
            .map { UUID.fromString(it[MusicAlbumTable.musicId]) }
            .singleOrNull()
    }

    override suspend fun getNumberOfMusicsFromAlbum(albumId: UUID): Int = dbQuery {
        MusicAlbumTable
            .selectAll()
            .where { MusicAlbumTable.albumId eq albumId.toString() }
            .count()
            .toInt()
    }
}
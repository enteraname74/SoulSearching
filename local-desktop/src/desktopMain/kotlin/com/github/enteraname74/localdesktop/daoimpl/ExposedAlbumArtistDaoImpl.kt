package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.localdesktop.dao.AlbumArtistDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.AlbumArtistTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the AlbumArtistDao for Exposed.
 */
class ExposedAlbumArtistDaoImpl: AlbumArtistDao {
    override suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist) {
        dbQuery {
            AlbumArtistTable.upsert {
                it[albumId] = albumArtist.albumId.toString()
                it[artistId] = albumArtist.artistId.toString()
            }
        }
    }

    override suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID) {
        dbQuery {
            AlbumArtistTable.update({AlbumArtistTable.albumId eq albumId.toString()}) {
                it[artistId] = newArtistId.toString()
            }
        }
    }

    override suspend fun deleteAlbumFromArtist(albumId: UUID) {
        dbQuery {
            AlbumArtistTable.deleteWhere { AlbumArtistTable.albumId eq albumId.toString() }
        }
    }
}
package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumArtistTable.albumId
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumArtistTable.artistId
import com.github.enteraname74.soulsearching.localdesktop.tables.toAlbumArtist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.*

internal class AlbumArtistDao {
    suspend fun upsertAlbumIntoArtist(albumArtist: AlbumArtist) {
        flowTransactionOn {
            AlbumArtistTable.upsert {
                it[albumId] = albumArtist.albumId
                it[artistId] = albumArtist.artistId
            }
        }
    }

    suspend fun upsertAll(albumArtists: List<AlbumArtist>) {
        flowTransactionOn {
            AlbumArtistTable.batchUpsert(albumArtists) {
                this[albumId] = it.albumId
                this[artistId] = it.artistId
            }
        }
    }

    suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID) {
        flowTransactionOn {
            AlbumArtistTable.update({ AlbumArtistTable.albumId eq albumId}) {
                it[artistId] = newArtistId
            }
        }
    }

    suspend fun deleteAlbumFromArtist(albumId: UUID) {
        flowTransactionOn {
            AlbumArtistTable.deleteWhere { AlbumArtistTable.albumId eq albumId }
        }
    }

    fun getArtistIdOfAlbum(albumId: UUID): Flow<UUID?> = transaction {
        AlbumArtistTable
            .selectAll()
            .where { AlbumArtistTable.albumId eq albumId }
            .asFlow()
            .mapSingleResultRow { it.toAlbumArtist() }
            .map { it?.artistId }
    }
}
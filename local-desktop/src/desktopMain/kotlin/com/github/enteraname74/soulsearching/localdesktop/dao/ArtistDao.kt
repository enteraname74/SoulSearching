package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.addedDate
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.artistName
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.coverId
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.id
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.isInQuickAccess
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.nbPlayed
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toArtist
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.util.*


internal class ArtistDao(
    private val albumArtistDao: AlbumArtistDao,
    private val musicDao: MusicDao,
) {
    suspend fun upsert(artist: Artist) {
        flowTransactionOn {
            ArtistTable.upsert {
                it[id] = artist.artistId
                it[artistName] = artist.artistName
                it[coverId] = (artist.cover as? Cover.FileCover)?.fileCoverId
                it[addedDate] = artist.addedDate
                it[nbPlayed] = artist.nbPlayed
                it[isInQuickAccess] = artist.isInQuickAccess
            }
        }
    }

    suspend fun upsertAll(artists: List<Artist>) {
        flowTransactionOn {
            ArtistTable.batchUpsert(artists) {
                this[id] = it.artistId
                this[artistName] = it.artistName
                this[coverId] = (it.cover as? Cover.FileCover)?.fileCoverId
                this[addedDate] = it.addedDate
                this[nbPlayed] = it.nbPlayed
                this[isInQuickAccess] = it.isInQuickAccess
            }
        }
    }

    suspend fun delete(artist: Artist) {
        flowTransactionOn {
            ArtistTable.deleteWhere { id eq artist.artistId }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getArtistOfAlbum(albumId: UUID): Flow<Artist?> =
        albumArtistDao.getArtistIdOfAlbum(albumId).flatMapLatest { artistId ->
            if (artistId != null) {
                getFromId(artistId)
            } else {
                flowOf(null)
            }
    }

    fun getFromId(artistId: UUID): Flow<Artist?> = transaction {
        ArtistTable
            .selectAll()
            .where { ArtistTable.id eq artistId }
            .asFlow()
            .mapSingleResultRow { it.toArtist() }
    }

    fun getAll(): Flow<List<Artist>> = transaction {
        ArtistTable
            .selectAll()
            .asFlow()
            .mapResultRow { it.toArtist() }
            .map { list -> list.filterNotNull() }
    }

    fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>> = transaction {
        (ArtistTable fullJoin MusicArtistTable fullJoin MusicTable)
            .selectAll()
            .asFlow()
            .map { list ->
                list.groupBy(
                    { it.toArtist() }, { it.toMusic() }
                ).map { (artist,songs) ->
                    artist?.let {
                        ArtistWithMusics(
                            artist = it,
                            musics = songs.filterNotNull(),
                        )
                    }
                }.filterNotNull()
            }
    }

    suspend fun getFromName(artistName: String): Artist? = dbQuery {
        ArtistTable
            .selectAll()
            .where { ArtistTable.artistName eq artistName }
            .map { it.toArtist() }
            .firstOrNull()
    }

    fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        combine(
            getFromId(artistId),
            musicDao.getAllMusicFromArtist(artistId)
        ) { artist, songs ->
            artist?.let {
                ArtistWithMusics(
                    artist = it,
                    musics = songs,
                )
            }
        }
}
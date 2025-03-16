package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.*
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.addedDate
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.artistName
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.coverId
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.dataMode
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.id
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.isInQuickAccess
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable.nbPlayed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


internal class ArtistDao(
    private val musicDao: MusicDao,
) {
    suspend fun upsert(artist: Artist) {
        flowTransactionOn {
            ArtistTable.upsert {
                it[id] = artist.artistId
                it[artistName] = artist.artistName
                it[coverId] = (artist.cover as? Cover.CoverFile)?.fileCoverId
                it[addedDate] = artist.addedDate
                it[nbPlayed] = artist.nbPlayed
                it[isInQuickAccess] = artist.isInQuickAccess
                it[dataMode] = artist.dataMode.value
            }
        }
    }

    suspend fun upsertAll(artists: List<Artist>) {
        flowTransactionOn {
            ArtistTable.batchUpsert(artists) {
                this[id] = it.artistId
                this[artistName] = it.artistName
                this[coverId] = (it.cover as? Cover.CoverFile)?.fileCoverId
                this[addedDate] = it.addedDate
                this[nbPlayed] = it.nbPlayed
                this[isInQuickAccess] = it.isInQuickAccess
                this[dataMode] = it.dataMode.value
            }
        }
    }

    suspend fun delete(artist: Artist) {
        flowTransactionOn {
            ArtistTable.deleteWhere { id eq artist.artistId }
        }
    }

    suspend fun deleteAll(artistsIds: List<UUID>) {
        flowTransactionOn {
            ArtistTable.deleteWhere { Op.build { id inList artistsIds } }
        }
    }

    suspend fun deleteAll(dataMode: String) = flowTransactionOn {
        ArtistTable.deleteWhere { ArtistTable.dataMode eq dataMode }
    }

    suspend fun getArtistNamesContainingSearch(search: String): List<String> = dbQuery {
        ArtistTable
            .selectAll()
            .where {
                artistName
                    .lowerCase()
                    .like("%${search.lowercase().trim()}%")
            }
            .mapNotNull {
                it.getOrNull(artistName)
            }
    }

    fun getFromId(artistId: UUID): Flow<Artist?> = transaction {
        ArtistTable
            .selectAll()
            .where { ArtistTable.id eq artistId }
            .asFlow()
            .mapSingleResultRow { it.toArtist() }
    }

    fun getAll(dataMode: String): Flow<List<Artist>> = transaction {
        ArtistTable
            .selectAll()
            .where { ArtistTable.dataMode eq dataMode }
            .orderBy(artistName to SortOrder.ASC)
            .asFlow()
            .mapResultRow { it.toArtist() }
            .map { list -> list.filterNotNull() }
    }

    suspend fun getAll(artistIds: List<UUID>): List<Artist> = dbQuery {
        ArtistTable
            .selectAll()
            .where { ArtistTable.id inList artistIds }
            .mapNotNull { it.toArtist() }
    }

    fun getAllArtistWithMusics(dataMode: String): Flow<List<ArtistWithMusics>> = transaction {
        (ArtistTable fullJoin MusicArtistTable fullJoin MusicTable)
            .selectAll()
            .where { ArtistTable.dataMode eq dataMode }
            .asFlow()
            .map { list ->
                list.groupBy(
                    { it.toArtist() }, { it.toMusic() }
                ).map { (artist, songs) ->
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

    suspend fun getAllFromNames(artistsNames: List<String>): List<Artist> = dbQuery {
        ArtistTable
            .selectAll()
            .where { artistName inList artistsNames }
            .mapNotNull { it.toArtist() }
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

    fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> = transaction {
        ArtistTable.join(
            otherTable = MusicArtistTable,
            joinType = JoinType.INNER,
            onColumn = ArtistTable.id,
            otherColumn = MusicArtistTable.artistId,
            additionalConstraint = {
                MusicArtistTable.musicId eq musicId
            }
        )
            .selectAll()
            .asFlow()
            .mapResultRow { it.toArtist() }
            .map { list -> list.filterNotNull() }
    }
}
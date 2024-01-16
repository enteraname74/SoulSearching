package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdesktop.dao.MusicArtistDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.MusicArtistTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the MusicArtistDao for Exposed.
 */
class ExposedMusicArtistDaoImpl: MusicArtistDao {
    override suspend fun insertMusicIntoArtist(musicArtist: MusicArtist) {
        dbQuery {
            MusicArtistTable.upsert {
                if (musicArtist.id != 0L) it[id] = musicArtist.id
                it[musicId] = musicArtist.musicId.toString()
                it[artistId] = musicArtist.artistId.toString()
            }
        }
    }

    override suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) {
        dbQuery {
            MusicArtistTable.update({MusicArtistTable.musicId eq musicId.toString()}) {
                it[artistId] = newArtistId.toString()
            }
        }
    }

    override suspend fun deleteMusicFromArtist(musicId: UUID) {
        dbQuery {
            MusicArtistTable.deleteWhere { MusicArtistTable.musicId eq musicId.toString() }
        }
    }

    override suspend fun getArtistIdFromMusicId(musicId: UUID): UUID? = dbQuery {
        MusicArtistTable
            .select(MusicArtistTable.artistId)
            .where { MusicArtistTable.musicId eq musicId.toString() }
            .map{ UUID.fromString(it[MusicArtistTable.artistId]) }
            .singleOrNull()
    }

    override suspend fun getNumberOfMusicsFromArtist(artistId: UUID): Int = dbQuery {
        MusicArtistTable
            .selectAll()
            .where { MusicArtistTable.artistId eq artistId.toString() }
            .count()
            .toInt()
    }
}
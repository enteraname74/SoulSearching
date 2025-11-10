package com.github.enteraname74.localdb.datasourceimpl

import androidx.room.useWriterConnection
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.ext.toRoomMusicArtists
import com.github.enteraname74.localdb.model.toRoomAlbum
import com.github.enteraname74.localdb.model.toRoomArtist
import com.github.enteraname74.localdb.model.toRoomMusic
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Implementation of the MusicDataSource with Room's DAO.
 */
internal class RoomMusicDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicDataSource {
    override suspend fun upsert(music: Music) {
        appDatabase.useWriterConnection {
            appDatabase.artistDao.upsertAll(music.artists.map { it.toRoomArtist() })
            appDatabase.albumDao.upsert(music.album.toRoomAlbum())
            appDatabase.musicDao.upsert(music.toRoomMusic())
            appDatabase.musicArtistDao.upsertAll(
                roomMusicArtists = music.toRoomMusicArtists()
            )
        }
    }

    override suspend fun upsertAll(musics: List<Music>) {
        appDatabase.useWriterConnection {
            appDatabase.artistDao.upsertAll(
                roomArtists = musics.flatMap { music ->
                    music.artists.map {
                        it.toRoomArtist()
                    }
                }
            )

            appDatabase.albumDao.upsertAll(
                roomAlbums = musics.map { it.album.toRoomAlbum() }
            )
            appDatabase.musicDao.upsertAll(musics.map { it.toRoomMusic() })
            appDatabase.musicArtistDao.upsertAll(
                roomMusicArtists = musics.flatMap { it.toRoomMusicArtists() }
            )
        }
    }

    override suspend fun delete(music: Music) {
        appDatabase.useWriterConnection {
            appDatabase.musicDao.delete(
                roomMusic = music.toRoomMusic()
            )

            val isAlbumEmpty = appDatabase.musicDao.getAllMusicFromAlbum(albumId = music.album.albumId).isEmpty()
            if (isAlbumEmpty) {
                appDatabase.albumDao.delete(id = music.album.albumId)
            }

            val artistsWithMusics = music.artists.mapNotNull {
                appDatabase.artistDao.getArtistWithMusics(artistId = it.artistId).firstOrNull()
            }

            appDatabase.artistDao.deleteAll(
                ids = artistsWithMusics.filter { it.roomMusics.isEmpty() }.map { it.roomArtist.artistId }
            )
        }

    }

    override suspend fun deleteAll(ids: List<UUID>) {
        appDatabase.useWriterConnection {
            val musics = appDatabase.musicDao.getAllFromId(ids = ids)
            appDatabase.musicDao.deleteAll(
                ids = ids,
            )

            appDatabase.albumDao.deleteAll(
                ids = musics
                    .filter { appDatabase.musicDao.getAllMusicFromAlbum(albumId = it.completeAlbum.roomAlbum.albumId).isEmpty() }
                    .map { it.completeAlbum.roomAlbum.albumId }
            )

            val artistsWithMusics = musics.flatMap { music ->
                music.artists.mapNotNull {
                    appDatabase.artistDao.getArtistWithMusics(artistId = it.artistId).firstOrNull()
                }
            }
            appDatabase.artistDao.deleteAll(
                ids = artistsWithMusics.filter { it.roomMusics.isEmpty() }.map { it.roomArtist.artistId }
            )
        }
    }

    override fun getFromId(musicId: UUID): Flow<Music?> {
        return appDatabase.musicDao.getFromId(
            musicId = musicId
        ).map { it?.toMusic() }
    }

    override fun getAll(): Flow<List<Music>> {
        return appDatabase.musicDao.getAll().map { list ->
            list.map { it.toMusic() }
        }
    }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        appDatabase.musicDao.getAllMusicFromAlbum(
            albumId = albumId
        ).map { it.toMusic() }

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        appDatabase.musicDao.updateMusicsAlbum(newAlbumId, legacyAlbumId)
    }
}
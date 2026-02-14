package com.github.enteraname74.localdb.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.useWriterConnection
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.ext.toRoomMusicArtists
import com.github.enteraname74.localdb.model.toRoomAlbum
import com.github.enteraname74.localdb.model.toRoomArtist
import com.github.enteraname74.localdb.model.toRoomMusic
import com.github.enteraname74.localdb.utils.PagingUtils
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementation of the MusicDataSource with Room's DAO.
 */
internal class RoomMusicDataSourceImpl(
    private val appDatabase: AppDatabase,
    private val settings: SoulSearchingSettings,
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
            appDatabase.musicDao.delete(roomMusic = music.toRoomMusic())
            appDatabase.albumDao.deleteAllEmpty()
            appDatabase.artistDao.deleteAllEmpty()
        }
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        appDatabase.useWriterConnection {
            appDatabase.musicDao.deleteAll(ids = ids)
            appDatabase.albumDao.deleteAllEmpty()
            appDatabase.artistDao.deleteAllEmpty()
        }
    }

    override suspend fun deleteAllFromUnselectedFolders() {
        appDatabase.useWriterConnection {
            appDatabase.musicDao.deleteFromUnselectedFolders()
            appDatabase.albumDao.deleteAllEmpty()
            appDatabase.artistDao.deleteAllEmpty()
        }
    }

    override suspend fun getAllIdsFromUnselectedFolders(): List<UUID> =
        appDatabase.musicDao.getAllIdsFromUnselectedFolders()

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

    override fun getAllFromQuickAccess(): Flow<List<Music>> =
        appDatabase.musicDao.getAllFromQuickAccess().map { list ->
            list.map { it.toMusic() }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPaged(): Flow<PagingData<Music>> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_DIRECTION_KEY).flatMapLatest { direction ->
            settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_TYPE_KEY).flatMapLatest { type ->
                val sortDirection = SortDirection.from(direction) ?: SortDirection.DEFAULT
                val sortType = SortType.from(type) ?: SortType.DEFAULT

                Pager(
                    config = PagingConfig(
                        pageSize = PagingUtils.PAGE_SIZE,
                        enablePlaceholders = false,
                    ),
                    pagingSourceFactory = {
                        when(sortDirection) {
                            SortDirection.ASC -> {
                                when (sortType) {
                                    SortType.NAME -> appDatabase.musicDao.getAllPagedByNameAsc()
                                    SortType.ADDED_DATE -> appDatabase.musicDao.getAllPagedByDateAsc()
                                    SortType.NB_PLAYED -> appDatabase.musicDao.getAllPagedByNbPlayedAsc()
                                }
                            }
                            SortDirection.DESC -> {
                                when (sortType) {
                                    SortType.NAME -> appDatabase.musicDao.getAllPagedByNameDesc()
                                    SortType.ADDED_DATE -> appDatabase.musicDao.getAllPagedByDateDesc()
                                    SortType.NB_PLAYED -> appDatabase.musicDao.getAllPagedByNbPlayedDesc()
                                }
                            }
                        }
                    }
                ).flow.map { pagingData ->
                    pagingData.map { it.toMusic() }
                }
            }
        }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        appDatabase.musicDao.getAllMusicFromAlbum(
            albumId = albumId
        ).map { it.toMusic() }

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        appDatabase.musicDao.updateMusicsAlbum(newAlbumId, legacyAlbumId)
    }

    override suspend fun cleanAllMusicCovers() {
        appDatabase.musicDao.cleanAllMusicCovers()
    }

    override suspend fun getAllMusicPath(): List<String> =
        appDatabase.musicDao.getAllMusicPath()

    override fun getMostListened(): Flow<List<Music>> =
        appDatabase.musicDao.getMostListened().map { list ->
            list.map { it.toMusic() }
        }

    override fun getAllMonthMusics(): Flow<List<MonthMusicsPreview>> =
        appDatabase.musicDao.getAllMonthMusics().map { list ->
            list.map { it.toMonthMusicsPreview() }
        }
}
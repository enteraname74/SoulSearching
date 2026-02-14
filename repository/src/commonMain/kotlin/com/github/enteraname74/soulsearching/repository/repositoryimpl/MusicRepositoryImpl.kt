package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.time.Duration

/**
 * Repository for handling Music related work.
 */
class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource,
) : MusicRepository {
    override suspend fun upsert(music: Music) {
        musicDataSource.upsert(music = music)
    }

    override suspend fun upsertAll(musics: List<Music>) {
        musicDataSource.upsertAll(musics = musics)
    }

    override suspend fun delete(music: Music) {
        musicDataSource.delete(music = music)
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        musicDataSource.deleteAll(ids = ids)
    }

    override suspend fun deleteAllFromUnselectedFolders() {
        musicDataSource.deleteAllFromUnselectedFolders()
    }

    override fun getFromId(musicId: UUID): Flow<Music?> = musicDataSource.getFromId(
        musicId = musicId
    )

    override suspend fun getAllIdsFromUnselectedFolders(): List<UUID> =
        musicDataSource.getAllIdsFromUnselectedFolders()

    @Deprecated("Avoid fetching all music from DB because of performance issue")
    override fun getAll(): Flow<List<Music>> =
        musicDataSource.getAll()

    override fun getAllFromQuickAccess(): Flow<List<Music>> =
        musicDataSource.getAllFromQuickAccess()

    override fun getAllPaged(): Flow<PagingData<Music>> =
        musicDataSource.getAllPaged()

    override fun getAllPagedOfAlbum(albumId: UUID): Flow<PagingData<Music>> =
        musicDataSource.getAllPagedOfAlbum(albumId)

    override fun getAllPagedByNameAscOfFolder(folder: String): Flow<PagingData<Music>> =
        musicDataSource.getAllPagedByNameAscOfFolder(folder)

    override fun getAllPagedByNameAscOfMonth(month: String): Flow<PagingData<Music>> =
        musicDataSource.getAllPagedByNameAscOfMonth(month)

    override fun getAllPagedByNameAscOfPlaylist(playlistId: UUID): Flow<PagingData<Music>> =
        musicDataSource.getAllPagedByNameAscOfPlaylist(playlistId)

    override fun getAllPagedByNameAscOfArtist(artistId: UUID): Flow<PagingData<Music>> =
        musicDataSource.getAllPagedByNameAscOfArtist(artistId)

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicDataSource.getAllMusicFromAlbum(
            albumId = albumId
        )

    override fun searchFromAlbum(
        albumId: UUID,
        search: String
    ): Flow<List<Music>> =
        musicDataSource.searchFromAlbum(
            albumId = albumId,
            search = search,
        )

    override fun searchFromPlaylist(
        playlistId: UUID,
        search: String
    ): Flow<List<Music>> =
        musicDataSource.searchFromPlaylist(
            playlistId = playlistId,
            search = search,
        )

    override fun searchFromArtist(
        artistId: UUID,
        search: String
    ): Flow<List<Music>> =
        musicDataSource.searchFromArtist(
            artistId = artistId,
            search = search,
        )

    override fun searchFromFolder(
        folder: String,
        search: String
    ): Flow<List<Music>> =
        musicDataSource.searchFromFolder(
            folder = folder,
            search = search,
        )

    override fun searchFromMonth(
        month: String,
        search: String
    ): Flow<List<Music>> =
        musicDataSource.searchFromMonth(
            month = month,
            search = search,
        )

    override fun searchAll(search: String): Flow<List<Music>> =
        musicDataSource.searchAll(search)

    override suspend fun getAllMusicFromArtist(artistId: UUID): List<Music> =
        musicDataSource.getAllMusicFromArtist(artistId)

    override suspend fun getAllMusicFromPlaylist(playlistId: UUID): List<Music> =
        musicDataSource.getAllMusicFromPlaylist(playlistId)

    override suspend fun getAllMusicFromMonth(month: String): List<Music> =
        musicDataSource.getAllMusicFromMonth(month)

    override suspend fun getAllMusicFromFolder(folder: String): List<Music> =
        musicDataSource.getAllMusicFromFolder(folder)

    override fun getAlbumDuration(albumId: UUID): Flow<Duration> =
        musicDataSource.getAlbumDuration(albumId)

    override fun getArtistDuration(artistId: UUID): Flow<Duration> =
        musicDataSource.getArtistDuration(artistId)

    override fun getPlaylistDuration(playlistId: UUID): Flow<Duration> =
        musicDataSource.getPlaylistDuration(playlistId)

    override fun getMonthMusicsDuration(month: String): Flow<Duration> =
        musicDataSource.getMonthMusicsDuration(month)

    override fun getFolderMusicsDuration(folder: String): Flow<Duration> =
        musicDataSource.getFolderMusicsDuration(folder)

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        musicDataSource.updateMusicsAlbum(newAlbumId, legacyAlbumId)
    }

    override suspend fun cleanAllMusicCovers() {
        musicDataSource.cleanAllMusicCovers()
    }

    override suspend fun getAllMusicPath(): List<String> =
        musicDataSource.getAllMusicPath()

    override fun getMostListened(): Flow<List<Music>> =
        musicDataSource.getMostListened()

    override fun getAllMonthMusics(): Flow<List<MonthMusicsPreview>> =
        musicDataSource.getAllMonthMusics()

    override fun getMonthMusicPreview(month: String): Flow<MonthMusicsPreview?> =
        musicDataSource.getMonthMusicPreview(month)

    override fun getAllMusicFolders(): Flow<List<MusicFolderPreview>> =
        musicDataSource.getAllMusicFolders()

    override fun getMusicFolderPreview(folder: String): Flow<MusicFolderPreview?> =
        musicDataSource.getMusicFolderPreview(folder)

    override suspend fun getSoulMixMusics(totalPerFolder: Int): List<Music> =
        musicDataSource.getSoulMixMusics(totalPerFolder)
}
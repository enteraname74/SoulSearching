package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for handling Music related work.
 */
class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource,
): MusicRepository {
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

    override suspend fun getAllMusicFromArtist(artistId: UUID): List<Music> =
        musicDataSource.getAllMusicFromArtist(artistId)

    override suspend fun getAllMusicFromPlaylist(playlistId: UUID): List<Music> =
        musicDataSource.getAllMusicFromPlaylist(playlistId)

    override suspend fun getAllMusicFromMonth(month: String): List<Music> =
        musicDataSource.getAllMusicFromMonth(month)

    override suspend fun getAllMusicFromFolder(folder: String): List<Music> =
        musicDataSource.getAllMusicFromFolder(folder)

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
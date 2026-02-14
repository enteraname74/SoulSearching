package com.github.enteraname74.domain.usecase.music

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import kotlin.time.Duration

class CommonMusicUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend fun deleteAll(ids: List<UUID>) {
        musicRepository.deleteAll(
            ids = ids,
        )
    }

    suspend fun deleteAllFromUnselectedFolders() {
        musicRepository.deleteAllFromUnselectedFolders()
    }

    suspend fun getAllIdsFromUnselectedFolders(): List<UUID> =
        musicRepository.getAllIdsFromUnselectedFolders()

    fun getAllFromQuickAccess(): Flow<List<Music>> =
        musicRepository.getAllFromQuickAccess()

    @Deprecated("Avoid fetching all music from DB because of performance issue")
    fun getAll(): Flow<List<Music>> =
        musicRepository.getAll()

    fun getAllPaged(): Flow<PagingData<Music>> =
        musicRepository.getAllPaged()

    fun getAllPagedOfAlbum(albumId: UUID): Flow<PagingData<Music>> =
        musicRepository.getAllPagedOfAlbum(albumId)

    fun getAllPagedByNameAscOfFolder(folder: String): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfFolder(folder)

    fun getAllPagedByNameAscOfMonth(month: String): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfMonth(month)

    fun getAllPagedByNameAscOfPlaylist(playlistId: UUID): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfPlaylist(playlistId)

    fun getAllPagedByNameAscOfArtist(artistId: UUID): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfArtist(artistId)

    fun getFromId(musicId: UUID): Flow<Music?> =
        musicRepository.getFromId(musicId = musicId)

    suspend fun getAllMusicFromMonth(month: String) : List<Music> =
        musicRepository.getAllMusicFromMonth(month)

    suspend fun getAllMusicFromFolder(folder: String) : List<Music> =
        musicRepository.getAllMusicFromFolder(folder)

    suspend fun getAllMusicFromArtist(artistId: UUID) : List<Music> =
        musicRepository.getAllMusicFromArtist(artistId)

    suspend fun getAllMusicFromPlaylist(playlistId: UUID) : List<Music> =
        musicRepository.getAllMusicFromPlaylist(playlistId)

    suspend fun getAllMusicFromAlbum(albumId: UUID) : List<Music> =
        musicRepository.getAllMusicFromAlbum(albumId)

    fun searchFromAlbum(
        albumId: UUID,
        search: String,
    ): Flow<List<Music>> =
        musicRepository.searchFromAlbum(
            albumId = albumId,
            search = search,
        )

    fun searchFromPlaylist(
        playlistId: UUID,
        search: String
    ): Flow<List<Music>> =
        musicRepository.searchFromPlaylist(
            playlistId = playlistId,
            search = search,
        )

    fun searchFromArtist(
        artistId: UUID,
        search: String
    ): Flow<List<Music>> =
        musicRepository.searchFromArtist(
            artistId = artistId,
            search = search,
        )

    fun searchFromFolder(
        folder: String,
        search: String
    ): Flow<List<Music>> =
        musicRepository.searchFromFolder(
            folder = folder,
            search = search,
        )

    fun searchFromMonth(
        month: String,
        search: String
    ): Flow<List<Music>> =
        musicRepository.searchFromMonth(
            month = month,
            search = search,
        )

    fun searchAll(
        search: String,
    ): Flow<List<Music>> =
        musicRepository.searchAll(search)
    
    fun getAlbumDuration(albumId: UUID): Flow<Duration> =
        musicRepository.getAlbumDuration(albumId)

    fun getArtistDuration(artistId: UUID): Flow<Duration> =
        musicRepository.getArtistDuration(artistId)

    fun getPlaylistDuration(playlistId: UUID): Flow<Duration> =
        musicRepository.getPlaylistDuration(playlistId)

    fun getMonthMusicsDuration(month: String): Flow<Duration> =
        musicRepository.getMonthMusicsDuration(month)

    fun getFolderMusicsDuration(folder: String): Flow<Duration> =
        musicRepository.getFolderMusicsDuration(folder)

    suspend fun incrementNbPlayed(musicId: UUID) {
        val music: Music = musicRepository.getFromId(musicId).first() ?: return
        musicRepository.upsert(
            music = music.copy(
                nbPlayed = music.nbPlayed + 1
            )
        )
    }

    suspend fun upsertAll(allMusics: List<Music>) {
        musicRepository.upsertAll(allMusics)
    }

    suspend fun upsert(music: Music) {
        musicRepository.upsert(music = music)
    }

    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        musicRepository.updateMusicsAlbum(newAlbumId, legacyAlbumId)
    }

    suspend fun cleanAllMusicCovers() {
        musicRepository.cleanAllMusicCovers()
    }

    suspend fun getAllMusicPath(): List<String> =
        musicRepository.getAllMusicPath()

    fun getMostListened(): Flow<List<Music>> =
        musicRepository.getMostListened()

    fun getAllMonthMusics(): Flow<List<MonthMusicsPreview>> =
        musicRepository.getAllMonthMusics()

    fun getMonthMusicPreview(month: String): Flow<MonthMusicsPreview?> =
        musicRepository.getMonthMusicPreview(month)

    fun getAllMusicFolders(): Flow<List<MusicFolderPreview>> =
        musicRepository.getAllMusicFolders()

    fun getMusicFolderPreview(folder: String): Flow<MusicFolderPreview?> =
        musicRepository.getMusicFolderPreview(folder)

    suspend fun getSoulMixMusics(totalPerFolder: Int): List<Music> =
        musicRepository.getSoulMixMusics(totalPerFolder)
}
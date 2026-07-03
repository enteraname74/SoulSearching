package com.github.enteraname74.domain.usecase.music

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.uuid.Uuid
import kotlin.time.Duration

class CommonMusicUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend fun deleteAll(ids: List<Uuid>) {
        musicRepository.deleteAll(
            ids = ids,
        )
    }

    suspend fun getAllIdsFromUnselectedFolders(): List<Uuid> =
        musicRepository.getAllIdsFromUnselectedFolders()

    fun getAllFromQuickAccess(): Flow<List<Music>> =
        musicRepository.getAllFromQuickAccess()

    @Deprecated("Avoid fetching all music from DB because of performance issue")
    fun getAll(): Flow<List<Music>> =
        musicRepository.getAll()

    suspend fun getAllLocalMusic(): List<Music> =
        musicRepository.getAllLocalMusic()

    suspend fun getAllSorted(): List<Music> =
        musicRepository.getAllSorted()

    fun getAllPaged(): Flow<PagingData<Music>> =
        musicRepository.getAllPaged()

    fun getAllPagedOfAlbum(albumId: Uuid): Flow<PagingData<Music>> =
        musicRepository.getAllPagedOfAlbum(albumId)

    fun getAllPagedByNameAscOfFolder(folder: String): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfFolder(folder)

    fun getAllPagedByNameAscOfMonth(month: String): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfMonth(month)

    fun getAllPagedByNameAscOfPlaylist(playlistId: Uuid): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfPlaylist(playlistId)

    fun getAllPagedByNameAscOfArtist(artistId: Uuid): Flow<PagingData<Music>> =
        musicRepository.getAllPagedByNameAscOfArtist(artistId)

    fun getFromId(musicId: Uuid): Flow<Music?> =
        musicRepository.getFromId(musicId = musicId)

    fun getFromIds(ids: List<Uuid>): Flow<List<Music>> =
        musicRepository.getFromIds(ids)

    suspend fun getAllMusicFromMonth(month: String) : List<Music> =
        musicRepository.getAllMusicFromMonth(month)

    suspend fun getAllMusicFromFolder(folder: String) : List<Music> =
        musicRepository.getAllMusicFromFolder(folder)

    suspend fun getAllMusicFromArtist(artistId: Uuid) : List<Music> =
        musicRepository.getAllMusicFromArtist(artistId)

    suspend fun getAllMusicFromPlaylist(playlistId: Uuid) : List<Music> =
        musicRepository.getAllMusicFromPlaylist(playlistId)

    suspend fun getAllMusicFromAlbum(albumId: Uuid) : List<Music> =
        musicRepository.getAllMusicFromAlbum(albumId)

    fun searchFromAlbum(
        albumId: Uuid,
        search: String,
    ): Flow<List<Music>> =
        musicRepository.searchFromAlbum(
            albumId = albumId,
            search = search,
        )

    fun searchFromPlaylist(
        playlistId: Uuid,
        search: String
    ): Flow<List<Music>> =
        musicRepository.searchFromPlaylist(
            playlistId = playlistId,
            search = search,
        )

    fun searchFromArtist(
        artistId: Uuid,
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
    
    fun getAlbumDuration(albumId: Uuid): Flow<Duration> =
        musicRepository.getAlbumDuration(albumId)

    fun getArtistDuration(artistId: Uuid): Flow<Duration> =
        musicRepository.getArtistDuration(artistId)

    fun getPlaylistDuration(playlistId: Uuid): Flow<Duration> =
        musicRepository.getPlaylistDuration(playlistId)

    fun getMonthMusicsDuration(month: String): Flow<Duration> =
        musicRepository.getMonthMusicsDuration(month)

    fun getFolderMusicsDuration(folder: String): Flow<Duration> =
        musicRepository.getFolderMusicsDuration(folder)

    suspend fun incrementNbPlayed(musicId: Uuid) {
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

    suspend fun updateMusicsAlbum(newAlbumId: Uuid, legacyAlbumId: Uuid) {
        musicRepository.updateMusicsAlbum(newAlbumId, legacyAlbumId)
    }

    suspend fun cleanAllMusicCovers() {
        musicRepository.cleanAllMusicCovers()
    }

    suspend fun getAllMusicLocalPath(): List<String> =
        musicRepository.getAllMusicLocalPath()

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

    suspend fun getFromPath(path: String): Music? =
        musicRepository.getFromPath(path)
}

package com.github.enteraname74.domain.usecase.music

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.MonthMusicsPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

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

    fun getFromId(musicId: UUID): Flow<Music?> =
        musicRepository.getFromId(musicId = musicId)

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

    fun getAllMusicFolders(): Flow<List<MusicFolderPreview>> =
        musicRepository.getAllMusicFolders()

    suspend fun getSoulMixMusics(totalPerFolder: Int): List<Music> =
        musicRepository.getSoulMixMusics(totalPerFolder)
}
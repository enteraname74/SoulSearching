package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

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

    override fun getFromId(musicId: UUID): Flow<Music?> = musicDataSource.getFromId(
        musicId = musicId
    )

    override suspend fun getFromPath(musicPath: String): Music? =
        musicDataSource.getFromPath(
            musicPath = musicPath,
        )

    override fun getAll(): Flow<List<Music>> =
        musicDataSource.getAll()

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicDataSource.getAllMusicFromAlbum(
            albumId = albumId
        )
}
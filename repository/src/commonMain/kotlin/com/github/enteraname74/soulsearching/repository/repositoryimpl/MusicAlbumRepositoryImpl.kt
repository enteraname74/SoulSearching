package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicAlbumDataSource
import java.util.*

/**
 * Repository of a MusicAlbum.
 */
class MusicAlbumRepositoryImpl(
    private val musicAlbumDataSource: MusicAlbumDataSource
): MusicAlbumRepository {
    override suspend fun upsertMusicIntoAlbum(musicAlbum: MusicAlbum) =
        musicAlbumDataSource.insertMusicIntoAlbum(
            musicAlbum = musicAlbum
        )

    override suspend fun deleteMusicFromAlbum(musicId: UUID) = musicAlbumDataSource.deleteMusicFromAlbum(
        musicId = musicId
    )

    override suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) =
        musicAlbumDataSource.updateAlbumOfMusic(
            musicId = musicId,
            newAlbumId = newAlbumId
        )

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) =
        musicAlbumDataSource.updateMusicsAlbum(
            newAlbumId = newAlbumId,
            legacyAlbumId = legacyAlbumId
        )

    override suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> =
        musicAlbumDataSource.getMusicsIdsFromAlbumId(
            albumId = albumId
        )

    override suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? =
        musicAlbumDataSource.getAlbumIdFromMusicId(
            musicId = musicId
        )

    override suspend fun getNumberOfMusicsFromAlbum(albumId: UUID): Int =
        musicAlbumDataSource.getNumberOfMusicsFromAlbum(
            albumId = albumId
        )
}
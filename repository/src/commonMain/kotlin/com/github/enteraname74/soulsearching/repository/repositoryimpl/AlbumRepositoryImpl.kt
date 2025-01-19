package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalDateTime
import java.util.*

/**
 * Repository of an Album.
 */
class AlbumRepositoryImpl(
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val albumRemoteDataSource: AlbumRemoteDataSource,
    private val dataModeDataSource: DataModeDataSource,
    private val cloudLocalDataSource: CloudLocalDataSource,
): AlbumRepository {

    override suspend fun delete(album: Album) {
        albumLocalDataSource.delete(album = album)
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        albumLocalDataSource.deleteAll(ids = ids)
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        albumLocalDataSource.deleteAll(dataMode = dataMode)
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumLocalDataSource.upsertAll(albums)
    }

    override suspend fun upsert(album: Album) = albumLocalDataSource.upsert(
        album = album
    )

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = albumLocalDataSource.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> =
        albumLocalDataSource.getAlbumsWithMusicsOfArtist(artistId)

    override fun getFromId(albumId: UUID): Flow<Album?> = albumLocalDataSource.getFromId(
        albumId = albumId
    )

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumLocalDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAll(): Flow<List<Album>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest {
                albumLocalDataSource.getAll(it)
            }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest {
                albumLocalDataSource.getAllAlbumWithMusics(it)
            }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest {
                albumLocalDataSource.getAllAlbumsWithArtist(it)
            }


    override suspend fun syncWithCloud(): SoulResult<Unit> {
        var currentPage = 0
        val lastUpdateDate: LocalDateTime? = cloudLocalDataSource.getLastUpdateDate()

        val idsToDeleteResult: SoulResult<List<UUID>> = albumRemoteDataSource.checkForDeletedAlbums(
            albumIds = albumLocalDataSource.getAll(dataMode = DataMode.Cloud).first().map { it.albumId }
        )

        val idsToDelete: List<UUID> = (idsToDeleteResult as? SoulResult.Success<List<UUID>>)?.data ?: emptyList()

        albumLocalDataSource.deleteAll(idsToDelete)

        while(true) {
            val songsFromCloud: SoulResult<List<Album>> = albumRemoteDataSource.fetchAlbumsFromCloud(
                after = lastUpdateDate,
                maxPerPage = MAX_ALBUMS_PER_PAGE,
                page = currentPage,
            )

            println("albumRepositoryImpl -- syncWithCloud -- got result: $songsFromCloud")

            when (songsFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(songsFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (songsFromCloud.data.isEmpty()) {
                        return SoulResult.ofSuccess()
                    } else {
                        currentPage += 1
                        albumLocalDataSource.upsertAll(
                            albums = songsFromCloud.data,
                        )
                    }

                }
            }
        }
    }
    companion object {
        private const val MAX_ALBUMS_PER_PAGE = 50
    }
}
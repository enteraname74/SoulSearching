package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.CloudRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumRemoteDataSource
import com.github.enteraname74.soulsearching.repository.utils.DeleteAllHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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
) : AlbumRepository, KoinComponent {
    private val cloudRepository: CloudRepository by inject()

    override suspend fun delete(album: Album): SoulResult<Unit> =
        when (album.dataMode) {
            DataMode.Local -> {
                albumLocalDataSource.delete(album = album)
                SoulResult.ofSuccess()
            }

            DataMode.Cloud -> {
                val result = albumRemoteDataSource.deleteAll(
                    albumIds = listOf(album.albumId),
                )
                cloudRepository.syncDataWithCloud()
                result
            }
        }

    override suspend fun deleteAll(ids: List<UUID>): SoulResult<Unit> =
        DeleteAllHelper.deleteAll(
            ids = ids,
            getAll = albumLocalDataSource::getAll,
            deleteAllLocal = albumLocalDataSource::deleteAll,
            deleteAllRemote = albumRemoteDataSource::deleteAll,
            mapIds = { it.albumId },
            getDataMode = { it.dataMode },
        )

    override suspend fun deleteAll(dataMode: DataMode) {
        albumLocalDataSource.deleteAll(dataMode = dataMode)
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumLocalDataSource.upsertAll(albums)
    }

    override suspend fun upsert(
        album: Album,
        artist: String,
    ): SoulResult<Unit> =
        when (album.dataMode) {
            DataMode.Local -> {
                albumLocalDataSource.upsert(
                    album = album
                )
                SoulResult.ofSuccess()
            }

            DataMode.Cloud -> {
                val result = albumRemoteDataSource.update(
                    album = album,
                    artist = artist,
                )
                cloudRepository.syncDataWithCloud()
                result
            }
        }

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
    override fun getAll(
        dataMode: DataMode?,
    ): Flow<List<Album>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest { currentDataMode ->
                albumLocalDataSource.getAll(
                    dataMode = dataMode ?: currentDataMode
                )
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

        while (true) {
            val albumsFromCloud: SoulResult<List<Album>> = albumRemoteDataSource.fetchAlbumsFromCloud(
                after = lastUpdateDate,
                maxPerPage = MAX_ALBUMS_PER_PAGE,
                page = currentPage,
            )

            println("albumRepositoryImpl -- syncWithCloud -- got result: $albumsFromCloud")

            when (albumsFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(albumsFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (albumsFromCloud.data.isEmpty()) {
                        return SoulResult.ofSuccess()
                    } else {
                        currentPage += 1
                        albumLocalDataSource.upsertAll(
                            albums = albumsFromCloud.data,
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
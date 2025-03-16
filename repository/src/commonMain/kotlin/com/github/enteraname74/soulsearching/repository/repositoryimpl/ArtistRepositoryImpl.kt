package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.CloudRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistRemoteDataSource
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
 * Repository of an Artist.
 */
class ArtistRepositoryImpl(
    private val artistLocalDataSource: ArtistLocalDataSource,
    private val artistRemoteDataSource: ArtistRemoteDataSource,
    private val dataModeDataSource: DataModeDataSource,
    private val cloudLocalDataSource: CloudLocalDataSource,
): ArtistRepository, KoinComponent {
    private val cloudRepository: CloudRepository by inject()

    override suspend fun upsert(artist: Artist): SoulResult<Unit> =
        when(artist.dataMode) {
            DataMode.Local -> {
                artistLocalDataSource.upsert(
                    artist = artist
                )
                SoulResult.ofSuccess()
            }
            DataMode.Cloud -> {
                val result = artistRemoteDataSource.update(artist)
                cloudRepository.syncDataWithCloud()
                result
            }
        }

    override suspend fun upsertAll(artists: List<Artist>) {
        artistLocalDataSource.upsertAll(artists)
    }

    /**
     * Deletes an Artist.
     */
    override suspend fun delete(artist: Artist): SoulResult<Unit> =
        when(artist.dataMode) {
            DataMode.Local -> {
                artistLocalDataSource.delete(
                    artist = artist
                )
                SoulResult.ofSuccess()
            }
            DataMode.Cloud -> {
                val result = artistRemoteDataSource.deleteAll(
                    artistIds = listOf(artist.artistId),
                )
                cloudRepository.syncDataWithCloud()
                result
            }
        }

    override suspend fun deleteAll(artistsIds: List<UUID>): SoulResult<Unit> =
        DeleteAllHelper.deleteAll(
            ids = artistsIds,
            getAll = artistLocalDataSource::getAll,
            deleteAllLocal = artistLocalDataSource::deleteAll,
            deleteAllRemote = artistRemoteDataSource::deleteAll,
            mapIds = { it.artistId },
            getDataMode = { it.dataMode },
        )

    override suspend fun deleteAll(dataMode: DataMode) {
        artistLocalDataSource.deleteAll(dataMode)
    }

    override suspend fun getArtistNamesContainingSearch(search: String): List<String> =
        artistLocalDataSource.getArtistNamesContainingSearch(search)

    /**
     * Retrieves an Artist from its id.
     */
    override fun getFromId(artistId: UUID): Flow<Artist?> = artistLocalDataSource.getFromId(
        artistId = artistId
    )

    override suspend fun getFromName(artistName: String): Artist? =
        artistLocalDataSource.getFromName(artistName = artistName)

    override suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        artistLocalDataSource.getAllFromName(artistsNames)

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAll(
        dataMode: DataMode?,
    ): Flow<List<Artist>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest { currentDataMode ->
                artistLocalDataSource.getAll(
                    dataMode = dataMode ?: currentDataMode
                )
            }


    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllArtistWithMusics(
        dataMode: DataMode?,
    ): Flow<List<ArtistWithMusics>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest { currentDataMode ->
                artistLocalDataSource.getAllArtistWithMusics(
                    dataMode = dataMode ?: currentDataMode
                )
            }


    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        artistLocalDataSource.getArtistWithMusics(
            artistId = artistId
        )

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        artistLocalDataSource.getArtistsOfMusic(musicId)

    override suspend fun syncWithCloud(): SoulResult<Unit> {
        var currentPage = 0
        val lastUpdateData: LocalDateTime? = cloudLocalDataSource.getLastUpdateDate()

        val idsToDeleteResult: SoulResult<List<UUID>> = artistRemoteDataSource.checkForDeletedArtists(
            artistIds = artistLocalDataSource.getAll(dataMode = DataMode.Cloud).first().map { it.artistId },
        )

        val idsToDelete: List<UUID> = (idsToDeleteResult as? SoulResult.Success<List<UUID>>)?.data ?: emptyList()
        artistLocalDataSource.deleteAll(idsToDelete)

        println("artistRepositoryImpl -- syncWithCloud -- lastUpdate: $lastUpdateData")

        while(true) {
            val artistFromCloud: SoulResult<List<Artist>> = artistRemoteDataSource.fetchArtistsFromCloud(
                after = lastUpdateData,
                maxPerPage = MAX_ARTISTS_PER_PAGE,
                page = currentPage,
            )

            println("artistRepositoryImpl -- syncWithCloud -- got result: $artistFromCloud")

            when (artistFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(artistFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (artistFromCloud.data.isEmpty()) {
                        return SoulResult.ofSuccess()
                    } else {
                        currentPage += 1
                        artistLocalDataSource.upsertAll(
                            artists = artistFromCloud.data,
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_ARTISTS_PER_PAGE = 50
    }
}
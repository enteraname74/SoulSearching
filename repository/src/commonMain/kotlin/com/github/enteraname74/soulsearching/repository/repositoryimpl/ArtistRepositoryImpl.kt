package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.util.FlowResult
import com.github.enteraname74.domain.util.handleFlowResultOn
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAccessor
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

/**
 * Repository of an Artist.
 */
class ArtistRepositoryImpl(
    private val artistLocalDataSource: ArtistLocalDataSource,
    private val artistRemoteDataSource: ArtistRemoteDataSource,
    private val dataModeDataSource: DataModeDataSource,
    private val cloudLocalDataSource: CloudLocalDataSource,
): ArtistRepository {
    /**
     * Inserts or updates an artist.
     */
    override suspend fun upsert(artist: Artist) = artistLocalDataSource.upsert(
        artist = artist
    )

    override suspend fun upsertAll(artists: List<Artist>) {
        artistLocalDataSource.upsertAll(artists)
    }

    /**
     * Deletes an Artist.
     */
    override suspend fun delete(artist: Artist) = artistLocalDataSource.delete(
        artist = artist
    )

    override suspend fun deleteAll(artistsIds: List<UUID>) {
        artistLocalDataSource.deleteAll(artistsIds)
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        artistLocalDataSource.deleteAll(dataMode)
    }

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
    override fun getAll(): Flow<List<Artist>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest {
                artistLocalDataSource.getAll(it)
            }


    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest {
                artistLocalDataSource.getAllArtistWithMusics(it)
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
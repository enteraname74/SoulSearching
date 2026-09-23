package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.soulsearching.domain.model.LocalMonthYear
import com.github.enteraname74.soulsearching.domain.model.statistics.LightListeningStatistics
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.domain.model.statistics.PeriodStatistics
import com.github.enteraname74.soulsearching.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.soulsearching.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.listeningstatistics.ListeningStatisticsLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.listeningstatistics.ListeningStatisticsRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class ListeningStatisticsRepositoryImpl(
    private val localDataSource: ListeningStatisticsLocalDataSource,
    private val remoteDataSource: ListeningStatisticsRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val playlistLocalDataSource: PlaylistLocalDataSource,
    private val musicLocalDataSource: MusicLocalDataSource,
    private val albumDataSource: AlbumDataSource,
    private val artistDataSource: ArtistDataSource,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
) : ListeningStatisticsRepository {
    override suspend fun upsert(listeningStatistics: ListeningStatistics) {
        localDataSource.upsert(listeningStatistics)
    }

    private fun <T, R> Map<T, R>.get(key: T?): R? =
        key?.let { this[it] }

    override suspend fun fetchFromFromCloud() {
        val lastUpdate = cloudPreferencesRepository.getLastStatsSyncMillis()
        val musicRemoteToLocalIds: Map<String, Uuid> = musicLocalDataSource.getAllRemoteToLocalIds()
        val albumRemoteToLocalIds: Map<Uuid, Uuid> = albumDataSource.getAllRemoteToLocalIds()
        val playlistRemoteToLocalIds: Map<Uuid, Uuid> = playlistLocalDataSource.getAllRemoteToLocalIds()
        val artistRemoteToLocalIds: Map<Uuid, Uuid> = artistDataSource.getAllRemoteToLocalIds()

        var page = 0
        val fetchedStats: MutableList<LightListeningStatistics> = mutableListOf()
        while (true) {
            val fetchedData = remoteDataSource.getOfUser(
                lastUpdateAt = lastUpdate,
                maxPerPage = MAX_STATS_PER_PAGE,
                page = page,
            )

            fetchedStats += fetchedData.mapNotNull { cloudData ->
                val localElementId: Uuid? = when {
                    cloudData.musicId != null -> musicRemoteToLocalIds[cloudData.musicId]
                    cloudData.albumId != null -> albumRemoteToLocalIds[cloudData.albumId]
                    cloudData.artistId != null -> artistRemoteToLocalIds[cloudData.artistId]
                    cloudData.playlistId != null -> playlistRemoteToLocalIds[cloudData.playlistId]
                    else -> null
                }

                localElementId?.let {
                    val localMonthYear = cloudData.localMonthYear.toDomain()

                    LightListeningStatistics(
                        id = "$localMonthYear-$it",
                        lastUpdatedMillis = cloudData.lastUpdateAtMillis,
                        nbPlayed = cloudData.nbPlayed,
                        timeListened = cloudData.timeListened,
                        localMonthYear = localMonthYear,
                        musicId = musicRemoteToLocalIds.get(cloudData.musicId),
                        playlistId = playlistRemoteToLocalIds.get(cloudData.playlistId),
                        albumId = albumRemoteToLocalIds.get(cloudData.albumId),
                        artistId = artistRemoteToLocalIds.get(cloudData.artistId),
                    )
                }
            }

            if (fetchedData.size < MAX_STATS_PER_PAGE) break

            page += 1
        }

        fetchedStats.chunked(UPSERT_CHUNK_SIZE).forEach {
            localDataSource.upsertLightListeningStatistics(it)
        }
    }

    override suspend fun upsertAllToCloud(
        toSend: List<ListeningStatistics>,
        onSent: (Int) -> Unit,
    ) {
        val userId = userLocalDataSource.observeUser().firstOrNull()?.id ?: return

        var sent = 0
        toSend.chunked(UPLOAD_CHUNK_SIZE).forEach { chunk ->
            remoteDataSource.upsertAll(
                statistics = chunk,
                userId = userId,
            )
            sent += chunk.size
            onSent(sent)
        }
    }

    override suspend fun getLastSyncMillis(): Long? =
        localDataSource.getLastSyncMillis()

    override suspend fun getAllToSendToCloud(): List<ListeningStatistics> =
        localDataSource.getAllToSendToCloud()

    override suspend fun getMusicStatistics(
        musicId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.MusicStats? =
        localDataSource.getMusicStatistics(
            musicId = musicId,
            localMonthYear = localMonthYear,
        )

    override suspend fun getAlbumStatistics(
        albumId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.AlbumStats? =
        localDataSource.getAlbumStatistics(
            albumId = albumId,
            localMonthYear = localMonthYear,
        )

    override suspend fun getArtistStatistics(
        artistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.ArtistStats? =
        localDataSource.getArtistStatistics(
            artistId = artistId,
            localMonthYear = localMonthYear,
        )

    override suspend fun getPlaylistStatistics(
        playlistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.PlaylistStats? =
        localDataSource.getPlaylistStatistics(
            playlistId = playlistId,
            localMonthYear = localMonthYear,
        )

    override fun getPeriodStatistics(period: Period): PeriodStatistics =
        localDataSource.getPeriodStatistics(period)

    override fun observeAllMonthPeriods(): Flow<List<Period.Month>> =
        localDataSource.observeAllMonthPeriods()

    override fun observeAllYearPeriods(): Flow<List<Period.Year>> =
        localDataSource.observeAllYearPeriods()

    private companion object {
        const val UPLOAD_CHUNK_SIZE = 500
        const val UPSERT_CHUNK_SIZE = 500
        const val MAX_STATS_PER_PAGE = 500
    }
}
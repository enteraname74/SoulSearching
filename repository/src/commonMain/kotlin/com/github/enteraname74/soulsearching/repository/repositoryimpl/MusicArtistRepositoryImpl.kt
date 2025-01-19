package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistRemoteDataSource
import java.time.LocalDateTime
import java.util.*

/**
 * Repository of a MusicArtist.
 */
class MusicArtistRepositoryImpl(
    private val musicArtistLocalDataSource: MusicArtistLocalDataSource,
    private val musicArtistRemoteDataSource: MusicArtistRemoteDataSource,
    private val cloudLocalDataSource: CloudLocalDataSource,
): MusicArtistRepository {
    override suspend fun getAll(dataMode: DataMode): List<MusicArtist> =
        musicArtistLocalDataSource.getAll(
            dataMode = dataMode,
        )

    override suspend fun get(artistId: UUID, musicId: UUID): MusicArtist? =
        musicArtistLocalDataSource.get(artistId, musicId)

    override suspend fun deleteAll(dataMode: DataMode) {
        musicArtistLocalDataSource.deleteAll(dataMode)
    }

    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) =
        musicArtistLocalDataSource.upsertMusicIntoArtist(
            musicArtist = musicArtist
        )

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        musicArtistLocalDataSource.upsertAll(musicArtists)
    }

    override suspend fun deleteMusicArtist(musicArtist: MusicArtist) {
        musicArtistLocalDataSource.deleteMusicArtist(musicArtist)
    }

    override suspend fun syncWithCloud(): SoulResult<Unit> {
        var currentPage = 0
        val lastUpdateDate: LocalDateTime? = cloudLocalDataSource.getLastUpdateDate()

        while(true) {
            val songsFromCloud: SoulResult<List<MusicArtist>> = musicArtistRemoteDataSource.fetchMusicArtistsFromCloud(
                after = lastUpdateDate,
                maxPerPage = MAX_MUSIC_ARTISTS_PER_PAGE,
                page = currentPage,
            )

            println("musicArtistRepositoryImpl -- syncWithCloud -- got result: $songsFromCloud")

            when (songsFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(songsFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (songsFromCloud.data.isEmpty()) {
                        return SoulResult.ofSuccess()
                    } else {
                        currentPage += 1
                        musicArtistLocalDataSource.upsertAll(
                            musicArtists = songsFromCloud.data,
                        )
                    }

                }
            }
        }
    }

    companion object {
        private const val MAX_MUSIC_ARTISTS_PER_PAGE = 50
    }
}
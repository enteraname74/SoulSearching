package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistRemoteDataSource
import java.util.*

/**
 * Repository of a MusicArtist.
 */
class MusicArtistRepositoryImpl(
    private val musicArtistLocalDataSource: MusicArtistLocalDataSource,
    private val musicArtistRemoteDataSource: MusicArtistRemoteDataSource,
) : MusicArtistRepository {
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

    override suspend fun delete(musicArtist: MusicArtist) {
        musicArtistLocalDataSource.deleteMusicArtist(musicArtist)
    }

    override suspend fun syncWithCloud(): SoulResult<Unit> {
        var currentPage = 0

        musicArtistLocalDataSource.deleteAll(DataMode.Cloud)

        while (true) {
            val musicArtistsFromCloud: SoulResult<List<MusicArtist>> =
                musicArtistRemoteDataSource.fetchMusicArtistsFromCloud(
                    after = null,
                    maxPerPage = MAX_MUSIC_ARTISTS_PER_PAGE,
                    page = currentPage,
                )

            println("musicArtistRepositoryImpl -- syncWithCloud -- got result: $musicArtistsFromCloud")

            when (musicArtistsFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(musicArtistsFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (musicArtistsFromCloud.data.isEmpty()) {
                        return SoulResult.ofSuccess()
                    } else {
                        currentPage += 1
                        musicArtistLocalDataSource.upsertAll(
                            musicArtists = musicArtistsFromCloud.data,
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
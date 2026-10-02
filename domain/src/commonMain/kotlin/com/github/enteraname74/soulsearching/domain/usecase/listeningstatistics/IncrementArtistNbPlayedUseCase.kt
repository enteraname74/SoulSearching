package com.github.enteraname74.soulsearching.domain.usecase.listeningstatistics

import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.repository.ArtistRepository
import com.github.enteraname74.soulsearching.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class IncrementArtistNbPlayedUseCase(
    private val artistRepository: ArtistRepository,
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    suspend operator fun invoke(artistId: Uuid) {
        val artist = artistRepository.getFromId(artistId).firstOrNull() ?: return
        val artistPreview = artistRepository.getArtistPreview(artistId).firstOrNull() ?: return

        artistRepository.upsert(
            artist.copy(nbPlayed = artist.nbPlayed + 1),
        )

        val existingStatistics = listeningStatisticsRepository.getArtistStatistics(
            artistId = artistId,
            localMonthYear = DateUtils.currentMonthYear(),
        ) ?: ListeningStatistics.ArtistStats.new(artistPreview)

        listeningStatisticsRepository.upsert(
            listeningStatistics = existingStatistics.increment(),
        )
    }
}
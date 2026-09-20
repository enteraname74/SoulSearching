package com.github.enteraname74.domain.usecase.listeningstatistics

import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class IncrementPlaylistNbPlayedUseCase(
    private val playlistRepository: PlaylistRepository,
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    suspend operator fun invoke(playlistId: Uuid) {
        val playlist = playlistRepository.getFromId(playlistId).firstOrNull() ?: return
        val playlistPreview = playlistRepository.getPlaylistPreview(playlistId).firstOrNull() ?: return

        playlistRepository.upsert(
            playlist.copy(nbPlayed = playlist.nbPlayed + 1),
        )

        val existingStatistics = listeningStatisticsRepository.getPlaylistStatistics(
            playlistId = playlistId,
            localMonthYear = DateUtils.currentMonthYear(),
        ) ?: ListeningStatistics.PlaylistStats.new(playlistPreview)

        listeningStatisticsRepository.upsert(
            listeningStatistics = existingStatistics.increment(),
        )
    }
}
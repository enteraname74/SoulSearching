package com.github.enteraname74.domain.usecase.listeningstatistics

import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class IncrementAlbumNbPlayedUseCase(
    private val albumRepository: AlbumRepository,
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    suspend operator fun invoke(albumId: Uuid) {
        val albumPreview = albumRepository.getAlbumPreview(albumId).firstOrNull() ?: return
        val album = albumRepository.getFromId(albumId).firstOrNull() ?: return

        albumRepository.upsert(album.copy(nbPlayed = album.nbPlayed + 1))

        val existingStatistics = listeningStatisticsRepository.getAlbumStatistics(
            albumId = albumId,
            localMonthYear = DateUtils.currentMonthYear(),
        ) ?: ListeningStatistics.AlbumStats.new(albumPreview)

        listeningStatisticsRepository.upsert(
            listeningStatistics = existingStatistics.increment(),
        )
    }
}
package com.github.enteraname74.domain.usecase.listeningstatistics

import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.util.DateUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class IncrementMusicNbPlayedUseCase(
    private val musicRepository: MusicRepository,
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    suspend operator fun invoke(musicId: Uuid) {
        val music = musicRepository.getFromId(musicId).firstOrNull() ?: return
        musicRepository.upsert(
            music.copy(nbPlayed = music.nbPlayed + 1),
        )

        val existingStatistics = listeningStatisticsRepository.getMusicStatistics(
            musicId = musicId,
            localMonthYear = DateUtils.currentMonthYear(),
        ) ?: ListeningStatistics.MusicStats.new(music)

        listeningStatisticsRepository.upsert(
            listeningStatistics = existingStatistics.increment(),
        )
    }
}
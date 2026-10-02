package com.github.enteraname74.soulsearching.domain.usecase.listeningstatistics

import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository
import com.github.enteraname74.soulsearching.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class IncrementCollectionNbPlayedUseCase(
    private val collectionRepository: CollectionRepository,
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    suspend operator fun invoke(collectionId: Uuid) {
        val collectionPreview = collectionRepository
            .getCollectionPreview(collectionId)
            .firstOrNull()
            ?: return

        collectionRepository.incrementNbPlayed(collectionId)

        val existingStatistics = listeningStatisticsRepository.getCollectionStatistics(
            collectionId = collectionId,
            localMonthYear = DateUtils.currentMonthYear(),
        ) ?: ListeningStatistics.CollectionStats.new(collectionPreview)

        listeningStatisticsRepository.upsert(
            listeningStatistics = existingStatistics.increment(),
        )
    }
}

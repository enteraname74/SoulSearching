package com.github.enteraname74.soulsearching.domain.model.statistics

import kotlin.time.Duration

data class PeriodGeneralStatistics(
    val mostPlayedMusic: ListeningStatistics.MusicStats?,
    val mostListenedMusic: ListeningStatistics.MusicStats?,
    val totalMusicsPlayed: Int,
    val totalUniqueMusicsPlayed: Int,
    val totalUniqueArtistsPlayed: Int,
    val totalListeningTime: Duration,
)

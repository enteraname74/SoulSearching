package com.github.enteraname74.localdb.model.listeningstatistics

import kotlin.time.Duration

data class RoomPeriodTotals(
    val totalMusicsPlayed: Int,
    val totalUniqueMusicsPlayed: Int,
    val totalUniqueArtistsPlayed: Int,
    val totalListeningTime: Duration,
)

package com.github.enteraname74.soulsearching.domain.model.statistics

import com.github.enteraname74.soulsearching.domain.model.LocalMonthYear
import kotlin.time.Duration
import kotlin.uuid.Uuid

data class LightListeningStatistics(
    val id: String,
    val lastUpdatedMillis: Long,
    val nbPlayed: Int,
    val timeListened: Duration?,
    val localMonthYear: LocalMonthYear,
    val musicId: Uuid?,
    val playlistId: Uuid?,
    val albumId: Uuid?,
    val artistId: Uuid?,
)

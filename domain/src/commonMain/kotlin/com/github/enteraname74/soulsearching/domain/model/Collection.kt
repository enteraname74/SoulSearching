package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class Collection(
    val collectionId: Uuid = Uuid.random(),
    val remoteId: Uuid? = null,
    val name: String,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long? = DateUtils.now(),
)

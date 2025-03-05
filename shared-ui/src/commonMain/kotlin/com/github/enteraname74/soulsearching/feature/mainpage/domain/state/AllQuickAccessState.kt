package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.QuickAccessible

/**
 * State for managing quick access.
 */
data class AllQuickAccessState(
    val allQuickAccess: List<QuickAccessible> = emptyList()
)

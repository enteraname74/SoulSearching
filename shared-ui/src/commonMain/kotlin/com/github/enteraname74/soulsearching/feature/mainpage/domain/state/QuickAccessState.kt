package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

/**
 * State for managing quick access.
 */
data class QuickAccessState(
    /**
     * The quick access is a list of anything (music, album...)
     */
    val allQuickAccess: List<Any> = emptyList()
)

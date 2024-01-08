package com.github.soulsearching.states

/**
 * State for managing quick access.
 */
data class QuickAccessState(
    /**
     * The quick access is a list of anything (music, album...)
     */
    val allQuickAccess: List<Any> = emptyList()
)

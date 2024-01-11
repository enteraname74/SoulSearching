package com.github.soulsearching.navigation

/**
 * Represent a route and its arguments.
 */
data class Route<T>(
    val route: T,
    val arguments: Map<String, Any> = emptyMap()
)

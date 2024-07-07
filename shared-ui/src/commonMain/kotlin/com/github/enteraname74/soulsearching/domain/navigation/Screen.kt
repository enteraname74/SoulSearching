package com.github.soulsearching.domain.navigation

import androidx.compose.runtime.Composable

/**
 * A Screen to be shown.
 */
data class Screen<T>(
    val screenRoute: T,
    val screen: @Composable () -> Unit
)
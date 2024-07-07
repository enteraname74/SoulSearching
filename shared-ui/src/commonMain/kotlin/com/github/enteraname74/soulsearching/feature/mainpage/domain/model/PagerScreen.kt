package com.github.enteraname74.soulsearching.feature.mainpage.domain.model

import androidx.compose.runtime.Composable

/**
 * Represent an item for a Pager.
 * It has a title and a composable linked to it.
 */
data class PagerScreen(
    val title : String,
    val screen: @Composable () -> Unit
)
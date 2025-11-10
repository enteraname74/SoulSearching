package com.github.enteraname74.soulsearching.domain.model

import androidx.compose.runtime.Composable

data class TabData(
    val title: String,
    val screen: @Composable () -> Unit,
)

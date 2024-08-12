package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationRowSpec(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

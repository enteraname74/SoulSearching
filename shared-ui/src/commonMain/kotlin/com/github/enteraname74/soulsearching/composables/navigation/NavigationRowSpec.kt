package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationRowSpec(
    val title: String,
    private val filledIcon: ImageVector,
    private val outlinedIcon: ImageVector,
    val isBadged: Boolean = false,
    val onClick: () -> Unit,
    val isSelected: Boolean,
) {
    val icon: ImageVector
        get() = if (isSelected) {
            filledIcon
        } else {
            outlinedIcon
        }
}

package com.github.enteraname74.soulsearching.composables.navigation

import org.jetbrains.compose.resources.DrawableResource

data class NavigationRowSpec(
    val title: String,
    private val filledIcon: DrawableResource,
    private val outlinedIcon: DrawableResource,
    val isBadged: Boolean = false,
    val onClick: () -> Unit,
    val isSelected: Boolean,
) {
    val icon: DrawableResource
        get() = if (isSelected) {
            filledIcon
        } else {
            outlinedIcon
        }
}

package com.github.enteraname74.domain.model

/**
 * Represent a Folder.
 * The isSelected value indicate if the folder is used in the application.
 */
data class Folder(
    val folderPath: String = "",
    val isSelected: Boolean = true
)
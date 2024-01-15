package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.sql.Table

/**
 * Table for storing Folders.
 */
internal object FolderTable: Table() {
    val folderPath = mediumText("folderPath")
    val isSelected = bool("isSelected")

    override val primaryKey = PrimaryKey(folderPath)
}
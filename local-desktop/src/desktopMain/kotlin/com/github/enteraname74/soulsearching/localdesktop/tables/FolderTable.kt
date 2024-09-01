package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Folder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

/**
 * Table for storing Folders.
 */
internal object FolderTable: Table() {
    val folderPath = mediumText("folderPath")
    val isSelected = bool("isSelected")

    override val primaryKey = PrimaryKey(folderPath)
}

/**
 * Builds a Folder from a ResultRow.
 */
internal fun ResultRow.toFolder(): Folder? =
    try {
        Folder(
            folderPath = this[FolderTable.folderPath],
            isSelected = this[FolderTable.isSelected]
        )
    } catch (_: Exception) {
        null
    }
package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.localdesktop.dao.FolderDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.FolderTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

/**
 * Implementation of the FolderDao for Exposed.
 */
class ExposedFolderDaoImpl: FolderDao{
    override suspend fun insertFolder(folder: Folder) {
        dbQuery {
            FolderTable.upsert {
                it[folderPath] = folder.folderPath
                it[isSelected] = folder.isSelected
            }
        }
    }

    override suspend fun deleteFolder(folder: Folder) {
       dbQuery {
           FolderTable.deleteWhere { folderPath eq folder.folderPath }
       }
    }

    override fun getAllFoldersAsFlow(): Flow<List<Folder>> = transaction {
        flowOf(
            FolderTable.selectAll().map(ExposedUtils::resultRowToFolder)
        )
    }

    override suspend fun getAllFolders(): List<Folder> = dbQuery {
        FolderTable.selectAll().map(ExposedUtils::resultRowToFolder)
    }

    override suspend fun getAllHiddenFoldersPaths(): List<String> = dbQuery {
        FolderTable
            .select(FolderTable.folderPath)
            .where { FolderTable.isSelected eq false}
            .map { it[FolderTable.folderPath] }
    }
}
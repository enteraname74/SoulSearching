package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.soulsearching.localdesktop.tables.FolderTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toFolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

internal class FolderDao {
    suspend fun upsert(folder: Folder) {
        flowTransactionOn {
            FolderTable.upsert {
                it[folderPath] = folder.folderPath
                it[isSelected] = folder.isSelected
            }
        }
    }

    suspend fun delete(folder: Folder) {
       flowTransactionOn {
           FolderTable.deleteWhere { folderPath eq folder.folderPath }
       }
    }

    fun getAll(): Flow<List<Folder>> = transaction {
        FolderTable
            .selectAll()
            .asFlow()
            .mapResultRow { it.toFolder() }
            .map { it.filterNotNull() }
    }
}
package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.soulsearching.localdesktop.tables.FolderTable
import com.github.enteraname74.soulsearching.localdesktop.tables.FolderTable.folderPath
import com.github.enteraname74.soulsearching.localdesktop.tables.FolderTable.isSelected
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.id
import com.github.enteraname74.soulsearching.localdesktop.tables.toFolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

internal class FolderDao {
    suspend fun upsert(folder: Folder) {
        flowTransactionOn {
            FolderTable.upsert {
                it[folderPath] = folder.folderPath
                it[isSelected] = folder.isSelected
            }
        }
    }

    suspend fun upsertAll(folders: List<Folder>) {
        flowTransactionOn {
            FolderTable.batchUpsert(folders) {
                this[folderPath] = it.folderPath
                this[isSelected] = it.isSelected
            }
        }
    }

    suspend fun delete(folder: Folder) {
       flowTransactionOn {
           FolderTable.deleteWhere { folderPath eq folder.folderPath }
       }
    }

    suspend fun deleteAll(folderPaths: List<String>) {
        flowTransactionOn {
            FolderTable.deleteWhere { Op.build { folderPath inList folderPaths }  }
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
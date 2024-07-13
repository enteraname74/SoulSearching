package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.ImageCoverTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toImageCover
import com.github.enteraname74.soulsearching.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

internal class ImageCoverDao {
    suspend fun upsert(imageCover: ImageCover) {
        flowTransactionOn {
            ImageCoverTable.upsert {
                if (imageCover.id != 0L) it[id] = imageCover.id
                it[coverId] = imageCover.coverId.toString()
                it[cover] = ExposedUtils.imageBitmapToString(imageCover.cover)
            }
        }
    }

    suspend fun delete(imageCover: ImageCover) {
        flowTransactionOn {
            ImageCoverTable.deleteWhere { coverId eq imageCover.coverId.toString() }
        }
    }

    suspend fun deleteFromCoverId(coverId: UUID) {
        flowTransactionOn {
            ImageCoverTable.deleteWhere { ImageCoverTable.coverId eq coverId.toString() }
        }
    }

    suspend fun getCoverOfElement(coverId: UUID): ImageCover? = dbQuery {
        ImageCoverTable
            .selectAll()
            .where { ImageCoverTable.coverId eq coverId.toString() }
            .map { it.toImageCover() }
            .firstOrNull()
    }

    fun getAll(): Flow<List<ImageCover>> = transaction {
        ImageCoverTable
            .selectAll()
            .asFlow()
            .mapResultRow { it.toImageCover() }
            .map { it.filterNotNull() }
    }
}
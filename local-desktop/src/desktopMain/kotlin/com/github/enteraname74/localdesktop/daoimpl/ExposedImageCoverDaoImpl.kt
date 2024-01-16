package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.localdesktop.dao.ImageCoverDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.ImageCoverTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the ImageCoverDao for Exposed.
 */
class ExposedImageCoverDaoImpl: ImageCoverDao {
    override suspend fun insertImageCover(imageCover: ImageCover) {
        dbQuery {
            ImageCoverTable.upsert {
                if (imageCover.id != 0L) it[id] = imageCover.id
                it[coverId] = imageCover.coverId.toString()
                it[cover] = ExposedUtils.imageBitmapToString(imageCover.cover)
            }
        }
    }

    override suspend fun deleteImageCover(imageCover: ImageCover) {
        dbQuery {
            ImageCoverTable.deleteWhere { coverId eq imageCover.coverId.toString() }
        }
    }

    override suspend fun deleteFromCoverId(coverId: UUID) {
        dbQuery {
            ImageCoverTable.deleteWhere { ImageCoverTable.coverId eq coverId.toString() }
        }
    }

    override suspend fun getCoverOfElement(coverId: UUID): ImageCover? = dbQuery {
        ImageCoverTable
            .selectAll()
            .where { ImageCoverTable.coverId eq coverId.toString() }
            .map(ExposedUtils::resultRowToImageCover)
            .singleOrNull()
    }

    override fun getAllCoversAsFlow(): Flow<List<ImageCover>> = transaction {
        flowOf(
            ImageCoverTable
                .selectAll()
                .map(ExposedUtils::resultRowToImageCover)
        )
    }
}
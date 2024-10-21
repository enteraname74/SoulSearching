package com.github.enteraname74.soulsearching.localdesktop.migration.impl

import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.localdesktop.migration.ExposedMigration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Base64
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class Migration16To17: ExposedMigration(
    forVersion = 16,
    toVersion = 17,
), KoinComponent {
    private val coverFileManager: CoverFileManager by inject()

    @OptIn(ExperimentalUuidApi::class)
    private fun Transaction.imageCoverMigration() {
        exec("SELECT coverId, cover FROM ImageCover") { resultSet ->
            val coverIdBytes: ByteArray = resultSet.getBytes("coverId")
            val coverId = Uuid.fromByteArray(coverIdBytes).toJavaUuid()

            val coverAsString = resultSet.getString("cover")

            if (!coverAsString.isNullOrEmpty()) {
                // Decode the Base64 string back to byte array
                val imageBytes = Base64.getDecoder().decode(coverAsString)

                CoroutineScope(Dispatchers.IO).launch {
                    coverFileManager.saveCover(id = coverId, data = imageBytes)
                }
            }
        }
        exec("DROP TABLE IF EXISTS ImageCover")
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun Transaction.migrate() {
        imageCoverMigration()
    }
}
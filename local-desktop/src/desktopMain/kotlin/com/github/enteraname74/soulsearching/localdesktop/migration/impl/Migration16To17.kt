package com.github.enteraname74.soulsearching.localdesktop.migration.impl

import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.localdesktop.migration.ExposedMigration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class Migration16To17: ExposedMigration(
    forVersion = 16,
    toVersion = 17,
), KoinComponent {
    private val coverFileManager: CoverFileManager by inject()

    private fun Transaction.imageCoverMigration() {
        exec("SELECT coverId, cover FROM ImageCover") { resultSet ->
            while (resultSet.next()) {
                val coverId = UUID.fromString(resultSet.getString("coverId"))
                val coverAsBytes: ByteArray = resultSet.getBytes("cover")

                if (coverAsBytes.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        coverFileManager.saveCover(id = coverId, data = coverAsBytes)
                    }
                }
            }
        }
        exec("DROP TABLE IF EXISTS ImageCover")
    }

    override fun Transaction.migrate() {
        imageCoverMigration()
    }
}
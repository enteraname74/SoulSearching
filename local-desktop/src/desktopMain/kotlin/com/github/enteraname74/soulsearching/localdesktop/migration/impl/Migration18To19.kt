package com.github.enteraname74.soulsearching.localdesktop.migration.impl

import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import com.github.enteraname74.soulsearching.localdesktop.migration.ExposedMigration
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Migration18To19: ExposedMigration(
    forVersion = 18,
    toVersion = 19,
), KoinComponent {
    private val musicMetadataHelper by inject<MusicMetadataHelper>()

    override fun Transaction.migrate() {
        MusicTable
            .select(MusicTable.id, MusicTable.path)
            .forEach { row ->
                val id = row[MusicTable.id]
                val path = row[MusicTable.path]

                val albumPosition: Int? = musicMetadataHelper.getMusicAlbumPosition(musicPath = path)

                MusicTable.update({ MusicTable.id eq id}) {
                    it[MusicTable.albumPosition] = albumPosition
                }
            }
    }
}
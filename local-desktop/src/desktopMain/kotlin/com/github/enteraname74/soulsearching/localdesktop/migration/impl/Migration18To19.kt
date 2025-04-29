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

    private fun Transaction.mxnTableMigration(
        tableName: String,
        mColumnName: String,
        nColumnName: String,
        mTableName: String,
        nTableName: String,
    ) {
        exec(
            """
            CREATE TABLE ${tableName}_new (
                id VARCHAR(256) PRIMARY KEY NOT NULL,
                $mColumnName UUID NOT NULL,
                $nColumnName UUID NOT NULL,
                FOREIGN KEY (${mColumnName}) REFERENCES ${mTableName}(id) ON DELETE CASCADE,
                FOREIGN KEY (${nColumnName}) REFERENCES ${nTableName}(id) ON DELETE CASCADE
            )
        """
        )

        exec(
            """
            INSERT INTO ${tableName}_new (id, ${mColumnName}, ${nColumnName})
            SELECT $mColumnName || $nColumnName, ${mColumnName}, $nColumnName FROM $tableName
        """
        )

        exec("DROP TABLE $tableName")
        exec("ALTER TABLE ${tableName}_new RENAME TO $tableName")

        exec("CREATE INDEX index_${tableName}_${mColumnName} ON ${tableName}(${mColumnName})")
        exec("CREATE INDEX index_${tableName}_${nColumnName} ON ${tableName}(${nColumnName})")
    }

    private fun Transaction.musicArtistMigration() {
        mxnTableMigration(
            tableName = "MusicArtist",
            mColumnName = "musicId",
            nColumnName = "artistId",
            mTableName = "Music",
            nTableName = "Artist",
        )
    }

    private fun Transaction.musicPlaylistMigration() {
        mxnTableMigration(
            tableName = "MusicPlaylist",
            mColumnName = "musicId",
            nColumnName = "playlistId",
            mTableName = "Music",
            nTableName = "Playlist"
        )
    }

    override fun Transaction.migrate() {
        musicArtistMigration()
        musicPlaylistMigration()

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
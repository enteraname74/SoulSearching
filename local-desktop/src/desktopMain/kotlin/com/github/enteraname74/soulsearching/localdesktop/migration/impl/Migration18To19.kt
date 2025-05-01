package com.github.enteraname74.soulsearching.localdesktop.migration.impl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.features.filemanager.util.MetadataField
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicDao
import com.github.enteraname74.soulsearching.localdesktop.migration.ExposedMigration
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    private val musicDao by inject<MusicDao>()

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

    private suspend fun musicMigration() {
        val allMusics: List<Music> = musicDao.getAll().first()
        val metadata: Map<String, Map<MetadataField, String>> = musicMetadataHelper.getMetadataFromPaths(
            musicPaths = allMusics.map { it.path },
            fields = listOf(
                MetadataField.Track,
                MetadataField.AlbumArtist,
            )
        )
        musicDao.upsertAll(
            musics = allMusics.map { music ->
                val fields: Map<MetadataField, String>? = metadata[music.path]
                fields?.let {
                    music.copy(
                        albumArtist = fields[MetadataField.AlbumArtist],
                        albumPosition = fields[MetadataField.Track]?.toIntOrNull()
                    )
                } ?: music
            }
        )
    }

    override fun Transaction.migrate() {
        musicArtistMigration()
        musicPlaylistMigration()
        runBlocking {
            musicMigration()
        }
    }
}
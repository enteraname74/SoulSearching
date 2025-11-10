package com.github.enteraname74.localdb.migration

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion

class EndMigrationCallback(
    private val settings: SoulSearchingSettings,
): RoomDatabase.Callback() {
    override fun onOpen(connection: SQLiteConnection) {
        super.onOpen(connection)
        settings.set(
            key = SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION.key,
            value = LocalDatabaseVersion.VERSION,
        )
    }
}
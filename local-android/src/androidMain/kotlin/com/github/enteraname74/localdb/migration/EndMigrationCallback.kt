package com.github.enteraname74.localdb.migration

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion

class EndMigrationCallback(
    private val settings: SoulSearchingSettings,
): RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        settings.set(
            key = SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION.key,
            value = LocalDatabaseVersion.version,
        )
    }
}
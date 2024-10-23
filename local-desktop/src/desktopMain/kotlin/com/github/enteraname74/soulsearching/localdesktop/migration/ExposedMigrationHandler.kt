package com.github.enteraname74.soulsearching.localdesktop.migration

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExposedMigrationHandler(
    vararg migrations: ExposedMigration,
): KoinComponent {
    private val settings: SoulSearchingSettings by inject()
    private val migrations: List<ExposedMigration> = migrations.toList()

    fun doMigrations() {
        var currentDbVersion = settings.get(
            settingElement = SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION,
        )

        while(currentDbVersion < LocalDatabaseVersion.version) {
            println("EXPOSED MIGRATION -- Current DB version is $currentDbVersion")
            val migration: ExposedMigration? = migrations.find { it.forVersion == currentDbVersion }

            println("EXPOSED MIGRATION -- Got migration to do: $migration")

            if (migration == null) {
                // We skip it
                settings.set(
                    key = SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION.key,
                    value = currentDbVersion + 1,
                )
            } else {
                migration.doMigration()
            }
            currentDbVersion = settings.get(
                settingElement = SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION,
            )
        }
    }
}
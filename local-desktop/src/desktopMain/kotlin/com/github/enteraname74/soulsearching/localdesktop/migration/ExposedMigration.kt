package com.github.enteraname74.soulsearching.localdesktop.migration

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class ExposedMigration(
    val forVersion: Int,
    val toVersion: Int,
): KoinComponent {
    private val settings: SoulSearchingSettings by inject()

    fun doMigration() = transaction {
        try {
            migrate()
        } catch (e: Exception) {
            println("EXPOSED MIGRATION -- Error while migrating from $forVersion to $toVersion: ${e.message}")
        }
        settings.set(
            key = SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION.key,
            value = toVersion,
        )
    }

    abstract fun Transaction.migrate()
}
package com.github.enteraname74.localdb

import org.jetbrains.exposed.sql.Database

/**
 * Database of the application.
 */
object AppDatabase {

    /**
     * Establish a connection with the database.
     */
    fun connectToDatabase() {
        Database.connect("jdbc:sqlite:database.db", "org.sqlite.JDBC")
    }
}
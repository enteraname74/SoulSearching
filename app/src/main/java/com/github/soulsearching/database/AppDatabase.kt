package com.github.soulsearching.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.soulsearching.database.converters.Converters
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.model.Music


@Database(
    entities = [Music::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val musicDao: MusicDao
}
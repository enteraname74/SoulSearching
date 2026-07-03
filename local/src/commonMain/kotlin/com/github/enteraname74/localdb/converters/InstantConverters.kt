package com.github.enteraname74.localdb.converters

import androidx.room.TypeConverter
import kotlin.time.Instant

/**
 * Used for converting complex types to more simple ones for the database.
 */
internal object InstantConverters {
    @TypeConverter
    fun instantToLong(date: Instant): Long =
        date.toEpochMilliseconds()

    @TypeConverter
    fun longToInstant(timestamp: Long): Instant =
        Instant.fromEpochMilliseconds(timestamp)
}

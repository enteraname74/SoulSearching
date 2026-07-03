package com.github.enteraname74.localdb.converters

import androidx.room3.ColumnTypeConverter
import kotlin.time.Instant

/**
 * Used for converting complex types to more simple ones for the database.
 */
internal object InstantConverters {
    @ColumnTypeConverter
    fun instantToLong(date: Instant): Long =
        date.toEpochMilliseconds()

    @ColumnTypeConverter
    fun longToInstant(timestamp: Long): Instant =
        Instant.fromEpochMilliseconds(timestamp)
}

package com.github.enteraname74.localdb.converters

import androidx.room3.ColumnTypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object DurationConverters {

    @ColumnTypeConverter
    fun durationToMillis(duration: Duration): Long =
        duration.inWholeMilliseconds

    @ColumnTypeConverter
    fun millisToDuration(milliseconds: Long): Duration =
        milliseconds.milliseconds
}
package com.github.enteraname74.localdb.migration.ext

import androidx.room.util.getColumnIndex
import androidx.sqlite.SQLiteStatement


fun SQLiteStatement.getId(field: String): ByteArray =
    getBlob(getColumnIndex(this,field))


fun SQLiteStatement.getString(field: String): String =
    getText(getColumnIndex(this,field))


fun SQLiteStatement.getInt(field: String): Int =
    getInt(getColumnIndex(this,field))


fun SQLiteStatement.getBool(field: String): Boolean =
    getInt(getColumnIndex(this,field)) == 1
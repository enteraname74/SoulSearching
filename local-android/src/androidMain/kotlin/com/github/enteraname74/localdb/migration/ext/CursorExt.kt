package com.github.enteraname74.localdb.migration.ext

import android.annotation.SuppressLint
import android.database.Cursor

@SuppressLint("Range")
fun Cursor.getId(field: String): ByteArray =
    getBlob(getColumnIndex(field))

@SuppressLint("Range")
fun Cursor.getString(field: String): String =
    getString(getColumnIndex(field))

@SuppressLint("Range")
fun Cursor.getInt(field: String): Int =
    getInt(getColumnIndex(field))

@SuppressLint("Range")
fun Cursor.getBool(field: String): Boolean =
    getInt(getColumnIndex(field)) == 1
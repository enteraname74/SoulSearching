package com.github.enteraname74.localdb.migration.ext

fun ByteArray.toSQLId(): String {
    return "X'" + joinToString("") { "%02x".format(it) } + "'"
}
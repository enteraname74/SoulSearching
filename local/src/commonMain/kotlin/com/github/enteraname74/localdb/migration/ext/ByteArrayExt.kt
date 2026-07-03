package com.github.enteraname74.localdb.migration.ext

fun ByteArray.toSQLId(): String {
    return "X'" + joinToString("") { byte ->
        byte.toUByte().toString(16).padStart(2, '0')
    } + "'"
}

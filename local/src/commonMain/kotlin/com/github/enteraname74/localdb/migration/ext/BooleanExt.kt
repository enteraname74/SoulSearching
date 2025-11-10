package com.github.enteraname74.localdb.migration.ext

fun Boolean.toSQLValue(): String =
    if (this) "1" else "0"
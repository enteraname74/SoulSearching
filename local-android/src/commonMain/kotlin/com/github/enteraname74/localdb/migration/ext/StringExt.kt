package com.github.enteraname74.localdb.migration.ext

fun String?.toSQLValue(): String =
    this?.let { "'${it.replace("'", "''")}'" } ?: "NULL"
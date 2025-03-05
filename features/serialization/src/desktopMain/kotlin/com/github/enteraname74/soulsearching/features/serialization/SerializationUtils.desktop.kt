package com.github.enteraname74.soulsearching.features.serialization

import com.google.gson.Gson

actual object SerializationUtils {
    val gson = Gson()
    
    actual inline fun <reified T> deserialize(json: String): T =
        gson.fromJson(json, T::class.java)

    actual inline fun <reified T> serialize(obj: T): String =
        gson.toJson(obj)
}
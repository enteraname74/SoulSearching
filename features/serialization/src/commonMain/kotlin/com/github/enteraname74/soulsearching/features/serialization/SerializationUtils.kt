package com.github.enteraname74.soulsearching.features.serialization

import kotlinx.serialization.json.Json

object SerializationUtils {
    val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    inline fun <reified T> deserialize(json: String): T =
        JSON.decodeFromString(json)

    inline fun <reified T> serialize(obj: T): String =
        JSON.encodeToString(obj)
}
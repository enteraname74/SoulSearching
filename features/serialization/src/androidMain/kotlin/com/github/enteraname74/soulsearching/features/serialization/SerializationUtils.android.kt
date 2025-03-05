package com.github.enteraname74.soulsearching.features.serialization

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual object SerializationUtils {
    val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    actual inline fun <reified T> deserialize(json: String): T =
        JSON.decodeFromString(json)

    actual inline fun <reified T> serialize(obj: T): String =
        JSON.encodeToString(obj)
}
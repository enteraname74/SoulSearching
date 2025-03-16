package com.github.enteraname74.soulsearching.features.serialization

expect object SerializationUtils {
    inline fun <reified T> deserialize(json: String): T
    inline fun <reified T> serialize(obj: T): String
}
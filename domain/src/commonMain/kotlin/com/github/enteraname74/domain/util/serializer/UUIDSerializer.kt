package com.github.enteraname74.domain.util.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "UUID",
        PrimitiveKind.STRING,
    )

    override fun deserialize(decoder: Decoder): UUID {
        val string = decoder.decodeString()
        return UUID.fromString(string)
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object UUIDListSerializer : KSerializer<List<UUID>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "UUID",
        PrimitiveKind.STRING,
    )

    override fun deserialize(decoder: Decoder): List<UUID> {
        val string = decoder.decodeString()
        val list: List<String> = Json.decodeFromString(string)
        return list.map { UUID.fromString(it) }
    }

    override fun serialize(encoder: Encoder, value: List<UUID>) {
        encoder.encodeString(Json.encodeToString(value.map { it.toString() }))
    }
}
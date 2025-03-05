package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import com.github.enteraname74.domain.model.Artist
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@Serializable
sealed interface MultipleArtistsChoiceMode {
    @Serializable
    data object InitialFetch : MultipleArtistsChoiceMode

    @Serializable
    data class NewSongs(
        val multipleArtists: List<Artist>
    ) : MultipleArtistsChoiceMode

    @Serializable
    data object GeneralCheck : MultipleArtistsChoiceMode

    fun serialize(): String =
        jsonSerializer.encodeToString(
            serializer = serializer(),
            value = this,
        )

    companion object {
        private val jsonSerializer: Json
            get() = Json {
                serializersModule = SerializersModule {
                    polymorphic(
                        MultipleArtistsChoiceMode::class,
                        InitialFetch::class,
                        InitialFetch.serializer(),
                    )
                    polymorphic(
                        MultipleArtistsChoiceMode::class,
                        GeneralCheck::class,
                        GeneralCheck.serializer(),
                    )
                    polymorphic(
                        MultipleArtistsChoiceMode::class,
                        NewSongs::class,
                        NewSongs.serializer(),
                    )
                }
            }



        fun deserialize(string: String): MultipleArtistsChoiceMode =
            jsonSerializer.decodeFromString(
                deserializer = serializer(),
                string = string,
            )
    }
}


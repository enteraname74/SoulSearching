package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.enteraname74.soulsearching.domain.model.Artist
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.textfield.*
import kotlin.uuid.Uuid
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

sealed interface ModifyMusicFormState {
    data object NoData : ModifyMusicFormState
    data class Data(
        private val initialMusic: Music,
        private val artistsOfMusic: List<Artist>,
        private val updateFoundAlbums: suspend (name: String) -> List<String>,
        private val updateFoundArtists: suspend (name: String) -> List<String>,
        private val onDeleteArtist: (artistId: Uuid) -> Unit,
        private val savedData: Map<String, String>,
        private val onFieldChange: (id: String, value: String) -> Unit,
        private val workDispatcher: WorkDispatcher,
    ) : ModifyMusicFormState {
        val textFields: List<SoulTextFieldHolder> = buildList {
            add(
                SoulTextFieldHolderImpl(
                    id = MUSIC_NAME,
                    initialValue = savedData[MUSIC_NAME] ?: initialMusic.name,
                    isValid = { it.isNotBlank() },
                    getLabel = { CoreRes.string.music_name.toTextSpec() },
                    style = SoulTextFieldStyle.Top,
                    getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
                    onChange = {
                        onFieldChange(MUSIC_NAME, it)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                    )
                )
            )
            add(
                SoulDropdownTextFieldHolderImpl(
                    id = ALBUM_NAME,
                    isValid = { it.isNotBlank() },
                    initialValue = savedData[ALBUM_NAME] ?: initialMusic.album.albumName,
                    updateProposedValues = updateFoundAlbums,
                    workDispatcher = workDispatcher,
                    getLabel = { CoreRes.string.album_name.toTextSpec() },
                    style = SoulTextFieldStyle.Body,
                    getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
                    onChange = {
                        onFieldChange(ALBUM_NAME, it)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                    )
                )
            )
            add(
                SoulTextFieldHolderImpl(
                    id = POSITION_IN_ALBUM,
                    isValid = { it.toIntOrNull() != null || it.isBlank() },
                    initialValue = savedData[POSITION_IN_ALBUM] ?: initialMusic.albumPosition?.toString().orEmpty(),
                    getLabel = { CoreRes.string.music_album_position.toTextSpec() },
                    style = SoulTextFieldStyle.Body,
                    getError = { CoreRes.string.field_must_be_anumber.toTextSpec() },
                    onChange = {
                        onFieldChange(POSITION_IN_ALBUM, it)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number,
                    )
                )
            )
            add(
                SoulDropdownTextFieldHolderImpl(
                    id = ALBUM_ARTIST,
                    initialValue = savedData[ALBUM_ARTIST] ?: initialMusic.album.artist.artistName,
                    getLabel = { CoreRes.string.album_artist_name.toTextSpec() },
                    style = SoulTextFieldStyle.Body,
                    getError = { null },
                    onChange = {
                        onFieldChange(ALBUM_ARTIST, it)
                    },
                    updateProposedValues = updateFoundArtists,
                    workDispatcher = workDispatcher,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                    )
                )
            )

            artistsOfMusic.forEachIndexed { index, artist ->
                val artistId = artist.artistId.toString()

                add(
                    SoulDropdownTextFieldHolderImpl(
                        id = artistId,
                        isValid = { it.isNotBlank() },
                        initialValue = savedData[artistId] ?: artist.artistName,
                        updateProposedValues = updateFoundArtists,
                        workDispatcher = workDispatcher,
                        getLabel = { CoreRes.string.artist_name.toTextSpec() },
                        style = if (index == artistsOfMusic.lastIndex) {
                            SoulTextFieldStyle.Bottom
                        } else {
                            SoulTextFieldStyle.Body
                        },
                        keyboardOptions = if (index == artistsOfMusic.lastIndex) {
                            KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            )
                        } else {
                            KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                            )
                        },
                        getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
                        leadingIconSpec = if (artistsOfMusic.size == 1) {
                            null
                        } else {
                            SoulTextFieldLeadingIconSpec(
                                icon =  CoreRes.drawable.ic_delete_filled,
                                onClick = {
                                    onDeleteArtist(artist.artistId)
                                }
                            )
                        },
                        onChange = {
                            onFieldChange(artistId, it)
                        }
                    )
                )
            }
        }

        fun isFormValid(): Boolean = textFields.all { it.isValid() }

        fun getMusicName(): String = textFields.first { it.id == MUSIC_NAME }.value
        fun getAlbumName(): String = textFields.first { it.id == ALBUM_NAME }.value
        fun getAlbumPosition(): String = textFields.first { it.id == POSITION_IN_ALBUM }.value
        fun getAlbumArtist(): String = textFields.first { it.id == ALBUM_ARTIST }.value
        fun getArtistsName(): List<String> = (4 until textFields.size).map { textFields[it].value }

        companion object {
            private const val MUSIC_NAME = "MUSIC_NAME"
            private const val ALBUM_NAME = "ALBUM_NAME"
            private const val POSITION_IN_ALBUM = "POSITION_IN_ALBUM"
            private const val ALBUM_ARTIST = "ALBUM_ARTIST"
        }
    }
}

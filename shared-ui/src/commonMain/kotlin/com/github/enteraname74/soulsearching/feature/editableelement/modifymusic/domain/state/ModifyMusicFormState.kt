package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.*
import java.util.UUID

sealed interface ModifyMusicFormState {
    data object NoData : ModifyMusicFormState
    data class Data(
        private val initialMusic: Music,
        private val artistsOfMusic: List<Artist>,
        private val updateFoundAlbums: suspend (name: String) -> List<String>,
        private val updateFoundArtists: suspend (name: String) -> List<String>,
        private val onDeleteArtist: (artistId: UUID) -> Unit,
        private val savedData: Map<String, String>,
        private val onFieldChange: (id: String, value: String) -> Unit,
    ) : ModifyMusicFormState {
        val textFields: List<SoulTextFieldHolder> = buildList {
            add(
                SoulTextFieldHolderImpl(
                    modifier = Modifier
                        .fillMaxWidth(),
                    id = MUSIC_NAME,
                    initialValue = savedData[MUSIC_NAME] ?: initialMusic.name,
                    isValid = { it.isNotBlank() },
                    getLabel = { strings.musicName },
                    style = SoulTextFieldStyle.Top,
                    getError = { strings.fieldCannotBeEmpty },
                    onChange = {
                        onFieldChange(MUSIC_NAME, it)
                    }
                )
            )
            add(
                SoulDropdownTextFieldHolderImpl(
                    modifier = Modifier
                        .fillMaxWidth(),
                    id = ALBUM_NAME,
                    isValid = { it.isNotBlank() },
                    initialValue = savedData[ALBUM_NAME] ?: initialMusic.album,
                    updateProposedValues = updateFoundAlbums,
                    getLabel = { strings.albumName },
                    style = SoulTextFieldStyle.Body,
                    getError = { strings.fieldCannotBeEmpty },
                    onChange = {
                        onFieldChange(ALBUM_NAME, it)
                    }
                )
            )

            artistsOfMusic.forEachIndexed { index, artist ->
                val artistId = artist.artistId.toString()

                add(
                    SoulDropdownTextFieldHolderImpl(
                        modifier = Modifier
                            .fillMaxWidth(),
                        id = artistId,
                        isValid = { it.isNotBlank() },
                        initialValue = savedData[artistId] ?: artist.artistName,
                        updateProposedValues = updateFoundArtists,
                        getLabel = { strings.artistName },
                        style = if (index == artistsOfMusic.lastIndex) {
                            SoulTextFieldStyle.Bottom
                        } else {
                            SoulTextFieldStyle.Body
                        },
                        getError = { strings.fieldCannotBeEmpty },
                        leadingIconSpec = if (index == 0) {
                            null
                        } else {
                            SoulTextFieldLeadingIconSpec(
                                icon = Icons.Rounded.Delete,
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

        fun getMusicName(): String = textFields[0].value
        fun getAlbumName(): String = textFields[1].value
        fun getArtistsName(): List<String> = (2 until textFields.size).map { textFields[it].value }

        companion object {
            private const val MUSIC_NAME = "MUSIC_NAME"
            private const val ALBUM_NAME = "ALBUM_NAME"
        }
    }
}
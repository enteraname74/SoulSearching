package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import java.util.*

class UpdateAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicRepository: MusicRepository,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend operator fun invoke(
        updateInformation: UpdateInformation
    ): Album {
        var albumArtistToSave = updateInformation.legacyAlbum.artist
        if (updateInformation.newArtistName != updateInformation.legacyAlbum.artist.artistName) {
            // We first try to find if there is an existing artist with the new artist name.
            var newArtist = artistRepository.getFromName(
                artistName = updateInformation.newArtistName
            )
            // If this artist doesn't exist, we create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = updateInformation.newArtistName,
                    cover = updateInformation.newCover,
                )
                artistRepository.upsert(
                    artist = newArtist
                )
            }
            albumArtistToSave = newArtist
        }

        // We then check if there is an album with the same name and artist that already exist.
        val duplicateAlbum: Album? = commonAlbumUseCase.getDuplicatedAlbum(
            albumId = updateInformation.legacyAlbum.albumId,
            albumName = updateInformation.newName,
            artistId = albumArtistToSave.artistId
        )

        val updatedAlbum = updateInformation.legacyAlbum.copy(
            artist = albumArtistToSave,
            isInQuickAccess = updateInformation.legacyAlbum.isInQuickAccess
                    || duplicateAlbum?.isInQuickAccess == true,
            cover = updateInformation.newCover,
        )

        // If so, we need to merge the two album.
        if (duplicateAlbum != null) mergeAlbums(from = duplicateAlbum, to = updatedAlbum)

        // We then need to update the musics of the album (new artist, album name and cover).
        updateMusicsOfAlbum(
            newAlbum = updatedAlbum,
            legacyArtist = updateInformation.legacyAlbum.artist,
            newArtist = albumArtistToSave,
        )

        // Finally, we can update the information of the album.
        albumRepository.upsert(updatedAlbum)

        // We check and delete the initial artist if it no longer possess songs.
        commonArtistUseCase.deleteIfEmpty(artistId = updateInformation.legacyAlbum.artist.artistId)

        return updatedAlbum
    }

    private suspend fun replaceArtistOfMusic(
        music: Music,
        legacyArtistId: UUID,
        newArtistId: UUID,
    ) {
        // We first remove the link to the legacy artist
        musicArtistRepository.deleteMusicArtist(
            musicArtist = MusicArtist(
                musicId = music.musicId,
                artistId = legacyArtistId,
            )
        )
        // And we add a link to the new one if it does not exist already
        val existingLink: MusicArtist? = musicArtistRepository.get(
            artistId = newArtistId,
            musicId = music.musicId,
        )

        if (existingLink == null) {
            musicArtistRepository.upsertMusicIntoArtist(
                musicArtist = MusicArtist(
                    musicId = music.musicId,
                    artistId = newArtistId,
                )
            )
        }
    }

    private suspend fun updateMusicsOfAlbum(
        newAlbum: Album,
        newArtist: Artist,
        legacyArtist: Artist,
    ) {
        val musicsFromAlbum = musicRepository.getAllMusicFromAlbum(
            albumId = newAlbum.albumId
        )

        for (music in musicsFromAlbum) {
            val newMusic = music.copy(
                album = newAlbum,
                cover = (music.cover as? Cover.CoverFile)?.copy(
                    fileCoverId = (newAlbum.cover as? Cover.CoverFile)?.fileCoverId
                        ?: (music.cover as? Cover.CoverFile)?.fileCoverId
                ) ?: music.cover,
            )

            musicRepository.upsert(newMusic)

            if (newArtist.artistId != legacyArtist.artistId) {
                replaceArtistOfMusic(
                    music = music,
                    legacyArtistId = legacyArtist.artistId,
                    newArtistId = newArtist.artistId,
                )
            }
        }
    }

    /**
     * Merge two albums together.
     * @param from the album to put to the "to" album.
     * @param to the album that will receive the merge ("from" param)/
     */
    private suspend fun mergeAlbums(from: Album, to: Album) {
        // We update the link of the musics of the duplicated album to the new album id.
        musicRepository.updateMusicsAlbum(
            newAlbumId = to.albumId,
            legacyAlbumId = from.albumId
        )
        albumRepository.delete(
            album = from
        )
    }

    data class UpdateInformation(
        val legacyAlbum: Album,
        val newName: String,
        val newArtistName: String,
        val newCover: Cover?,
    )
}
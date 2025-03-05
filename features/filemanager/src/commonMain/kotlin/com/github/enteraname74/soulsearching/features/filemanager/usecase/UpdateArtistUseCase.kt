package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.domain.usecase.artist.GetDuplicatedArtistUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class UpdateArtistUseCase(
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val updateArtistNameOfMusicUseCase: UpdateArtistNameOfMusicUseCase,
    private val getDuplicatedArtistUseCase: GetDuplicatedArtistUseCase,
) {
    suspend operator fun invoke(newArtistWithMusicsInformation: ArtistWithMusics) {
        val legacyArtist: Artist = artistRepository.getFromId(newArtistWithMusicsInformation.artist.artistId).firstOrNull() ?: return

        artistRepository.upsert(
            newArtistWithMusicsInformation.artist
        )

        // We redirect the possible artists albums that do not share the same artist id.
        redirectAlbumsToCorrectArtist(artist = newArtistWithMusicsInformation.artist)

        for (music in newArtistWithMusicsInformation.musics) {
            updateArtistNameOfMusicUseCase(
                music = music,
                legacyArtistName = legacyArtist.artistName,
                newArtistName = newArtistWithMusicsInformation.artist.artistName,
            )
        }

        // We check if there is not two times the artist name.
        val possibleDuplicatedArtist: ArtistWithMusics? =
            getDuplicatedArtistUseCase(
                artistName = newArtistWithMusicsInformation.artist.artistName,
                artistId = newArtistWithMusicsInformation.artist.artistId
            )

        // If so, we merge the duplicate one to the current artist.
        if (possibleDuplicatedArtist != null) {
            mergeArtists(
                from = possibleDuplicatedArtist,
                to = newArtistWithMusicsInformation.artist,
            )
        }

        // We update the quick access value of the new artist:
        artistRepository.upsert(
            newArtistWithMusicsInformation.artist.copy(
                isInQuickAccess = newArtistWithMusicsInformation.artist.isInQuickAccess ||
                possibleDuplicatedArtist?.isInQuickAccess == true
            )
        )
    }

    /**
     * Redirect the albums of an artist with the same name to the correct artist id.
     */
    private suspend fun redirectAlbumsToCorrectArtist(artist: Artist) {
        val legacyAlbumsOfArtist = albumRepository.getAllAlbumWithMusics().first().filter {
            it.artist?.artistName == artist.artistName
        }

        /*
         If, once the artist name was changed,
         we have two albums with the same album and artist name,
         we redirect the album's musics that do not have the same artist's id as the actual one:
         */
        val albumsOrderedByAppearance =
            legacyAlbumsOfArtist.groupingBy { it.album.albumName }.eachCount()

        for (entry in albumsOrderedByAppearance.entries) {
            val albumWithMusicToUpdate = legacyAlbumsOfArtist.find {
                (it.album.albumName == entry.key)
                        && (it.artist!!.artistId != artist.artistId)
            }
            if (entry.value == 2) {
                // The album has a duplicate!
                // We redirect the album's songs to the one with the actual artist id.
                for (music in albumWithMusicToUpdate!!.musics) {
                    musicAlbumRepository.updateAlbumOfMusic(
                        musicId = music.musicId,
                        newAlbumId = legacyAlbumsOfArtist.find {
                            (it.album.albumName == entry.key)
                                    && (it.artist!!.artistId == artist.artistId)
                        }!!.album.albumId
                    )
                }
                // We delete the previous album
                albumRepository.delete(
                    albumWithMusicToUpdate.album
                )
            } else if (albumWithMusicToUpdate != null) {
                // Else, we update the artist's id.
                albumArtistRepository.update(
                    albumId = albumWithMusicToUpdate.album.albumId,
                    newArtistId = artist.artistId
                )
            }
        }
    }

    /**
     * Merge two artist together.
     * @param from the artist to put to the "to" artist.
     * @param to the artist that will receive the merge ("from" param)/
     */
    private suspend fun mergeArtists(from: ArtistWithMusics, to: Artist) {
        for (music in from.musics) {
            // We first remove the link to the legacy artist
            musicArtistRepository.deleteMusicArtist(
                musicArtist = MusicArtist(
                    musicId = music.musicId,
                    artistId = from.artist.artistId,
                )
            )
            // And we add a link to the new one if it does not exist already
            val existingLink: MusicArtist? = musicArtistRepository.get(
                artistId = to.artistId,
                musicId = music.musicId,
            )

            if (existingLink == null) {
                musicArtistRepository.upsertMusicIntoArtist(
                    musicArtist = MusicArtist(
                        musicId = music.musicId,
                        artistId = to.artistId,
                    )
                )
            }
        }

        // We delete the previous artist.
        artistRepository.delete(
            from.artist
        )
    }
}
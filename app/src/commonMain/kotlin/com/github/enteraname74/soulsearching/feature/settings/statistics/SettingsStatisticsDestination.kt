package com.github.enteraname74.soulsearching.feature.settings.statistics

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.model.MusicListDetailId
import com.github.enteraname74.soulsearching.feature.musiclistdetail.MusicListDetailDestination
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Serializable
data object SettingsStatisticsDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsStatisticsDestination> {
            val holder: SettingsStatisticsViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsStatisticsNavScope {
                    override fun toAlbum(albumId: Uuid) {
                        navigator.push(MusicListDetailDestination(MusicListDetailId.Album(albumId)))
                    }

                    override fun toArtist(artistId: Uuid) {
                        navigator.push(MusicListDetailDestination(MusicListDetailId.Artist(artistId)))
                    }

                    override fun toPlaylist(playlistId: Uuid) {
                        navigator.push(MusicListDetailDestination(MusicListDetailId.Playlist(playlistId)))
                    }

                    override fun navigateBack() {
                        navigator.pop()
                    }
                }
            )
        }
    }
}
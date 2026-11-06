package com.github.enteraname74.soulsearching.feature.settings.statistics.model

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.albums
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.artists
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.collections
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.most_plays
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.most_songs
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.musics
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.playlists
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.time_listened
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import kotlinx.coroutines.flow.Flow

sealed interface ListStatistics {
    val type: Type
    val list: Flow<PagingData<StatisticsUiElement>>

    sealed interface Choices : ListStatistics {
        val currentChoice: Type
        val choices: List<Type>
        val onChoiceChange: (Type) -> Unit

        sealed interface Type {
            val title: SoulTextSpec

            enum class Musics(
                override val title: SoulTextSpec,
            ) : Type {
                MostPlays(CoreRes.string.most_plays.toTextSpec()),
                MostListened(CoreRes.string.time_listened.toTextSpec()),
            }

            enum class Artists(
                override val title: SoulTextSpec,
            ) : Type {
                MostPlays(CoreRes.string.most_plays.toTextSpec()),
                MostSongs(CoreRes.string.most_songs.toTextSpec()),
            }
        }
    }

    data class Musics(
        override val list: Flow<PagingData<StatisticsUiElement>>,
        override val currentChoice: Choices.Type.Musics,
        override val onChoiceChange: (Choices.Type) -> Unit
    ) : Choices {
        override val type: Type = Type.Musics
        override val choices: List<Choices.Type.Musics> = Choices.Type.Musics.entries
    }

    data class Artists(
        override val list: Flow<PagingData<StatisticsUiElement>>,
        override val currentChoice: Choices.Type.Artists,
        override val onChoiceChange: (Choices.Type) -> Unit,
        override val choices: List<Choices.Type.Artists>,
    ) : Choices {
        override val type: Type = Type.Artists
    }

    data class Albums(
        override val list: Flow<PagingData<StatisticsUiElement>>
    ) : ListStatistics {
        override val type: Type = Type.Albums
    }

    data class Playlists(
        override val list: Flow<PagingData<StatisticsUiElement>>
    ) : ListStatistics {
        override val type: Type = Type.Playlists
    }

    data class Collections(
        override val list: Flow<PagingData<StatisticsUiElement>>
    ) : ListStatistics {
        override val type: Type = Type.Collections
    }

    enum class Type(val sectionTitle: SoulTextSpec) {
        Musics(CoreRes.string.musics.toTextSpec()),
        Artists(CoreRes.string.artists.toTextSpec()),
        Albums(CoreRes.string.albums.toTextSpec()),
        Playlists(CoreRes.string.playlists.toTextSpec()),
        Collections(CoreRes.string.collections.toTextSpec()),
    }
}

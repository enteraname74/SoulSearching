package com.github.soulsearching.states

import com.github.soulsearching.database.model.ArtistWithMusics

data class SelectedArtistState(
    val artistWithMusics : ArtistWithMusics = ArtistWithMusics()
)
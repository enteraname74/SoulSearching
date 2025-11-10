package com.github.enteraname74.soulsearching.feature.mainpage.domain.model

import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType

data class SortingInformation(
    val type: SortType,
    val direction: SortDirection,
)

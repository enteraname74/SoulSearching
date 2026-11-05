package com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
/**
 * Information about a developer on the SoulSearching project.
 */
data class Developer(
    val name: String,
    val function: SoulTextSpec,
    val link: String,
)

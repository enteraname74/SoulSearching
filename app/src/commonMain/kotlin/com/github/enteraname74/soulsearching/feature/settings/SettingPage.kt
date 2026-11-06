package com.github.enteraname74.soulsearching.feature.settings

import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

interface SettingPage : NavKey {
    val title: SoulTextSpec?
        get() = null
}

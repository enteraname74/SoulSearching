package com.github.enteraname74.soulsearching.coreui.strings

import org.jetbrains.compose.resources.StringResource

fun StringResource.toTextSpec(vararg args: Any): SoulTextSpec =
    SoulTextSpec.Resource(this, *args)

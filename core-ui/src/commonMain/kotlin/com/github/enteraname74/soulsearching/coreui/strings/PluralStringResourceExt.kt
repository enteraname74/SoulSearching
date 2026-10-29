package com.github.enteraname74.soulsearching.coreui.strings

import org.jetbrains.compose.resources.PluralStringResource

fun PluralStringResource.toTextSpec(quantity: Int, vararg args: Any): SoulTextSpec =
    SoulTextSpec.Plural(this, quantity, *args)

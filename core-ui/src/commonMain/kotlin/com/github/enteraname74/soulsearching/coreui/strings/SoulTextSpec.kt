package com.github.enteraname74.soulsearching.coreui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Describes a text to show, resolved at display time.
 * Arguments that are themselves [SoulTextSpec] are resolved before formatting.
 */
@Immutable
sealed interface SoulTextSpec {
    @get:Composable
    val string: String

    suspend fun suspendedString(): String

    class Resource(
        val resource: StringResource,
        vararg val args: Any,
    ) : SoulTextSpec {
        override val string: String
            @Composable
            get() = stringResource(resource, *args.resolved())

        override suspend fun suspendedString(): String =
            getString(resource, *args.suspendedResolved())

        override fun equals(other: Any?): Boolean =
            other is Resource && other.resource == resource && other.args.contentEquals(args)

        override fun hashCode(): Int = 31 * resource.hashCode() + args.contentHashCode()
    }

    class Plural(
        val resource: PluralStringResource,
        val quantity: Int,
        vararg val args: Any,
    ) : SoulTextSpec {
        override val string: String
            @Composable
            get() = pluralStringResource(resource, quantity, *args.resolved())

        override suspend fun suspendedString(): String =
            getPluralString(resource, quantity, *args.suspendedResolved())

        override fun equals(other: Any?): Boolean =
            other is Plural &&
                other.resource == resource &&
                other.quantity == quantity &&
                other.args.contentEquals(args)

        override fun hashCode(): Int =
            31 * (31 * resource.hashCode() + quantity) + args.contentHashCode()
    }

    data class Raw(
        val text: String,
    ) : SoulTextSpec {
        override val string: String
            @Composable
            get() = text

        override suspend fun suspendedString(): String = text
    }
}

@Composable
private fun Array<out Any>.resolved(): Array<Any> =
    Array(size) { index ->
        when (val arg = this[index]) {
            is SoulTextSpec -> arg.string
            else -> arg
        }
    }

private suspend fun Array<out Any>.suspendedResolved(): Array<Any> =
    Array(size) { index ->
        when (val arg = this[index]) {
            is SoulTextSpec -> arg.suspendedString()
            else -> arg
        }
    }

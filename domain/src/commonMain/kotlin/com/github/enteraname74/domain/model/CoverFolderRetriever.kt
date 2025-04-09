package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable

typealias RuleMapper = (String) -> String

@Serializable
data class CoverFolderRetriever(
    val folderPath: String,
    val coverFileName: String,
    val whiteSpaceReplacement: String?,
    val isLowerCase: Boolean?,
    val mode: DynamicMode,
) {

    fun String.applyWhiteSpaceRule(): String =
        whiteSpaceReplacement?.let { replacement ->
            replace(" ", replacement)
        } ?: this

    fun String.applyLowerCaseRule(): String =
        isLowerCase?.let { lowerCase ->
            if (lowerCase) {
                lowercase()
            } else {
                uppercase()
            }
        } ?: this

    fun buildDynamicCoverPath(dynamicName: String): String {
        var finalDynamicName = dynamicName
            .applyLowerCaseRule()
            .applyWhiteSpaceRule()

        return when(mode) {
            DynamicMode.Folder -> "$folderPath/$finalDynamicName/$coverFileName"
            DynamicMode.File -> "$folderPath/$finalDynamicName.png"
        }
    }

    enum class DynamicMode {
        Folder,
        File;
    }
}
package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CoverFolderRetriever(
    val isActivated: Boolean = true,
    val folderPath: String,
    val filePath: String,
    val coverFileName: String,
    val whiteSpaceRule: WhiteSpaceRule,
    val lowerCaseRule: Boolean?,
    val mode: DynamicMode,
) {

    fun String.applyWhiteSpaceRule(): String =
        whiteSpaceRule.takeIf { it.isActivated }?.replacement?.let { replacement ->
            replace(" ", replacement)
        } ?: this

    fun String.applyLowerCaseRule(): String =
        lowerCaseRule?.let { lowerCase ->
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
            DynamicMode.File -> "$filePath/$finalDynamicName.png"
        }
    }

    enum class DynamicMode {
        Folder,
        File;
    }

    @Serializable
    data class WhiteSpaceRule(
        val isActivated: Boolean,
        val replacement: String,
    )
}
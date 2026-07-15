plugins {
    `kotlin-dsl`
}

group = "com.github.enteraname74.soulsearching.buildlogic"

dependencies {
    implementation(libs.gradle)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.compose.multiplatform.get()}")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:${libs.versions.ksp.get()}")
    implementation("androidx.room3:room3-gradle-plugin:${libs.versions.room.get()}")
}

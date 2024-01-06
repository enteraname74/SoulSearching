import com.github.enteraname74.buildsrc.Dependencies

plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(Dependencies.COROUTINES_CORE)
    implementation(Dependencies.INJECT_API)
}
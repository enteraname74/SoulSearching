// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlinSerialization) apply false
}
repositories {
    maven { url = uri("offline-repository") }
    google()
    mavenCentral()
}

val flatpakOnlyArch = providers.gradleProperty("flatpakOnlyArch").orElse("host")
val flatpakDependencyDirectory =
    layout.projectDirectory.dir("flatpak/dependencies/${flatpakOnlyArch.get()}")
val flatpakSourcesFile =
    layout.projectDirectory.file("flatpak/flatpak-sources-${flatpakOnlyArch.get()}.json")

val buildLogicFlatpakDependencies =
    gradle.includedBuild("build-logic").task(":convention:flatpakGradleGenerator")

val generateFlatpakDependencies = tasks.register("generateFlatpakDependencies") {
    group = "flatpak"
    description = "Generates and merges offline Gradle sources for one Linux architecture."

    dependsOn(buildLogicFlatpakDependencies)

    outputs.file(flatpakSourcesFile)

    doFirst {
        val arch = flatpakOnlyArch.get()
        require(arch == "x86_64" || arch == "aarch64") {
            "Set -PflatpakOnlyArch=x86_64 or -PflatpakOnlyArch=aarch64"
        }
    }

    doLast {
        val sourceEntries =
            flatpakDependencyDirectory.asFile
                .listFiles { file -> file.extension == "json" }
                .orEmpty()
                .sortedBy { it.name }
                .flatMap { file ->
                    file.readText()
                        .trim()
                        .removePrefix("[")
                        .removeSuffix("]")
                        .trim()
                        .takeIf(String::isNotEmpty)
                        ?.let(::listOf)
                        .orEmpty()
                }

        flatpakSourcesFile.asFile.apply {
            parentFile.mkdirs()
            writeText(sourceEntries.joinToString(",\n", "[\n", "\n]\n"))
        }
    }
}

gradle.projectsEvaluated {
    generateFlatpakDependencies.configure {
        dependsOn(
            subprojects.mapNotNull {
                it.tasks.findByName("flatpakGradleGenerator")
            }
        )
    }
}

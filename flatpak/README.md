# Flathub build

This directory contains the source-building manifest intended for local
Flathub validation. The existing release-archive manifest under `app/` remains
unchanged.

## Generate offline Gradle sources

Generate each dependency list on Linux with the matching CPU architecture:

```shell
./gradlew generateFlatpakDependencies \
  --no-configuration-cache \
  -PflatpakOnlyArch=x86_64
```

```shell
./gradlew generateFlatpakDependencies \
  --no-configuration-cache \
  -PflatpakOnlyArch=aarch64
```

Each command replaces the corresponding `flatpak-sources-<arch>.json` file.
The checked-in empty lists are placeholders and are not sufficient for an
offline build.

The generator must run natively on Linux because Compose Desktop and SQLite
resolve platform-specific artifacts. Do not generate either Flathub source
list on macOS.

## Update the application source

Before building or submitting, update `commit` in
`io.github.enteraname74.soulsearching.yml` to the full commit hash that
contains the Flatpak and XDG changes.

## Build and lint on Linux

```shell
flatpak install -y flathub org.flatpak.Builder
flatpak run --command=flathub-build org.flatpak.Builder \
  --install io.github.enteraname74.soulsearching.yml
flatpak run io.github.enteraname74.soulsearching
flatpak run --command=flatpak-builder-lint org.flatpak.Builder \
  manifest io.github.enteraname74.soulsearching.yml
flatpak run --command=flatpak-builder-lint org.flatpak.Builder repo repo
```

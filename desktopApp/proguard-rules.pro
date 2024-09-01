# FileKit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

# VLC
-keep class uk.co.caprica.vlcj.** { *; }

# Exposed
-keep class org.jetbrains.exposed.** { *; }
-keep class org.sqlite.** { *; }

-keep class io.ktor.serialization.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Jaudiotagger
-keep class org.jaudiotagger.** { *; }

-dontwarn *
-ignorewarnings
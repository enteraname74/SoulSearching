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

-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-dontnote kotlinx.serialization.SerializationKt

# Keep Serializers
-keep,includedescriptorclasses class com.company.package.**$$serializer { *; }
-keepclassmembers class com.company.package.** {
    *** Companion;
}
-keepclasseswithmembers class com.company.package.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# When kotlinx.serialization.json.JsonObjectSerializer occurs
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep, allowobfuscation, allowoptimization @kotlinx.serialization.Serializable class *

-dontwarn *
-ignorewarnings
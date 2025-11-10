# FileKit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

# VLC
-keep class uk.co.caprica.vlcj.** { *; }

-keep class io.ktor.serialization.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Jaudiotagger
-keep class org.jaudiotagger.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class androidx.sqlite.** { *; }

-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-dontnote kotlinx.serialization.SerializationKt

# Keep Serializers
# Kotlinx serialization, included by androidx.navigation
# https://github.com/Kotlin/kotlinx.serialization/blob/master/rules/common.pro
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$* Companion;
}
-keepnames @kotlinx.serialization.internal.NamedCompanion class *
-if @kotlinx.serialization.internal.NamedCompanion class *
-keepclassmembernames class * {
    static <1> *;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-dontnote kotlinx.serialization.**
-dontwarn kotlinx.serialization.internal.ClassValueReferences
-keepclassmembers public class **$$serializer {
    private ** descriptor;
}

# Kotlinx serialization, additional rules
# Fixes:
#   Exception in thread "main" kotlinx.serialization.SerializationException: Serializer for class 'SomeClass' is not found.
#   Please ensure that class is marked as '@Serializable' and that the serialization compiler plugin is applied.
-keep class **$$serializer {
    *;
}

-dontwarn *
-ignorewarnings
-printusage
# Alterna SDK Consumer ProGuard Rules
# These rules will be applied to apps that use this SDK

# Keep all SDK public classes and methods
-keep public class com.alterna.sdk.** { *; }
-keep public interface com.alterna.sdk.** { *; }

# Keep SDK main class
-keep class com.alterna.sdk.SDK {
    public *;
}

# Keep all callback interfaces
-keep interface com.alterna.sdk.callbacks.** { *; }

# Keep model classes for serialization
-keep class com.alterna.sdk.models.** { *; }

# Keep UI activities (they will be referenced from manifest)
-keep class com.alterna.sdk.ui.** { *; }

# Keep WPS handler
-keep class com.alterna.sdk.wps.** { *; }

# Keep attributes for reflection
-keepattributes Signature
-keepattributes *Annotation*

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

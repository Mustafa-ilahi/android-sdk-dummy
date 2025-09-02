# Alterna SDK ProGuard Rules

# Keep all public classes and methods in the SDK package
-keep public class com.alterna.sdk.** { *; }

# Keep SDK main class and its methods
-keep class com.alterna.sdk.SDK {
    public *;
}

# Keep all callback interfaces
-keep interface com.alterna.sdk.callbacks.** { *; }

# Keep all model classes
-keep class com.alterna.sdk.models.** { *; }

# Keep all UI activities
-keep class com.alterna.sdk.ui.** { *; }

# Keep WPS handler
-keep class com.alterna.sdk.wps.** { *; }

# Keep attributes for reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ProGuard rules for Vinkällaren

# Keep model classes for Room
-keep class se.ahlen.vinkallaren.data.model.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Hilt
-keep class dagger.hilt.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# Keep generic signatures
-keepattributes Signature

# Keep annotations
-keepattributes *Annotation*

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**

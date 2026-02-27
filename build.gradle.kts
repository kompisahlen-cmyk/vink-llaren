// Top-level build file
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    // NOTE: compose_version removed - using BOM (Bill of Materials) in app/build.gradle.kts instead
    extra["room_version"] = "2.5.2"
    extra["lifecycle_version"] = "2.7.0"
}

// Top-level build file
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    extra["compose_version"] = "1.6.1"
    extra["room_version"] = "2.5.2"
    extra["lifecycle_version"] = "2.7.0"
}

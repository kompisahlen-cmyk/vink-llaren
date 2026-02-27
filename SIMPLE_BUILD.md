# Snabbfix för byggfel

## Problem
- Kotlin-version för ny för din Android Studio
- Compose-kompilator inkompatibel
- Kodgenererad för nyare API

## Lösning

### 1. Öppna build.gradle.kts (projekt-nivå, inte app)
Ändra:
```kotlin
id("org.jetbrains.kotlin.android") version "1.9.22" apply false
```
Till:
```kotlin
id("org.jetbrains.kotlin.android") version "1.8.10" apply false
```

### 2. Öppna app/build.gradle.kts
Ändra:
```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.8"
}
```
Till:
```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.4.3"
}
```

### 3. Synca Gradle
- Klicka "Sync Now" när den frågar
- ELLER: File → Sync Project with Gradle Files

### 4. Bygg igen
- Build → Build APK

## Om det fortfarande strular
Försök:
1. Build → Clean Project
2. File → Invalidate Caches / Restart
3. Build → Rebuild Project

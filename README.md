# 🍷 Vinkällaren - Wine Scanner & Cellar Manager

En komplett Android-app för att skanna, katalogisera och analysera dina viner. Byggd för Samsung S24 Ultra.

## Funktioner

### 1. Wine Scanner 
- **Kamera-integration**: Skanna vinetiketter direkt med CameraX
- **OCR**: ML Kit Text Recognition för textextraktion
- **Automatisk igenkänning**: Namn, producent, årgång, typ, land, region
- **Konfidensbetyg**: Visar hur säker skanningen är

### 2. Vindatabas
- **SQLite + Room**: Lokal lagring, fullt offline
- **Fält**: Namn, producent, årgång, typ, land, region, pris, betyg
- **Förvaring**: Plats och kvantitetsspårning
- **Foton**: Etikett- och flaskbilder
- **Drickfönster**: Automatisk beräkning

### 3. Vinanalys
- **Drickfönster**: Beräknat per vintyp och druva
- **Matkombinationer**: Förslag baserat på vindruvor
- **Mognadsindikator**: Visuell status

### 4. Smarta Rekommendationer
- **Redo att dricka**: Lista viner i optimal fas
- **För tidiga**: Viner som behöver lagras
- **Övermogna**: Drick snart!

### 5. Datasynk
- **Firebase**: Molnlagring (valfritt)
- **Systembolaget**: Prisintegration (planerat)
- **Vivino**: Betyg (planerat)

## Teknik

- **Kotlin** + **Jetpack Compose**
- **Hilt** för dependency injection
- **Room** för databas
- **ML Kit** för OCR
- **CameraX** för kamera
- **Material Design 3**

## Bygg

```bash
# Bygg debug-APK
./gradlew assembleDebug

# Installera på enhet
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Struktur

```
app/src/main/java/se/ahlen/vinkallaren/
├── MainActivity.kt
├── analysis/       # Vinanalys och matkombinationer
├── data/           # Databas, modeller, repository
├── scanner/        # OCR-skanner
├── ui/             # Skärmar och ViewModels
└── di/             # Dependency injection
```

## Användning

1. **Skanna**: Tryck kamera-ikonen, håll över etikett
2. **Lägg till**: Fyll i eller ändra extraherad data
3. **Spara**: Vin sparas med automatiskt drickfönster
4. **Se": Vinlistan visar alla viner
5. **Redo**: Hemskärmen visar viner redo att dricka

## Status

✅ Fullt fungerande scanner med OCR
✅ Databas med Wine, TastingNote, StorageLocation  
✅ Drickfönster-kalkylator per vintyp
✅ Matkombinationsförslag
✅ Huvudskärmar: Hem, Vinlista, Scanner, Inställningar
✅ Lägg till vin (manuellt och via skanning)

## Licens

MIT License - Se LICENSE för detaljer.

---

Byggd med ❤️ för Petters vinsamling.

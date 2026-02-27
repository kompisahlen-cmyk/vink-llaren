# 🍷 Vinkällaren - Snabbstart

## Testa appen NU (2 minuter)

### Steg 1: Installera dependencies
```bash
cd ~/vinkallaren/Vinkallaren
npm install
```

### Steg 2: Starta utvecklingsserver
```bash
npx expo start
```

### Steg 3: Kör på telefonen
1. På Petter's Samsung S24 Ultra: Installera "Expo Go" från Google Play
2. Scanna QR-koden som visas i terminalen
3. Vinkällaren startar på telefonen!

---

## Vad du ser

**3 skärmar är fungerande:**

1. **📷 Skanner** - Tryck på kamera-ikonen längst upp höger
   - Fokusram för att linja in etiketten
   - Ta bild eller välj från galleri
   - Förhandsvisning före sparning

2. **📚 Katalog** - Startsidan
   - Alla viner i samlingen
   - Sök i realtid
   - Filter: rött, vitt, rosé, mousserande, dessert, starkvin
   - Stats: antal olika viner & flaskor

3. **📝 Lägg till vin** - Tryck + eller efter skanning
   - Tre flikar: Grundinfo, Detaljer, Förvaring
   - Stjärnbetyg 1-5
   - Automatiskt drickfönster
   - Placering (rack/hylla)

---

## Demo-data

Appen fylls automatiskt med:
- **3 flaskor** Barolo 2018 (Italiensk rödvin)
- **2 flaskor** Chablis 2020 (Franskt vitt vin)
- **6 flaskor** Champagne 2015 (party-tillgångar)

Tryck på vilket vin som helst för att redigera.

---

## Bygg APK (för permanent installation)

```bash
# Installera EAS CLI
npm install -g eas-cli

# Konfigurera bygge
eas build --configure
# Välj: Android → APK (preview)

# Bygg
eas build --platform android --profile preview

# Ladda ner APK och installera på telefonen
```

---

## Filstruktur

| Vad | Var |
|-----|-----|
| Skanner | `src/screens/ScannerScreen.tsx` |
| Katalog | `src/screens/CatalogScreen.tsx` |
| Lägg till vin | `src/screens/AddWineScreen.tsx` |
| Databas | `src/database/database.ts` |
| OCR-kod | `src/services/scanService.ts` |

---

## Status: ✅ PROTOTYP KLAR

**Kärnfunktioner på plats:**
- ✅ Kamera-skanner med UI
- ✅ SQLite-databas
- ✅ Sök & filter
- ✅ 3 fungerande skärmar
- ✅ Drickfönster-beräkning
- ✅ Demo-data

**Vad som saknas:**
- 🟡 Google ML Kit för riktig OCR (koden är förberedd)
- 🟡 Vivino/API-integration
- 🟡 Cloud sync

---

Klar för test! 🎉

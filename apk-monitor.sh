#!/bin/bash
# APK Monitor för Vinkällaren
# Kör via cron för att bevaka nya APK-byggen

APK_PATH="$HOME/vinkallaren/app/build/outputs/apk/debug/app-debug.apk"
DESKTOP_PATH="$HOME/Desktop/app-debug.apk"
LOCK_FILE="$HOME/.vinkallaren_apk_sent"
MIN_SIZE=$((10 * 1024 * 1024))  # 10MB i bytes

# Helper: Formatera bytes till läsbar storlek (macOS-kompatibel)
format_size() {
    local bytes=$1
    if [[ $bytes -gt 1073741824 ]]; then
        echo "$(echo "scale=2; $bytes/1073741824" | bc)GB"
    elif [[ $bytes -gt 1048576 ]]; then
        echo "$(echo "scale=1; $bytes/1048576" | bc)MB"
    elif [[ $bytes -gt 1024 ]]; then
        echo "$(echo "scale=0; $bytes/1024" | bc)KB"
    else
        echo "${bytes}B"
    fi
}

# Kolla om filen finns
if [[ ! -f "$APK_PATH" ]]; then
    echo "$(date): Ingen APK-fil hittad än"
    exit 0
fi

# Kolla filstorlek
FILE_SIZE=$(stat -f%z "$APK_PATH" 2>/dev/null || stat -c%s "$APK_PATH" 2>/dev/null)
if [[ "$FILE_SIZE" -lt "$MIN_SIZE" ]]; then
    echo "$(date): APK hittad men för liten (${FILE_SIZE} bytes)"
    exit 0
fi

# Kolla om vi redan skickat denna version (basera på checksum)
CURRENT_CHECKSUM=$(md5 -q "$APK_PATH" 2>/dev/null || md5sum "$APK_PATH" 2>/dev/null | cut -d' ' -f1)
if [[ -f "$LOCK_FILE" ]]; then
    SENT_CHECKSUM=$(cat "$LOCK_FILE" 2>/dev/null)
    if [[ "$CURRENT_CHECKSUM" == "$SENT_CHECKSUM" ]]; then
        echo "$(date): Denna version är redan skickad"
        exit 0
    fi
fi

SIZE_HUMAN=$(format_size $FILE_SIZE)

# Kopiera till Desktop (med felhantering)
if cp "$APK_PATH" "$DESKTOP_PATH" 2>/dev/null; then
    echo "$(date): ✅ Kopierade APK till Desktop ($SIZE_HUMAN)"
else
    echo "$(date): ⚠️ Kunde inte kopiera till Desktop (rättighetsproblem)"
    # Fortsätt ändå för att skicka mail
fi

# Skicka mail via gog
source "$HOME/.openclaw/workspace/.env.gog" 2>/dev/null
if command -v gog &> /dev/null; then
    gog gmail send \
        --to "petter.ahlen@aixia.se" \
        --subject "Vinkällaren APK" \
        --body "Ny APK-version är byggd och klar!

Fil: app-debug.apk
Storlek: $SIZE_HUMAN
Plats: $APK_PATH
Skickad till Desktop: $(test -f "$DESKTOP_PATH" && echo "Ja" || echo "Nej - rättighetsproblem")
Checksum: $CURRENT_CHECKSUM

Filen finns på ovanstående sökväg." \
        --no-input
    
    if [[ $? -eq 0 ]]; then
        echo "$CURRENT_CHECKSUM" > "$LOCK_FILE"
        echo "$(date): ✅ Mail skickat till petter.ahlen@aixia.se"
        echo "$(date): APK storlek: $SIZE_HUMAN"
    else
        echo "$(date): ❌ Mail kunde inte skickas"
        exit 1
    fi
else
    echo "$(date): gog inte tillgängligt"
    exit 1
fi

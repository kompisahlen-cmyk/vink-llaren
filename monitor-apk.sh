#!/bin/bash
# Vinkällaren APK Monitor - Kopia och mail vid ny byggnad

APK_SOURCE="$HOME/vinkallaren/app/build/outputs/apk/debug/app-debug.apk"
DEST_DIR="$HOME/Desktop"
DEST_FILE="$DEST_DIR/app-debug.apk"
MIN_SIZE=$((10 * 1024 * 1024))  # 10MB i bytes

# Loggfil
LOG_FILE="$HOME/vinkallaren/apk-monitor.log"

log_msg() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
}

log_msg "=== APK Monitor startad ==="

# Kontrollera att filen finns
if [ ! -f "$APK_SOURCE" ]; then
    log_msg "APK finns inte: $APK_SOURCE"
    echo "APK_EJ_FUNNEN"
    exit 0
fi

# Hämta filstorlek
FILE_SIZE=$(stat -f%z "$APK_SOURCE" 2>/dev/null || stat -c%s "$APK_SOURCE" 2>/dev/null)
log_msg "APK hittad. Storlek: $FILE_SIZE bytes ($(($FILE_SIZE / 1024 / 1024)) MB)"

# Kontrollera storlek (>10MB)
if [ "$FILE_SIZE" -le "$MIN_SIZE" ]; then
    log_msg "APK för liten ($FILE_SIZE bytes). Gräns: $MIN_SIZE bytes"
    echo "FOR_LITEN"
    exit 0
fi

# Kontrollera om vi redan kopierat denna version (jämför md5)
if [ -f "$DEST_FILE" ]; then
    SOURCE_MD5=$(md5 -q "$APK_SOURCE" 2>/dev/null || md5sum "$APK_SOURCE" | awk '{print $1}')
    DEST_MD5=$(md5 -q "$DEST_FILE" 2>/dev/null || md5sum "$DEST_FILE" | awk '{print $1}')
    
    if [ "$SOURCE_MD5" = "$DEST_MD5" ]; then
        log_msg "Samma APK-version finns redan på Desktop. Hoppar över."
        echo "REDAN_KOPIERAD"
        exit 0
    fi
fi

# Kopiera filen
cp "$APK_SOURCE" "$DEST_FILE"
if [ $? -ne 0 ]; then
    log_msg "Kopiering misslyckades!"
    echo "KOP_ERROR"
    exit 1
fi
log_msg "APK kopierad till Desktop: $DEST_FILE"

# Skicka mail via gog
source "$HOME/.openclaw/workspace/.env.gog"
if gog gmail send \
    --to "petter.ahlen@aixia.se" \
    --subject "Vinkällaren APK" \
    --body "Ny APK-fil är klar och bifogas.\n\nFil: app-debug.apk\nStorlek: $(($FILE_SIZE / 1024 / 1024)) MB\n\nGenererad: $(date)" \
    --attach "$DEST_FILE" \
    --no-input; then
    log_msg "Mail skickat till petter.ahlen@aixia.se"
    echo "MAIL_SENT"
else
    log_msg "Mail-konfig saknas eller fel vid sändning"
    echo "MAIL_OK (fil kopierad men mail kunde ej skickas)"
fi

log_msg "=== APK Monitor slutförd ==="

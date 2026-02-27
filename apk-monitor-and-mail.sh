#!/bin/bash
# APK Monitor with Email Notification
# Körs av cron för att bevaka APK-filen

APK_PATH="$HOME/vinkallaren/app/build/outputs/apk/debug/app-debug.apk"
DESKTOP_PATH="$HOME/Desktop/app-debug.apk"
STATE_FILE="$HOME/vinkallaren/.apk-monitor-state.json"
LOG_FILE="$HOME/vinkallaren/apk-monitor.log"
MIN_SIZE_BYTES=$((10 * 1024 * 1024))  # 10MB

# Log function
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Check if file exists and is large enough
check_apk() {
    if [ -f "$APK_PATH" ]; then
        local size=$(stat -f%z "$APK_PATH" 2>/dev/null || stat -c%s "$APK_PATH" 2>/dev/null)
        if [ "$size" -gt "$MIN_SIZE_BYTES" ]; then
            echo "valid"
        else
            echo "too_small:$size"
        fi
    else
        echo "missing"
    fi
}

# Send email with attachment using gog
send_email() {
    local apk_path="$1"
    log "Skickar APK via email till petter.ahlen@aixia.se"
    
    # Kopiera till Desktop först
    cp "$apk_path" "$DESKTOP_PATH"
    log "Kopierade APK till Desktop: $DESKTOP_PATH"
    
    # Skicka mail via gog CLI
    export GOG_USE_BROWSER_RELAY=true
    if gog gmail send --to "petter.ahlen@aixia.se" \
               --subject "Vinkällaren APK" \
               --body "Hej! Här är den senaste debug-versionen av Vinkällaren-appen. Byggd: $(date '+%Y-%m-%d %H:%M')

Filen finns även på Desktop." \
               --attach "$apk_path" \
               --no-input 2>/dev/null; then
        log "✅ Email skickat framgångsrikt!"
        return 0
    else
        log "⚠️ Kunde inte skicka via gog, försöker med mail-kommando..."
        # Fallback: använd mail-kommandot om tillgängligt
        if command -v mail >/dev/null 2>&1; then
            echo "Vinkällaren APK bifogad. Byggd: $(date)" | mail -s "Vinkällaren APK" -A "$apk_path" petter.ahlen@aixia.se
            log "Email skickat via mail-kommando"
        else
            log "❌ Kunde inte skicka email - inget mail-kommando tillgängligt"
            return 1
        fi
    fi
}

# Main logic
log "=== APK Monitor Check Started ==="
STATUS=$(check_apk)

if [ "$STATUS" = "valid" ]; then
    log "✅ APK hittad och är >10MB ($APK_PATH)"
    
    # Check if already notified
    if [ -f "$STATE_FILE" ]; then
        LAST_NOTIFIED=$(cat "$STATE_FILE" | grep -o '"lastNotifiedSize":[0-9]*' | cut -d: -f2)
        CURRENT_SIZE=$(stat -f%z "$APK_PATH" 2>/dev/null)
        
        if [ "$LAST_NOTIFIED" = "$CURRENT_SIZE" ]; then
            log "ℹ️ Redan notifierad om denna APK (samma storlek)"
            exit 0
        fi
    fi
    
    # Copy and email
    if send_email "$APK_PATH"; then
        # Update state
        CURRENT_SIZE=$(stat -f%z "$APK_PATH" 2>/dev/null)
        echo "{\"lastNotifiedSize\":$CURRENT_SIZE,\"lastNotifiedTime\":\"$(date -u +%Y-%m-%dT%H:%M:%SZ)\"}" > "$STATE_FILE"
        log "✅ APK levererad! Storlek: $((CURRENT_SIZE / 1024 / 1024))MB"
    fi
    
elif [[ "$STATUS" == too_small:* ]]; then
    SIZE=${STATUS#too_small:}
    log "⚠️ APK för liten: $((SIZE / 1024))KB (kräver >10MB)"
else
    log "⏳ APK saknas - väntar på bygge"
fi

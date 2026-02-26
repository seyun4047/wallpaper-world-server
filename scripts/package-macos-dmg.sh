#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

APP_NAME="${APP_NAME:-WallpaperWorldServer}"
APP_VERSION="${APP_VERSION:-1.0.0}"
VENDOR="${VENDOR:-mutzin}"
DEST_DIR="${DEST_DIR:-dist}"
ICON_PATH="${ICON_PATH:-}"
SIGNING_IDENTITY="${SIGNING_IDENTITY:-}"
KEYCHAIN_PATH="${KEYCHAIN_PATH:-}"

echo "[1/3] Build bootJar"
./gradlew clean bootJar

MAIN_JAR="$(ls -1 build/libs/*.jar | grep -v -- '-plain.jar' | head -n 1)"
if [[ -z "${MAIN_JAR:-}" ]]; then
  echo "No runnable jar found in build/libs"
  exit 1
fi

mkdir -p "$DEST_DIR"

JPACKAGE_ARGS=(
  --type dmg
  --name "$APP_NAME"
  --input build/libs
  --main-jar "$(basename "$MAIN_JAR")"
  --dest "$DEST_DIR"
  --app-version "$APP_VERSION"
  --vendor "$VENDOR"
  --java-options "-Dspring.profiles.active=prod"
)

if [[ -n "$ICON_PATH" && -f "$ICON_PATH" ]]; then
  JPACKAGE_ARGS+=(--icon "$ICON_PATH")
else
  if [[ -f "$ROOT_DIR/assets/macos/icon.icns" ]]; then
    JPACKAGE_ARGS+=(--icon "$ROOT_DIR/assets/macos/icon.icns")
  fi
fi

if [[ -z "$SIGNING_IDENTITY" ]]; then
  SIGNING_IDENTITY="$(security find-identity -v -p codesigning 2>/dev/null | awk -F\" '/Developer ID Application:/ {print $2; exit}')"
fi

if [[ -n "$SIGNING_IDENTITY" ]]; then
  echo "[2/3] Build signed dmg with identity: $SIGNING_IDENTITY"
  JPACKAGE_ARGS+=(--mac-sign --mac-signing-key-user-name "$SIGNING_IDENTITY")
  if [[ -n "$KEYCHAIN_PATH" && -f "$KEYCHAIN_PATH" ]]; then
    JPACKAGE_ARGS+=(--mac-signing-keychain "$KEYCHAIN_PATH")
  fi
else
  echo "[2/3] No code-sign identity found. Building unsigned dmg."
fi

echo "[3/3] Run jpackage"
jpackage "${JPACKAGE_ARGS[@]}"

echo "Done: $DEST_DIR"

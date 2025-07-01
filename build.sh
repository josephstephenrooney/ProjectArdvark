#!/bin/bash
set -e

if [ -z "$ANDROID_SDK_ROOT" ]; then
  echo "ANDROID_SDK_ROOT not set" >&2
  exit 1
fi

BUILD_TOOLS=$(ls "$ANDROID_SDK_ROOT/build-tools" | sort -V | tail -n 1)
AAPT2="$ANDROID_SDK_ROOT/build-tools/$BUILD_TOOLS/aapt2"
D8="$ANDROID_SDK_ROOT/build-tools/$BUILD_TOOLS/d8"
ZIPALIGN="$ANDROID_SDK_ROOT/build-tools/$BUILD_TOOLS/zipalign"
APKSIGNER="$ANDROID_SDK_ROOT/build-tools/$BUILD_TOOLS/apksigner"
ANDROID_JAR="$ANDROID_SDK_ROOT/platforms/android-30/android.jar"

OUT=build
RES_ZIP=$OUT/compiled_res.zip
APK_UNSIGNED=$OUT/unsigned.apk
APK_ALIGNED=$OUT/aligned.apk
APK=$OUT/app-debug.apk
JAVA_GEN=$OUT/generated
CLASSES_DIR=$OUT/classes

rm -rf "$OUT"
mkdir -p "$OUT" "$JAVA_GEN" "$CLASSES_DIR"

echo "Compiling resources..."
"$AAPT2" compile --dir src/main/res -o "$RES_ZIP"

echo "Linking resources..."
"$AAPT2" link -o "$APK_UNSIGNED" \
    -I "$ANDROID_JAR" \
    --manifest src/main/AndroidManifest.xml \
    --java "$JAVA_GEN" \
    "$RES_ZIP"

echo "Compiling Kotlin sources..."
kotlinc src/main/java "$JAVA_GEN" \
    -classpath "$ANDROID_JAR" \
    -d "$CLASSES_DIR"

find "$JAVA_GEN" -name '*.java' > "$OUT/sources.list"

if [ -s "$OUT/sources.list" ]; then
  echo "Compiling generated Java..."
  javac -classpath "$ANDROID_JAR":"$CLASSES_DIR" \
    -d "$CLASSES_DIR" @"$OUT/sources.list"
fi

echo "Converting classes to DEX..."
"$D8" "$CLASSES_DIR" --lib "$ANDROID_JAR" --output "$OUT"

echo "Packaging APK..."
"$AAPT2" add "$APK_UNSIGNED" "$OUT/classes.dex"

echo "Aligning APK..."
"$ZIPALIGN" -f 4 "$APK_UNSIGNED" "$APK_ALIGNED"

DEBUG_KEYSTORE="$HOME/.android/debug.keystore"

echo "Signing APK..."
"$APKSIGNER" sign --ks "$DEBUG_KEYSTORE" --ks-pass pass:android --out "$APK" "$APK_ALIGNED"

echo "APK generated at $APK"

# Screenshot App

This sample Kotlin application captures the device screen every 10 seconds using `MediaProjection` and stores timestamped PNGs under
`/storage/emulated/0/Android/data/com.example.myapp/files/screenshots`.

The repository demonstrates two build approaches:

1. **Option A** – a shell script (`build.sh`) that drives the Android SDK tools directly.
2. **Option B** – a Bazel build (`bazel build //:app`).

Both methods use the same Kotlin sources and resources.

## Folder Layout

```
project root
├── build.sh                # option A build script
├── WORKSPACE               # Bazel workspace
├── BUILD                   # Bazel build targets
├── settings.gradle         # Gradle settings (for reference)
├── build.gradle            # Gradle module (for reference)
└── src/
    └── main/
        ├── AndroidManifest.xml
        ├── java/com/example/myapp/
        │   ├── MainActivity.kt
        │   ├── ScreenshotService.kt
        │   └── StorageManager.kt
        └── res/
            ├── layout/activity_main.xml
            └── values/*.xml
```

## Prerequisites

- **Java 11+** installed and on your `PATH` (`java -version`).
- **Android SDK** with platform 30 and build-tools installed. Set `ANDROID_SDK_ROOT` to this directory.
- Tools from the SDK: `aapt2`, `d8`, `zipalign`, `apksigner`.
- **Bazel** (for option B) installed and available in `PATH`.
- USB debugging enabled on your Android device and `adb` available.

## Option A – Building with `build.sh`

1. Ensure `ANDROID_SDK_ROOT` points to your SDK:
   ```bash
   export ANDROID_SDK_ROOT=/path/to/android/sdk
   ```
2. Make the script executable and run it:
   ```bash
   chmod +x build.sh
   ./build.sh
   ```
   The script performs the following steps:
   1. `aapt2 compile` resources → `compiled_res.zip`
   2. `aapt2 link` with `android.jar` → generates `R.java` and `unsigned.apk`
   3. `kotlinc` and `javac` compile sources → `.class` files
   4. `d8` converts classes → `classes.dex`
   5. `aapt2 add` inserts the dex into the APK
   6. `zipalign` aligns the APK
   7. `apksigner` signs it using the debug keystore

   The final artifact is `build/app-debug.apk`.
3. Install on a connected device:
   ```bash
   adb install -r build/app-debug.apk
   ```

## Option B – Building with Bazel

1. Install Bazel from [bazel.build](https://bazel.build). Ensure it is on your `PATH`.
2. From the project root, build the APK:
   ```bash
   bazel build //:app
   ```
   Bazel outputs `bazel-bin/app.apk`.
3. Install the APK:
   ```bash
   adb install -r bazel-bin/app.apk
   ```

## Troubleshooting

- Verify your device is visible with `adb devices`.
- Ensure USB debugging is enabled on the device.
- Permission or path errors often mean `ANDROID_SDK_ROOT` or Java is not correctly set.
- If `apksigner` complains about missing keystore, ensure the debug keystore exists at `$HOME/.android/debug.keystore` (created automatically by Android Studio or the SDK tools).
- On Linux, you may need udev rules for USB debugging.

## Customization

- The application package is `com.example.myapp`. Change this in `AndroidManifest.xml`, Gradle/Bazel files, and source directories if needed.
- Versioning is defined in `build.gradle`. Update `versionCode` and `versionName` as desired.

## License

This project is provided as-is with no specific license. Use it freely in your experiments.

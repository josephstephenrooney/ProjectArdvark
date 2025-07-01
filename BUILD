load("@rules_kotlin//kotlin:kotlin.bzl", "kt_android_library")

kt_android_library(
    name = "app_lib",
    srcs = glob(["src/main/java/**/*.kt"]),
    manifest = "src/main/AndroidManifest.xml",
    resource_files = glob(["src/main/res/**"]),
    custom_package = "com.example.myapp",
)

android_binary(
    name = "app",
    manifest = "src/main/AndroidManifest.xml",
    deps = [":app_lib"],
    custom_package = "com.example.myapp",
    min_sdk_version = 21,
    target_sdk_version = 30,
)

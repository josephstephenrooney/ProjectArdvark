load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "rules_kotlin",
    url = "https://github.com/bazelbuild/rules_kotlin/releases/download/v1.8.2/rules_kotlin_release.tgz",
    sha256 = "0e582c108b73a3ef3c5c1397cbe5c7d07212e08a3b3fcdeec95bf2c98b5eb2d8",
)

load("@rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories", "kt_register_toolchains")

kotlin_repositories()
kt_register_toolchains()

android_sdk_repository(
    name = "androidsdk",
)

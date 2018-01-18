load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library", "scala_test")
load("@io_bazel_rules_scala//scala_proto:scala_proto.bzl", "scalapb_proto_library")

genrule(
    name = "copy_link_jni_md_header",
    srcs = ["@local_jdk//:jni_md_header-linux"],
    outs = ["cpp/src/jni_md.h"],
    cmd = "cp -f $< $@",
)

genrule(
    name = "copy_link_jni_header",
    srcs = ["@local_jdk//:jni_header"],
    outs = ["cpp/src/jni.h"],
    cmd = "cp -f $< $@",
)

proto_library(
    name = "jni_proto",
    srcs = [
        "src/main/protobuf/Av.proto",
        "src/main/protobuf/Core.proto",
        "src/main/protobuf/ProtoLog.proto",
    ],
)

cc_proto_library(
    name = "jni_cc_proto",
    deps = [":jni_proto"],
)

cc_library(
    name = "jni_lib",
    srcs = glob([
        "cpp/src/**/*.cpp",
        "cpp/src/**/*.h",
    ]) + [
        ":cpp/src/jni.h",
        ":cpp/src/jni_md.h",
    ],
    copts = [
        "-Wno-error=switch",
        "-Wno-parentheses",
        "-std=c++14",
        "-DHAVE_MAKE_UNIQUE",
        "-DHAVE_TO_STRING",
    ],
    includes = [
        "cpp/src",
        "src/main/protobuf",
    ],
    deps = [
        ":jni_cc_proto",
        "//c-toxcore:headers",
        "//c-toxcore/toxav",
        "//c-toxcore/toxcore",
        "//c-toxcore/toxencryptsave",
    ],
)

cc_binary(
    name = "libtox4j-c.so",
    linkopts = ["-Wl,--version-script,$(location cpp/src/libtox4j-c.ld)"],
    linkshared = True,
    visibility = ["//visibility:public"],
    deps = [
        "cpp/src/libtox4j-c.ld",
        ":jni_lib",
    ],
)

scalapb_proto_library(
    name = "jni_scala_proto",
    with_flat_package = True,
    deps = [":jni_proto"],
)

scala_library(
    name = "jvm-toxcore-c",
    srcs = glob([
        "src/main/java/**/*.java",
        "src/main/java/**/*.scala",
    ]),
    visibility = ["//visibility:public"],
    deps = [
        ":jni_scala_proto",
        "//jvm-macros",
        "//jvm-toxcore-api",
        "@com_google_guava_guava//jar",
        "@com_intellij_annotations//jar",
        "@com_typesafe_scala_logging_scala_logging//jar:file",
        "@org_slf4j_slf4j_api//jar",
    ],
)

scala_test(
    name = "jvm-toxcore-c-test",
    srcs = glob([
        "src/test/java/**/*.scala",
    ]),
    data = [":libtox4j-c.so"],
    jvm_flags = ["-Djava.library.path=jvm-toxcore-c"],
    resources = glob([
        "src/test/resources/**/*",
    ]),
    deps = [
        ":jni_scala_proto",
        ":jvm-toxcore-c",
        "//jvm-macros",
        "//jvm-toxcore-api",
        "@com_chuusai_shapeless//jar:file",
        "@com_google_guava_guava//jar",
        "@com_intellij_annotations//jar",
        "@com_typesafe_scala_logging_scala_logging//jar:file",
        "@log4j_log4j//jar",
        "@org_apache_commons_commons_lang3//jar",
        "@org_scalacheck_scalacheck//jar",
        "@org_scalactic_scalactic//jar:file",
        "@org_scalatest_scalatest//jar:file",
        "@org_slf4j_slf4j_api//jar",
        "@org_slf4j_slf4j_log4j12//jar",
    ],
)

[java_test(
    name = src[src.rindex("/") + 1:-5],
    srcs = [src],
    data = [":libtox4j-c.so"],
    jvm_flags = ["-Djava.library.path=`dirname $(location :libtox4j-c.so)`"],
    resources = glob([
        "src/test/resources/**/*",
    ]),
    deps = [
        ":jvm-toxcore-c",
        "//jvm-toxcore-api",
        "@junit_junit//jar",
        "@log4j_log4j//jar",
        "@org_scalactic_scalactic//jar",
        "@org_scalatest_scalatest//jar",
        "@org_slf4j_slf4j_api//jar",
        "@org_slf4j_slf4j_log4j12//jar",
    ],
) for src in glob(["src/test/java/**/*.java"])]

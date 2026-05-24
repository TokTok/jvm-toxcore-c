"""Macro for defining tox4j Kotlin tests.

Each test class gets its own `kt_jvm_test` target so Bazel can run them in
parallel and report failures per-class. The macro pins the shared
configuration (JNI library path, kotlin-test dependency, test_class
derivation) so the BUILD file stays focused on the test inventory.
"""

load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_test")

def tox4j_test(name, package, srcs = None, deps = [], tags = [], size = None):
    """Define a single tox4j JUnit test class.

    Args:
      name: short test class name (e.g. "ToxCoreConstantsTest"). Used both
        as the Bazel target name and as the class name within `package`.
      package: Kotlin package the test class lives in (e.g.
        "im.tox.tox4j.core"). Combined with `name` for the JUnit
        `test_class` and used to locate the source file when `srcs` is
        omitted.
      srcs: optional explicit source list. Defaults to a single file in
        the conventional location.
      deps: extra dependencies on top of the standard test set.
      tags: extra tags on top of "java" (e.g. "e2e" for slow tests).
      size: Bazel test size override ("small", "medium", "large", "enormous").
    """
    src_path = "lib/src/test/java/" + package.replace(".", "/") + "/" + name + ".kt"
    kwargs = {}
    if size != None:
        kwargs["size"] = size
    kt_jvm_test(
        name = name,
        srcs = srcs or [src_path],
        jvm_flags = ["-Djava.library.path=jvm-toxcore-c"],
        tags = ["java"] + tags,
        test_class = package + "." + name,
        deps = [
            "//jvm-toxcore-c:jvm-toxcore-c",
            "@maven//:org_jetbrains_kotlin_kotlin_test_junit",
        ] + deps,
        **kwargs
    )

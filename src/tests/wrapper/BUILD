load("//:arm_rules.bzl", "arm_source", "arm_clib", "arm_elf")

package(default_visibility = ["//visibility:public"])

arm_source(
    name = "start",
    src = "start.s",
)

arm_source(
    name = "reset",
    src = "reset.s",
)

arm_clib(
    name = "print",
    src = "print.c",
)

arm_source(
    name = "main",
    src = "main.s",
)

filegroup(
    name = "layout",
    srcs = [
        "test.ld",
    ],
)

arm_elf( 
    name = "test",
    layout = "test.ld",
    deps = [
    ":start",
    ":reset",
    ":print",
    ":main"
   ],
)

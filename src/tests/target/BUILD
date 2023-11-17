load("//:arm_rules.bzl", "arm_source", "arm_clib", "arm_elf")

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

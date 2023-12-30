Run individual tests with, for example,
>  bazel test functions:call_void_return_type

Multiple tests cannot be run simultaneously. (This is a bug caused by them sharing the same file name for some intermediate files.)

For debugging,

qemu-system-aarch64 -M virt -cpu cortex-a57 -nographic -s -S -kernel bazel-bin/<path>/<target>.elf

In a separate terminal:
cd src/tests/bazel-bin/<path>
aarch64-none-elf-gdb
target remote localhost:1234
file <target>.elf


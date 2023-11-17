ROOT=/Users/hoefel/dev/parser2/src/tests
DEBUG=""

while test $# != 0
do
    case "$1" in
    -d) DEBUG=" -s -S " ;;
    *)  break ;;
    esac
    shift
done

qemu-system-aarch64 -M virt -cpu cortex-a57 -nographic -kernel $ROOT/bazel-bin/target/test.elf --no-reboot $DEBUG


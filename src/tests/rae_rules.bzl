as_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-as'
gcc_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-gcc'
ld_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-ld'
objcopy_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-objcopy'
rae_jar='/Users/hoefel/dev/parser2/target/parser-1.0-SNAPSHOT.jar'

def _rae_source_impl(ctx):
    asm_file, obj_file, elf_file, bin_file = _rae_source_impl_common(ctx)
    return [DefaultInfo(files = depset([asm_file, obj_file, elf_file, bin_file]))]

def _rae_source_impl_common(ctx):
    in_file = ctx.file.src
    asm_file = ctx.actions.declare_file(ctx.label.name + ".s") 
    obj_file = ctx.actions.declare_file(ctx.label.name + ".o") 
    elf_file = ctx.actions.declare_file(ctx.label.name + ".elf") 
    bin_file = ctx.actions.declare_file(ctx.label.name + ".bin") 
    static_deps = [ctx.file._start_file, ctx.file._reset_file, ctx.file._print_file]
    static_deps_copies = [ctx.actions.declare_file(f.basename) for f in static_deps]
    obj_deps = " ".join([d.path for d in static_deps_copies])
    obj_copy = ctx.actions.declare_file("main.o")

    layout_copy = ctx.actions.declare_file(ctx.file._layout_file.basename) 
    compile_cmd = ("filepath=%s && " % in_file.path) + "abspath=$(realpath $filepath) && " + "rootpath=${abspath%$filepath} && " + ("java -jar %s --root=$rootpath --input=$abspath --output=%s" % (rae_jar, asm_file.path))
    print(compile_cmd)
    ctx.actions.run_shell(
        inputs = [in_file],
	outputs = [asm_file],
        command = compile_cmd,
    )
    ctx.actions.run_shell(
        inputs = [asm_file],
	outputs = [obj_file],
        command = "%s -mcpu=cortex-a57 -g %s -o %s" %
                  (as_cmd, asm_file.path, obj_file.path),
    )
    ctx.actions.run_shell(
	inputs = [obj_file],
	outputs = [obj_copy],
	command = "cp %s %s" % (obj_file.path, obj_copy.path)
    )
    ctx.actions.run_shell(
        inputs = [ctx.file._layout_file],
	outputs = [layout_copy],
	command = "cp %s %s" % (ctx.file._layout_file.path, layout_copy.path)
    )
    ctx.actions.run_shell(
        inputs = static_deps,
	outputs = static_deps_copies,
	command = " && ".join(["cp %s %s" % (src.path, tgt.path) for (src, tgt) in zip(static_deps, static_deps_copies)])
    )
    ctx.actions.run_shell(
        inputs = static_deps_copies + [layout_copy, obj_copy],
	outputs = [elf_file],
        command = "cd %s && %s -g -T %s %s -o %s" %
                  (layout_copy.dirname, ld_cmd, layout_copy.basename, obj_copy.basename, elf_file.basename),
    )
    ctx.actions.run_shell(
        inputs = [elf_file],
	outputs = [bin_file],
        command = "%s -O binary %s %s" % (objcopy_cmd, elf_file.path, bin_file.path),
    )
    return asm_file, obj_file, elf_file, bin_file

rae_source = rule(
    implementation = _rae_source_impl,
    attrs = {
        "src": attr.label(allow_single_file = [".ro"]),
        "_start_file": attr.label(default = Label("//wrapper:start"), allow_single_file=True),
        "_reset_file": attr.label(default = Label("//wrapper:reset"), allow_single_file=True),
        "_print_file": attr.label(default = Label("//wrapper:print"), allow_single_file=True),
        "_layout_file": attr.label(default = Label("//wrapper:layout"), allow_single_file=True)
    },
)

#(echo -n; sleep 1; echo '\001x') | qemu-system-aarch64 -M virt -cpu cortex-a57 -nographic -kernel bazel-bin/functions/call_return_int.elf > /tmp/foo

def _rae_test_impl(ctx):
    asm_file, obj_file, elf_file, bin_file = _rae_source_impl_common(ctx)

    runfiles_path = "$0.runfiles/"
    data_file_root = runfiles_path + ctx.workspace_name + "/"
    elf_test_file = runfiles_path + elf_file.short_path
    qemu_clean_cmd = "sed -e ':a' -e 'N' -e '$!ba' -e 's/QEMU: Terminated\\n\\r//g'"

    tmp_file = '$TEST_UNDECLARED_OUTPUTS_DIR/' + ctx.label.name + ".out"
    qemu_exit = "(echo ''; sleep 1; echo '\001x')"
    qemu_cmd = "qemu-system-aarch64 -M virt -cpu cortex-a57 -nographic -kernel %s " % elf_file.short_path
    script = "\n".join(
      ["err=1"] +
      ["%s | %s | %s > %s" % (qemu_exit, qemu_cmd, qemu_clean_cmd, tmp_file)] +
      ["echo \"Expected <, Actual >\""] +
      ["diff %s %s" % (ctx.files.expected[0].path, tmp_file)]
    )

    # Write the file, it is executed by 'bazel test'.
    ctx.actions.write(
        output = ctx.outputs.executable,
        content = script,
    )

    # To ensure the files needed by the script are available, we put them in
    # the runfiles.
    runfiles = [elf_file]
    if ctx.file.expected != None:
        runfiles.append(ctx.file.expected)
    if ctx.file.error != None:
        runfiles.append(ctx.file.error)
    return [DefaultInfo(runfiles = ctx.runfiles(files = runfiles))]

rae_test = rule(
    implementation = _rae_test_impl,
    attrs = {
        "src": attr.label(allow_single_file = [".ro"]),
        "expected": attr.label(allow_single_file = [".out"]),
        "error": attr.label(allow_single_file = [".err"]),
        "_start_file": attr.label(default = Label("//wrapper:start"), allow_single_file=True),
        "_reset_file": attr.label(default = Label("//wrapper:reset"), allow_single_file=True),
        "_print_file": attr.label(default = Label("//wrapper:print"), allow_single_file=True),
        "_layout_file": attr.label(default = Label("//wrapper:layout"), allow_single_file=True)
    },
    test = True,

)

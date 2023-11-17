as_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-as'
gcc_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-gcc'
ld_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-ld'
objcopy_cmd='/Applications/ArmGNUToolchain/12.3.rel1/aarch64-none-elf/bin/aarch64-none-elf-objcopy'

def _arm_source_impl(ctx):
    in_file = ctx.file.src
    out_file = ctx.actions.declare_file(ctx.label.name + ".o") 
    ctx.actions.run_shell(
        inputs = [in_file],
	outputs = [out_file],
        command = "%s -mcpu=cortex-a57 -g %s -o %s" %
                  (as_cmd, in_file.path, out_file.path),
    )
    return [DefaultInfo(files = depset([out_file]))]

arm_source = rule(
    implementation = _arm_source_impl,
    attrs = {
        "src": attr.label(allow_single_file = [".s"])
    },
)

def _arm_clib_impl(ctx):
    in_file = ctx.file.src
    out_file = ctx.actions.declare_file(ctx.label.name + ".o") 
    ctx.actions.run_shell(
        inputs = [in_file],
	outputs = [out_file],
        command = "%s -c -mcpu=cortex-a57 -g %s -o %s" %
                  (gcc_cmd, in_file.path, out_file.path),
    )
    return [DefaultInfo(files = depset([out_file]))]

arm_clib = rule(
    implementation = _arm_clib_impl,
    attrs = {
        "src": attr.label(allow_single_file = [".c"])
    }
)

def _arm_elf_impl(ctx):
    layout_copy = ctx.actions.declare_file(ctx.file.layout.basename) 
    in_files = ctx.files.deps + [ctx.file.layout]
    elf_file = ctx.actions.declare_file(ctx.label.name + ".elf") 
    bin_file = ctx.actions.declare_file(ctx.label.name + ".bin") 
    ctx.actions.run_shell(
        inputs = in_files,
	outputs = [elf_file, layout_copy],
        command = "cp %s %s && cd %s && %s -g -T %s %s -o %s" %
                  (ctx.file.layout.path, layout_copy.path, layout_copy.dirname, ld_cmd, layout_copy.basename, " ".join([d.basename for d in ctx.files.deps]), elf_file.basename),
    )
    ctx.actions.run_shell(
        inputs = [elf_file],
	outputs = [bin_file],
        command = "%s -O binary %s %s" % (objcopy_cmd, elf_file.path, bin_file.path),
    )
    return [DefaultInfo(files = depset([elf_file, bin_file]))]

arm_elf = rule(
    implementation = _arm_elf_impl,
    attrs = {
        "layout": attr.label(allow_single_file = [".ld"]),
        "deps": attr.label_list(allow_rules = ["arm_source", "arm_clib"]),
    }
)


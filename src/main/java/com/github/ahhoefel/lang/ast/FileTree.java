package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.rules.LanguageRules;
import com.github.ahhoefel.parser.ErrorLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class FileTree {

    public String base;
    public Map<String, File> files;

    public FileTree(String base) {
        this.base = base;
        files = new HashMap<>();
    }

    public static Result fromTarget(Target target) throws IOException {
        System.out.println("Reading tree from target: " + target);
        ErrorLog log = new ErrorLog();
        String base = target.getBase();
        FileTree tree = new FileTree(base);
        LanguageRules lang = new LanguageRules();
        Stack<String> paths = new Stack<>();
        List<Target> targets = new ArrayList<>();
        paths.push(target.getSuffix());
        while (!paths.isEmpty()) {
            String p = paths.pop();
            System.out.println("Reading path: " + p);
            Target t = new Target(target.getSource(), base, p);
            File file = lang.parse(t, log);
            file.setTarget(t);
            tree.files.put(p, file);
            for (Import im : file.getImports().getImports()) {
                String next = im.getPath();
                if (!tree.files.containsKey(next)) {
                    paths.add(next);
                    tree.files.put(next, null);
                }
            }
        }
        System.out.print(tree);
        System.out.print(tree.files.get("return_statement"));
        if (log.isEmpty()) {
            return new Result(targets, tree);
        }
        return new Result(targets, log);
    }

    public String toString() {
        String out = "FileTree:\n";
        out += "\tBase: " + base + "\n";
        for (Map.Entry<String, File> file : files.entrySet()) {
            out += "\tFile: " + file.getKey() + "\n";
        }
        return out;
    }

    public class TargetMap {
        public File get(String p) {
            return files.get(p);
        }
    }

    public static class Result {
        private FileTree tree;
        private ErrorLog log;
        private List<Target> targets;

        public Result(List<Target> targets, FileTree tree) {
            this.tree = tree;
            this.targets = targets;
        }

        public Result(List<Target> targets, ErrorLog log) {
            this.log = log;
            this.targets = targets;
        }

        public boolean pass() {
            return tree != null;
        }

        public ErrorLog getLog() {
            return log;
        }

        public FileTree getTree() {
            return tree;
        }

        public List<Target> getTargets() {
            return targets;
        }
    }
}

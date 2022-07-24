package com.github.ahhoefel.lang.ast;

import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.parser.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Calling conventions.
 * <p>
 * FunctionInvocation: - store all local variables to the stack. - put return
 * pointer on the stack - put all parameters on the stack.
 * <p>
 * FunctionDeclaration: - pop all parameters into registers - execute body
 * Return statement: - pop return pointer - push return values
 * <p>
 * FunctionInvocation at return pointer: - pop return values into registers
 */
public class FunctionDeclaration implements Declaration {

    private String name;
    private List<VariableDeclaration> parameters;
    private Block statements;
    private Optional<Type> returnType;

    public FunctionDeclaration(Token name, List<VariableDeclaration> parameters, Optional<Type> returnType,
            Block statements) {
        this.name = name.getValue();
        this.parameters = parameters;
        this.statements = statements;
        this.returnType = returnType;
    }

    public void accept(Visitor v, Object... objs) {
        v.visit(this, objs);
    }

    public String getName() {
        return name;
    }

    public Block getBlock() {
        return statements;
    }

    public List<VariableDeclaration> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        if (!returnType.isPresent()) {
            return Type.VOID;
        }
        return returnType.get();
    }

    public List<Type> getParameterTypes() {
        List<Type> types = new ArrayList<>();
        for (VariableDeclaration param : parameters) {
            types.add(param.getType());
        }
        return types;
    }

    public String getParameterName(int i) {
        return parameters.get(i).getName();
    }

    @Override
    public File addToFile(File file) {
        file.addFunction(this);
        return file;
    }

}

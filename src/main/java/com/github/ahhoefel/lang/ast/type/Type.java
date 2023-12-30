package com.github.ahhoefel.lang.ast.type;

import java.math.BigInteger;

import com.github.ahhoefel.arm.AssemblyFile;
import com.github.ahhoefel.arm.Comment;
import com.github.ahhoefel.arm.InstructionType;
import com.github.ahhoefel.arm.Label;
import com.github.ahhoefel.arm.Register;
import com.github.ahhoefel.arm.RegisterShift;
import com.github.ahhoefel.arm.UInt12;
import com.github.ahhoefel.arm.UInt15MultipleOf8;
import com.github.ahhoefel.arm.UInt64;
import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.Target;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.expression.IndexAccessExpression;
import com.github.ahhoefel.lang.ast.expression.NewExpression;
import com.github.ahhoefel.lang.ast.expression.VariableExpression;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols.FunctionDefinition;
import com.github.ahhoefel.lang.ast.visitor.FormatVisitor;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope;

public abstract class Type extends Expression {

    // Singletons should not be used to explicitly written types in code, but rather
    // for the implicit types of expressions.
    // For explicitly written types in code, a new instance should be created so it
    // can have a code location.
    public static final Type INT = new IntType(null);
    public static final Type BOOL = new BooleanType(null);
    public static final Type STRING = new StringType(null);
    public static final Type VOID = new VoidType(null);
    // The type of types.
    public static final Type TYPE = new TypeType(null);

    public static final VariableDeclaration LENGTH_MEMBER = new VariableDeclaration("length", Type.INT,
            new CodeLocation(new Target(null, "//lang:Array"), 0, 0, 0));

    public abstract int getWidthBits();

    public abstract int getEncoding();

    public static int getWidthBits(Expression type) {
        if (type instanceof Type) {
            return ((Type) type).getWidthBits();
        }
        // TODO: allow width calculations for type expressions.
        return 64;
    }

    public static void getWidthBits(Expression type, AssemblyFile asm) {
        if (type instanceof NewExpression) {
            getWidthBitsNewExpression((NewExpression) type, asm);
            return;
        }
        if (type instanceof IndexAccessExpression) {
            getWidthBitsIndexAccessExpression((IndexAccessExpression) type, asm);
            return;
        }
        if (type instanceof VariableExpression) {
            getWidthBitsVariableExpression((VariableExpression) type, asm);
        }
        throw new RuntimeException("Expected new expressions for new array and array access only: " + type.getClass());
    }

    public static void getWidthBitsIndexAccessExpression(IndexAccessExpression expr, AssemblyFile asm) {
        int elementWidthBits = getWidthBits(expr.getIndex());
        asm.add(InstructionType.MOV.of(
                Register.X1,
                new UInt64(BigInteger.valueOf(elementWidthBits / 8)), new Comment("Array element width bytes")));

        // FormatVisitor fmt = new FormatVisitor();
        // fmt.visit(expr);
        // throw new RuntimeException(fmt.toString().trim());

        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getWidthRegisterTracker().getStackPositionBytes()))));
    }

    public static void getWidthBitsNewExpression(NewExpression expr, AssemblyFile asm) {
        if (!(expr.getType() instanceof IndexAccessExpression)) {
            throw new RuntimeException("Expected new expressions for arrays only");
        }
        IndexAccessExpression arrayType = (IndexAccessExpression) expr.getType();
        if (!(arrayType.getSubject() instanceof VariableExpression)) {
            throw new RuntimeException("Expected new expressions for arrays only");
        }
        if (!((VariableExpression) arrayType.getSubject()).getIdentifier().equals("Array")) {
            throw new RuntimeException("Expected new expressions for arrays only");
        }

        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getArgs().get(0).getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getArrayLengthRegisterTracker().getStackPositionBytes()))));

        int elementWidthBits = getWidthBits(arrayType.getIndex());
        asm.add(InstructionType.MOV.of(
                Register.X1,
                new UInt64(BigInteger.valueOf(elementWidthBits / 8)), new Comment("Array element width bytes")));
        // Store item width in bytes
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getArrayItemWidthRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.MUL_IMM.of(Register.X0, Register.X0, Register.X1));

        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getWidthRegisterTracker().getStackPositionBytes()))));
    }

    public static void getWidthBitsVariableExpression(VariableExpression expr, AssemblyFile asm) {

    }

    public static Expression getMemberType(Expression type, String memberName) {
        // TODO: lookup members on a type expression.
        return INT;
    }

    public static boolean hasMemberVariable(Expression type, String memberName) {
        System.out.println("Expression: " + type + ", " + memberName + "ArrayType: " + (type.getClass().toString()));
        if (type instanceof IndexAccessExpression &&
                ((IndexAccessExpression) type).getSubject() instanceof VariableExpression &&
                ((VariableExpression) ((IndexAccessExpression) type).getSubject()).getIdentifier().equals("Array") &&
                memberName.equals("length")) {
            return true;
        }
        return false;
    }

    public static VariableDeclaration getMemberVariable(Expression type, String memberName) {
        if (type instanceof IndexAccessExpression &&
                ((IndexAccessExpression) type).getSubject() instanceof VariableExpression &&
                ((VariableExpression) ((IndexAccessExpression) type).getSubject()).getIdentifier().equals("Array") &&
                memberName.equals("length")) {
            return LENGTH_MEMBER;
        }
        return null;
    }

    public static void constructorCall(NewExpression expr, AssemblyFile asm, FunctionDefinition defn) {
        Type.getWidthBits(expr, asm);
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getWidthRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.LDR.of(Register.X0, new Label("heap_bottom")));
        asm.add(InstructionType.ADD.of(Register.X1, Register.X0, Register.X1));

        // Store Address
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));

        // Store used heap size
        asm.add(InstructionType.LDR.of(Register.X1, new Label("=heap_bottom")));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.X1,
                new UInt15MultipleOf8(0))));
    }

    public static class IntType extends Type {

        public IntType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "int";
        }

        public boolean equals(Object o) {
            return o instanceof IntType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 1;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class BooleanType extends Type {

        public BooleanType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "bool";
        }

        public boolean equals(Object o) {
            return o instanceof BooleanType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 2;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class StringType extends Type {

        public StringType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "string";
        }

        public boolean equals(Object o) {
            return o instanceof StringType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 3;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class VoidType extends Type {

        public VoidType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "void";
        }

        public boolean equals(Object o) {
            return o instanceof VoidType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 0;
        }

        public int getEncoding() {
            return 4;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }

    public static class TypeType extends Type {

        public TypeType(CodeLocation location) {
            this.setLocation(location);
        }

        public String toString() {
            return "type";
        }

        public boolean equals(Object o) {
            return o instanceof TypeType;
        }

        @Override
        public void accept(Visitor v, Object... objs) {
            v.visit(this, objs);
        }

        public int getWidthBits() {
            return 64;
        }

        public int getEncoding() {
            return 5;
        }

        @Override
        public boolean isLValue() {
            return false;
        }

        @Override
        public Expression getType() {
            return Type.TYPE;
        }
    }
}

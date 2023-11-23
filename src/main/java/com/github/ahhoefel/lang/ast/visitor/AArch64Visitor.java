package com.github.ahhoefel.lang.ast.visitor;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import com.github.ahhoefel.arm.AssemblyFile;
import com.github.ahhoefel.arm.Condition;
import com.github.ahhoefel.arm.InstructionType;
import com.github.ahhoefel.arm.Label;
import com.github.ahhoefel.arm.Register;
import com.github.ahhoefel.arm.RegisterShift;
import com.github.ahhoefel.arm.UInt12;
import com.github.ahhoefel.arm.UInt15MultipleOf8;
import com.github.ahhoefel.arm.UInt64;
import com.github.ahhoefel.arm.Condition.Code;
import com.github.ahhoefel.lang.ast.Block;
import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.Import;
import com.github.ahhoefel.lang.ast.ImportCatalog;
import com.github.ahhoefel.lang.ast.LValue;
import com.github.ahhoefel.lang.ast.TypeDeclaration;
import com.github.ahhoefel.lang.ast.VariableDeclaration;
import com.github.ahhoefel.lang.ast.Visitable;
import com.github.ahhoefel.lang.ast.Visitor;
import com.github.ahhoefel.lang.ast.expression.AndExpression;
import com.github.ahhoefel.lang.ast.expression.BooleanLiteralExpression;
import com.github.ahhoefel.lang.ast.expression.EqualExpression;
import com.github.ahhoefel.lang.ast.expression.Expression;
import com.github.ahhoefel.lang.ast.expression.FunctionInvocationExpression;
import com.github.ahhoefel.lang.ast.expression.IntegerLiteralExpression;
import com.github.ahhoefel.lang.ast.expression.LessThanExpression;
import com.github.ahhoefel.lang.ast.expression.LessThanOrEqualExpression;
import com.github.ahhoefel.lang.ast.expression.MemberAccessExpression;
import com.github.ahhoefel.lang.ast.expression.NotEqualExpression;
import com.github.ahhoefel.lang.ast.expression.NotExpression;
import com.github.ahhoefel.lang.ast.expression.OrExpression;
import com.github.ahhoefel.lang.ast.expression.ParenthesesExpression;
import com.github.ahhoefel.lang.ast.expression.ProductExpression;
import com.github.ahhoefel.lang.ast.expression.StructLiteralExpression;
import com.github.ahhoefel.lang.ast.expression.SubtractExpression;
import com.github.ahhoefel.lang.ast.expression.SumExpression;
import com.github.ahhoefel.lang.ast.expression.UnaryMinusExpression;
import com.github.ahhoefel.lang.ast.expression.VariableExpression;
import com.github.ahhoefel.lang.ast.statements.AssignmentStatement;
import com.github.ahhoefel.lang.ast.statements.ExpressionStatement;
import com.github.ahhoefel.lang.ast.statements.ForStatement;
import com.github.ahhoefel.lang.ast.statements.IfStatement;
import com.github.ahhoefel.lang.ast.statements.ReturnStatement;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols.FunctionDefinition;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope.RegisterTracker;
import com.github.ahhoefel.lang.ast.symbols.SymbolReference.Resolution;
import com.github.ahhoefel.lang.ast.type.NamedType;
import com.github.ahhoefel.lang.ast.type.StructType;
import com.github.ahhoefel.lang.ast.type.Type.BooleanType;
import com.github.ahhoefel.lang.ast.type.Type.IntType;
import com.github.ahhoefel.lang.ast.type.Type.StringType;
import com.github.ahhoefel.lang.ast.type.Type.VoidType;
import com.github.ahhoefel.lang.ast.type.UnionType;

public class AArch64Visitor implements Visitor {

    public AssemblyFile createAssemblyFile(GlobalSymbols globals) {
        AssemblyFile asm = new AssemblyFile();
        for (FileSymbols f : globals.getFiles()) {
            for (FunctionDefinition fn : f.getFunctions()) {
                asm.add(InstructionType.GLOBAL.of(new Label(fn.getDeclaration().getName())));
            }
        }
        for (FileSymbols f : globals.getFiles()) {
            for (FunctionDefinition fn : f.getFunctions()) {
                fn.getDeclaration().accept(this, asm, fn);
            }
        }
        return asm;
    }

    @Override
    public void visit(AndExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getLeft().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRight().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.ADD.of(Register.X0, Register.X0, Register.X1));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(BooleanLiteralExpression expr, Object... objs) {
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.MOV.of(
                Register.X0,
                new UInt64(BigInteger.valueOf(expr.getValue() ? 1 : 0))));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(
                Register.X0,
                new RegisterShift<>(Register.SP,
                        new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(EqualExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getLeft().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRight().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.CMP.of(Register.X0, Register.X1));
        asm.add(InstructionType.CSET.of(Register.X0, new Condition(Code.EQ)));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(FunctionInvocationExpression expr, Object... objs) {
        AssemblyFile asm = (AssemblyFile) objs[0];
        FunctionDefinition defn = (FunctionDefinition) objs[1];
        for (Expression arg : expr.getArgs()) {
            arg.accept(this, objs);
        }

        Optional<Resolution> res = expr.getSymbolReference().getResolution();
        if (!res.isPresent()) {
            throw new RuntimeException("Unresolved function invocation:" + defn.getDeclaration().getName());
        }
        Optional<FunctionDefinition> fnDefnOpt = res.get().getFunctionDefinition();
        if (!fnDefnOpt.isPresent()) {
            throw new RuntimeException(
                    "Function invocations should resolve to functions:" + defn.getDeclaration().getName());
        }
        FunctionDefinition fnDefn = fnDefnOpt.get();
        List<VariableDeclaration> params = fnDefn.getDeclaration().getParameters();
        if (params.size() != expr.getArgs().size()) {
            throw new RuntimeException(
                    "Args length and param length do not match. This should have been caught in type checking.");
        }
        for (int i = 0; i < params.size(); i++) {
            VariableDeclaration param = params.get(i);
            Expression arg = expr.getArgs().get(i);
            asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0,
                    new RegisterShift<>(Register.SP,
                            new UInt15MultipleOf8(arg.getRegisterTracker().getStackPositionBytes()))));
            asm.add(InstructionType.SUB_IMM.of(Register.X1, Register.SP,
                    new UInt12(fnDefn.getRegisterScope().getTotalWidthBits() / 8
                            - param.getRegisterTracker().getStackPositionBytes())));
            asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0,
                    new RegisterShift<>(Register.X1, new UInt15MultipleOf8(0))));
        }
        asm.add(InstructionType.BL.of(new Label(fnDefn.getDeclaration().getName())));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0,
                new RegisterShift<>(Register.SP,
                        new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(IntegerLiteralExpression expr, Object... objs) {
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.MOV.of(
                Register.X0,
                new UInt64(BigInteger.valueOf(expr.getValue()))));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(
                Register.X0,
                new RegisterShift<>(Register.SP,
                        new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(LessThanExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(LessThanOrEqualExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(MemberAccessExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(NotExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(NotEqualExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getLeft().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRight().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.CMP.of(Register.X0, Register.X1));
        asm.add(InstructionType.CSET.of(Register.X0, new Condition(Code.NE)));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(OrExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ProductExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getLeft().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRight().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.MUL.of(Register.X0, Register.X0, Register.X1));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(StructLiteralExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(SubtractExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getLeft().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRight().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.SUB.of(Register.X0, Register.X0, Register.X1));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(SumExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getLeft().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X1, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRight().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.ADD.of(Register.X0, Register.X0, Register.X1));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(expr.getRegisterTracker().getStackPositionBytes()))));
    }

    @Override
    public void visit(UnaryMinusExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(VariableExpression expr, Object... objs) {
        // Something to determine the register
    }

    @Override
    public void visit(ParenthesesExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(AssignmentStatement stmt, Object... objs) {
        stmt.getExpression().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        // FunctionDefinition fn = (FunctionDefinition) objs[1];
        RegisterTracker register;
        if (stmt.getLValue().isPresent()) {
            LValue lvalue = stmt.getLValue().get();
            throw new UnsupportedOperationException("LValues not supported yet in assignment statements");
        } else {
            VariableDeclaration var = stmt.getVariableDeclaration().get();
            register = var.getRegisterTracker();
        }
        // asm.add(InstructionType.MOV.of(Register.virtual(register),
        // Register.virtual(stmt.getExpression())));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(stmt.getExpression().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.X0,
                new RegisterShift<>(Register.SP, new UInt15MultipleOf8(register.getStackPositionBytes()))));
    }

    @Override
    public void visit(Block block, Object... objs) {
        for (Visitable s : block.getStatements()) {
            s.accept(this, objs);
        }
    }

    @Override
    public void visit(ExpressionStatement stmt, Object... objs) {
        stmt.getExpression().accept(this, objs);
    }

    private static int FOR_LABEL_COUNTER = 0;

    @Override
    public void visit(ForStatement stmt, Object... objs) {
        AssemblyFile asm = (AssemblyFile) objs[0];
        Label before = new Label("before_for_" + FOR_LABEL_COUNTER);
        Label after = new Label("after_for_" + FOR_LABEL_COUNTER++);

        asm.add(InstructionType.LABEL.of(before));
        stmt.getCondition().accept(this, objs);
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(stmt.getCondition().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.CMP.of(Register.X0, new UInt12(0)));
        asm.add(InstructionType.B_EQ.of(after));
        stmt.getBlock().accept(this, objs);
        asm.add(InstructionType.B.of(before));
        asm.add(InstructionType.LABEL.of(after));
    }

    @Override
    public void visit(FunctionDeclaration fn, Object... objs) {
        AssemblyFile asm = (AssemblyFile) objs[0];
        FunctionDefinition defn = (FunctionDefinition) objs[1];
        RegisterScope scope = defn.getRegisterScope();
        int stackDepthBytes = scope.getTotalWidthBits() / 8;
        asm.add(InstructionType.LABEL.of(new Label(fn.getName())));
        asm.add(InstructionType.SUB_IMM.of(Register.SP, Register.SP, new UInt12(stackDepthBytes)));
        asm.add(InstructionType.STR_REGISTER_OFFSET.of(Register.W30,
                new RegisterShift<>(Register.SP,
                        new UInt15MultipleOf8(defn.getReturnProgramCounterRegister().getStackPositionBytes()))));
        fn.getBlock().accept(this, asm, defn);

        asm.add(InstructionType.LABEL.of(defn.getReturnLabel()));
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.W30,
                new RegisterShift<>(Register.SP,
                        new UInt15MultipleOf8(defn.getReturnProgramCounterRegister().getStackPositionBytes()))));
        asm.add(InstructionType.ADD_IMM.of(Register.SP, Register.SP, new UInt12(stackDepthBytes)));
        asm.add(InstructionType.RET.of());
    }

    private static int IF_LABEL_COUNTER = 0;

    @Override
    public void visit(IfStatement stmt, Object... objs) {

        stmt.getCondition().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        // FunctionDefinition fn = (FunctionDefinition) objs[1];

        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(stmt.getCondition().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.CMP.of(Register.X0, new UInt12(0)));
        asm.add(InstructionType.B_EQ.of(new Label("after_if_" + IF_LABEL_COUNTER)));
        stmt.getBlock().accept(this, objs);
        asm.add(InstructionType.LABEL.of(new Label("after_if_" + IF_LABEL_COUNTER)));
        IF_LABEL_COUNTER++;
    }

    @Override
    public void visit(Import stmt, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ImportCatalog imports, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(LValue stmt, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ReturnStatement stmt, Object... objs) {
        stmt.getExpression().accept(this, objs);
        AssemblyFile asm = (AssemblyFile) objs[0];
        FunctionDefinition fn = (FunctionDefinition) objs[1];

        // For now, throw the result into X0.
        asm.add(InstructionType.LDR_REGISTER_OFFSET.of(Register.X0, new RegisterShift<>(Register.SP,
                new UInt15MultipleOf8(stmt.getExpression().getRegisterTracker().getStackPositionBytes()))));
        asm.add(InstructionType.B.of(fn.getReturnLabel()));
    }

    @Override
    public void visit(TypeDeclaration decl, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(VariableDeclaration decl, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(File file, Object... objs) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(IntType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(BooleanType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(StringType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(VoidType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(UnionType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(StructType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(NamedType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

}

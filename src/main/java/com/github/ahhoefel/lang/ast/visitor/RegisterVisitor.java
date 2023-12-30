package com.github.ahhoefel.lang.ast.visitor;

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
import com.github.ahhoefel.lang.ast.expression.IndexAccessExpression;
import com.github.ahhoefel.lang.ast.expression.IntegerLiteralExpression;
import com.github.ahhoefel.lang.ast.expression.LessThanExpression;
import com.github.ahhoefel.lang.ast.expression.LessThanOrEqualExpression;
import com.github.ahhoefel.lang.ast.expression.MemberAccessExpression;
import com.github.ahhoefel.lang.ast.expression.NewExpression;
import com.github.ahhoefel.lang.ast.expression.NotEqualExpression;
import com.github.ahhoefel.lang.ast.expression.NotExpression;
import com.github.ahhoefel.lang.ast.expression.OrExpression;
import com.github.ahhoefel.lang.ast.expression.ParenthesesExpression;
import com.github.ahhoefel.lang.ast.expression.ProductExpression;
import com.github.ahhoefel.lang.ast.expression.StructLiteralExpression;
import com.github.ahhoefel.lang.ast.expression.SubtractExpression;
import com.github.ahhoefel.lang.ast.expression.SumExpression;
import com.github.ahhoefel.lang.ast.expression.TypeExpression;
import com.github.ahhoefel.lang.ast.expression.UnaryMinusExpression;
import com.github.ahhoefel.lang.ast.expression.VariableExpression;
import com.github.ahhoefel.lang.ast.statements.AssignmentStatement;
import com.github.ahhoefel.lang.ast.statements.ExpressionStatement;
import com.github.ahhoefel.lang.ast.statements.ForStatement;
import com.github.ahhoefel.lang.ast.statements.IfStatement;
import com.github.ahhoefel.lang.ast.statements.ReturnStatement;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols.FunctionDefinition;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.symbols.RegisterScope;
import com.github.ahhoefel.lang.ast.type.ExpressionType;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.lang.ast.type.Type.BooleanType;
import com.github.ahhoefel.lang.ast.type.Type.IntType;
import com.github.ahhoefel.lang.ast.type.Type.StringType;
import com.github.ahhoefel.lang.ast.type.Type.TypeType;
import com.github.ahhoefel.lang.ast.type.Type.VoidType;

public class RegisterVisitor implements Visitor {

    public void assignRegisters(GlobalSymbols globals) {
        for (FileSymbols f : globals.getFiles()) {
            for (FunctionDefinition fn : f.getFunctions()) {
                fn.getDeclaration().accept(this, fn);
            }
        }
    }

    @Override
    public void visit(AndExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.BOOL.getWidthBits()));
    }

    @Override
    public void visit(BooleanLiteralExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.setRegisterTracker(scope.createRegister(Type.BOOL.getWidthBits()));
    }

    @Override
    public void visit(EqualExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.BOOL.getWidthBits()));
    }

    @Override
    public void visit(FunctionInvocationExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        for (Expression arg : expr.getArgs()) {
            arg.accept(this, objs);
        }
        if (expr.getSymbolReference() == null) {
            throw new RuntimeException("No symbol reference for identifier:" + expr.getIdentifier());
        }
        Type returnType = expr.getSymbolReference().getResolution().get().getFunctionDefinition().get().getDeclaration()
                .getReturnType();
        expr.setRegisterTracker(scope.createRegister(returnType.getWidthBits()));
    }

    @Override
    public void visit(NewExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getType().accept(this, objs);
        for (Expression arg : expr.getArgs()) {
            arg.accept(this, objs);
        }
        expr.setRegisterTracker(scope.createRegister(Type.getWidthBits(expr.getType())));
        expr.setWidthRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
        expr.setArrayLengthRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
        expr.setArrayItemWidthRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(TypeExpression expr, Object... objs) {
        expr.getStoredType().accept(this, objs);
    }

    @Override
    public void visit(IntegerLiteralExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(LessThanExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.BOOL.getWidthBits()));
    }

    @Override
    public void visit(LessThanOrEqualExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.BOOL.getWidthBits()));
    }

    @Override
    public void visit(MemberAccessExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getExpression().accept(this, objs);
        Expression subjectType = expr.getExpression().getType();
        Expression memberType = Type.getMemberType(subjectType, expr.getMember().getValue());
        expr.setRegisterTracker(scope.createRegister(Type.getWidthBits(memberType)));
    }

    @Override
    public void visit(NotExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(NotEqualExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.BOOL.getWidthBits()));
    }

    @Override
    public void visit(OrExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ProductExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(StructLiteralExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(SubtractExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(SumExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        expr.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(UnaryMinusExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(VariableExpression expr, Object... objs) {
        // No-op. RegisterTrackes are assigned at variable declaration.
    }

    @Override
    public void visit(ParenthesesExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(IndexAccessExpression expr, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        expr.getSubject().accept(this, objs);
        expr.getIndex().accept(this, objs);
        expr.getType().accept(this, objs); // ???!
        expr.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
        expr.setWidthRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(AssignmentStatement stmt, Object... objs) {
        stmt.getExpression().accept(this, objs);
        if (stmt.getLValue().isPresent()) {
            stmt.getLValue().get().accept(this, objs);
        }
        if (stmt.getVariableDeclaration().isPresent()) {
            stmt.getVariableDeclaration().get().accept(this, objs);
        }
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

    @Override
    public void visit(ForStatement stmt, Object... objs) {
        stmt.getCondition().accept(this, objs);
        stmt.getBlock().accept(this, objs);
    }

    @Override
    public void visit(FunctionDeclaration fn, Object... objs) {
        FunctionDefinition defn = (FunctionDefinition) objs[0];
        // TODO: consider separating INT type and return address width.
        // Should it be 32 bits if it's in w30?
        defn.setReturnProgramCounterRegister(defn.getRegisterScope().createRegister(Type.INT.getWidthBits()));
        for (VariableDeclaration v : fn.getParameters()) {
            v.accept(this, objs);
        }
        fn.getBlock().accept(this, objs);
    }

    @Override
    public void visit(IfStatement stmt, Object... objs) {
        stmt.getCondition().accept(this, objs);
        stmt.getBlock().accept(this, objs);

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
        stmt.getExpression().accept(this, objs);
    }

    @Override
    public void visit(ReturnStatement stmt, Object... objs) {
        stmt.getExpression().accept(this, objs);
    }

    @Override
    public void visit(TypeDeclaration decl, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(VariableDeclaration decl, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        decl.setRegisterTracker(scope.createRegister(Type.getWidthBits(decl.getType())));
        if (decl.getType() instanceof IndexAccessExpression) {
            IndexAccessExpression type = (IndexAccessExpression) decl.getType();
            if (!(type.getSubject() instanceof VariableExpression)) {
                throw new RuntimeException(
                        "IndexAccessExpressions used in variable declarations must have Array as subject.");
            }
            if (!((VariableExpression) type.getSubject()).getIdentifier().equals("Array")) {
                throw new RuntimeException(
                        "IndexAccessExpressions used in variable declarations must have Array as subject.");
            }
            decl.setArrayLengthRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
            decl.setArrayItemWidthRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
        }
    }

    @Override
    public void visit(File file, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(IntType type, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        // Register store the type index of "int" rather than an int value.
        type.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(BooleanType type, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        // Register store the type index of "bool" rather than an bool value.
        type.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(StringType type, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        // Register store the type index of "string" rather than an bool value.
        type.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(VoidType type, Object... objs) {
        RegisterScope scope = ((FunctionDefinition) objs[0]).getRegisterScope();
        // Register store the type index of "void" rather than an bool value.
        type.setRegisterTracker(scope.createRegister(Type.INT.getWidthBits()));
    }

    @Override
    public void visit(TypeType type, Object... objs) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ExpressionType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

}
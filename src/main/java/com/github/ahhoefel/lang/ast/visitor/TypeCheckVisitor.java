package com.github.ahhoefel.lang.ast.visitor;

import java.util.ArrayList;
import java.util.List;

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
import com.github.ahhoefel.lang.ast.type.ExpressionType;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.lang.ast.type.Type.BooleanType;
import com.github.ahhoefel.lang.ast.type.Type.IntType;
import com.github.ahhoefel.lang.ast.type.Type.StringType;
import com.github.ahhoefel.lang.ast.type.Type.TypeType;
import com.github.ahhoefel.lang.ast.type.Type.VoidType;
import com.github.ahhoefel.parser.Locateable;

public class TypeCheckVisitor implements Visitor {

    public static class TypeError {

        private Expression expectedType;
        private Expression actualType;
        private Locateable location;

        public TypeError(Expression expectedType, Expression actualType, Locateable location) {
            this.expectedType = expectedType;
            this.actualType = actualType;
            this.location = location;
        }

        public String toString() {
            return "Expected type: " + expectedType + "\n" +
                    "Actual type: " + actualType + "\n" +
                    "Location: " + location.getLocation();

        }
    }

    public List<TypeError> check(GlobalSymbols globals) {
        List<TypeError> errors = new ArrayList<>();
        for (FileSymbols f : globals.getFiles()) {
            for (FunctionDefinition fn : f.getFunctions()) {
                fn.getDeclaration().accept(this, fn, errors);
            }
        }
        return errors;
    }

    @Override
    public void visit(AndExpression expr, Object... objs) {
        // FunctionDefinition defn = (FunctionDefinition) objs[0];
        List<TypeError> errors = (List<TypeError>) objs[1];
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        if (!expr.getLeft().getType().equals(Type.BOOL)) {
            errors.add(new TypeError(Type.BOOL, expr.getLeft().getType(), expr));
        }
        if (!expr.getRight().getType().equals(Type.BOOL)) {
            errors.add(new TypeError(Type.BOOL, expr.getRight().getType(), expr));
        }
    }

    @Override
    public void visit(BooleanLiteralExpression expr, Object... objs) {
    }

    @Override
    public void visit(EqualExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(FunctionInvocationExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(IntegerLiteralExpression expr, Object... objs) {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(OrExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ProductExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(StructLiteralExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(SubtractExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(SumExpression expr, Object... objs) {
        // FunctionDefinition defn = (FunctionDefinition) objs[0];
        List<TypeError> errors = (List<TypeError>) objs[1];
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
        if (!expr.getLeft().getType().equals(Type.INT)) {
            errors.add(new TypeError(Type.INT, expr.getLeft().getType(), expr));
        }
        if (!expr.getRight().getType().equals(Type.INT)) {
            errors.add(new TypeError(Type.INT, expr.getRight().getType(), expr));
        }
    }

    @Override
    public void visit(UnaryMinusExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(VariableExpression expr, Object... objs) {

    }

    @Override
    public void visit(ParenthesesExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(NewExpression expr, Object... objs) {
        expr.getType().accept(this, objs);
        for (Expression arg : expr.getArgs()) {
            arg.accept(this, objs);
        }
    }

    @Override
    public void visit(TypeExpression expr, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(IndexAccessExpression expr, Object... objs) {
        expr.getSubject().accept(this, objs);
        expr.getIndex().accept(this, objs);
    }

    @Override
    public void visit(AssignmentStatement stmt, Object... objs) {
        // FunctionDefinition defn = (FunctionDefinition) objs[0];
        List<TypeError> errors = (List<TypeError>) objs[1];
        if (stmt.getVariableDeclaration().isPresent()) {
            stmt.getVariableDeclaration().get().accept(this, objs);
            Expression expectedType = stmt.getVariableDeclaration().get().getType();
            Expression actualType = stmt.getExpression().getType();
            if (!expectedType.equals(actualType)) {
                errors.add(new TypeError(expectedType, actualType, stmt.getVariableDeclaration().get()));
            }
        }
        if (stmt.getLValue().isPresent()) {
            stmt.getLValue().get().accept(this, objs);
            Expression expectedType = stmt.getLValue().get().getType();
            Expression actualType = stmt.getExpression().getType();
            if (!expectedType.equals(actualType)) {
                errors.add(new TypeError(expectedType, actualType, stmt));
            }
        }
        stmt.getExpression().accept(this, objs);
    }

    @Override
    public void visit(Block block, Object... objs) {
        // FunctionDefinition defn = (FunctionDefinition) objs[0];
        // List<TypeError> errors = (List<TypeError>) objs[1];
        for (Visitable stmt : block.getStatements()) {
            stmt.accept(this, objs);
        }
    }

    @Override
    public void visit(ExpressionStatement stmt, Object... objs) {
        stmt.getExpression().accept(this, objs);
    }

    @Override
    public void visit(ForStatement stmt, Object... objs) {
        if (!stmt.getCondition().getType().equals(Type.BOOL)) {
            throw new RuntimeException(
                    "For statement conditions should be boolen typed:" + stmt.getCondition().getType());
        }
        stmt.getBlock().accept(this, objs);
    }

    @Override
    public void visit(FunctionDeclaration fn, Object... objs) {
        // FunctionDefinition defn = (FunctionDefinition) objs[0];
        // List<TypeError> errors = (List<TypeError>) objs[1];
        for (VariableDeclaration param : fn.getParameters()) {
            param.accept(this, objs);
        }
        fn.getBlock().accept(this, objs);
    }

    @Override
    public void visit(IfStatement stmt, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
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

    }

    @Override
    public void visit(ReturnStatement stmt, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(TypeDeclaration decl, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(VariableDeclaration decl, Object... objs) {
        // FunctionDefinition defn = (FunctionDefinition) objs[0];
        List<TypeError> errors = (List<TypeError>) objs[1];
        if (!decl.getType().getType().equals(Type.TYPE)) {
            errors.add(new TypeError(Type.TYPE, decl.getType().getType(), decl));
        }
    }

    @Override
    public void visit(File file, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(IntType type, Object... objs) {

    }

    @Override
    public void visit(BooleanType type, Object... objs) {

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
    public void visit(TypeType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ExpressionType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

}

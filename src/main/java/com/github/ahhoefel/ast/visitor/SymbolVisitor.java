package com.github.ahhoefel.ast.visitor;

import com.github.ahhoefel.ast.AssignmentStatement;
import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.ExpressionStatement;
import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.ForStatement;
import com.github.ahhoefel.ast.FunctionDeclaration;
import com.github.ahhoefel.ast.IfStatement;
import com.github.ahhoefel.ast.Import;
import com.github.ahhoefel.ast.ImportCatalog;
import com.github.ahhoefel.ast.LValue;
import com.github.ahhoefel.ast.NamedType;
import com.github.ahhoefel.ast.ReturnStatement;
import com.github.ahhoefel.ast.StructType;
import com.github.ahhoefel.ast.Type.BooleanType;
import com.github.ahhoefel.ast.Type.IntType;
import com.github.ahhoefel.ast.Type.StringType;
import com.github.ahhoefel.ast.Type.VoidType;
import com.github.ahhoefel.ast.TypeDeclaration;
import com.github.ahhoefel.ast.UnionType;
import com.github.ahhoefel.ast.VariableDeclaration;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.ast.expression.AndExpression;
import com.github.ahhoefel.ast.expression.BooleanLiteralExpression;
import com.github.ahhoefel.ast.expression.EqualExpression;
import com.github.ahhoefel.ast.expression.FunctionInvocationExpression;
import com.github.ahhoefel.ast.expression.IntegerLiteralExpression;
import com.github.ahhoefel.ast.expression.LessThanExpression;
import com.github.ahhoefel.ast.expression.LessThanOrEqualExpression;
import com.github.ahhoefel.ast.expression.MemberAccessExpression;
import com.github.ahhoefel.ast.expression.NotEqualExpression;
import com.github.ahhoefel.ast.expression.NotExpression;
import com.github.ahhoefel.ast.expression.OrExpression;
import com.github.ahhoefel.ast.expression.ParenthesesExpression;
import com.github.ahhoefel.ast.expression.ProductExpression;
import com.github.ahhoefel.ast.expression.StructLiteralExpression;
import com.github.ahhoefel.ast.expression.SubtractExpression;
import com.github.ahhoefel.ast.expression.SumExpression;
import com.github.ahhoefel.ast.expression.UnaryMinusExpression;
import com.github.ahhoefel.ast.expression.VariableExpression;

public class SymbolVisitor implements Visitor {

    @Override
    public void visit(AndExpression expr) {

    }

    @Override
    public void visit(BooleanLiteralExpression expr) {

    }

    @Override
    public void visit(EqualExpression expr) {

    }

    @Override
    public void visit(FunctionInvocationExpression expr) {

    }

    @Override
    public void visit(IntegerLiteralExpression expr) {

    }

    @Override
    public void visit(LessThanExpression expr) {

    }

    @Override
    public void visit(LessThanOrEqualExpression expr) {

    }

    @Override
    public void visit(MemberAccessExpression expr) {

    }

    @Override
    public void visit(NotExpression expr) {

    }

    @Override
    public void visit(OrExpression expr) {

    }

    @Override
    public void visit(ProductExpression expr) {

    }

    @Override
    public void visit(StructLiteralExpression expr) {

    }

    @Override
    public void visit(SubtractExpression expr) {

    }

    @Override
    public void visit(SumExpression expr) {

    }

    @Override
    public void visit(UnaryMinusExpression expr) {

    }

    @Override
    public void visit(VariableExpression expr) {

    }

    @Override
    public void visit(AssignmentStatement stmt) {

    }

    @Override
    public void visit(Block block) {

    }

    @Override
    public void visit(ExpressionStatement stmt) {

    }

    @Override
    public void visit(ForStatement stmt) {

    }

    @Override
    public void visit(FunctionDeclaration fn) {

    }

    @Override
    public void visit(IfStatement stmt) {

    }

    @Override
    public void visit(Import stmt) {

    }

    @Override
    public void visit(ImportCatalog imports) {

    }

    @Override
    public void visit(LValue stmt) {

    }

    @Override
    public void visit(ReturnStatement stmt) {

    }

    @Override
    public void visit(TypeDeclaration decl) {

    }

    @Override
    public void visit(VariableDeclaration decl) {

    }

    @Override
    public void visit(File file) {
    }

    @Override
    public void visit(NotEqualExpression expr) {

    }

    @Override
    public void visit(ParenthesesExpression expr) {

    }

    @Override
    public void visit(IntType type) {

    }

    @Override
    public void visit(BooleanType type) {

    }

    @Override
    public void visit(StringType type) {

    }

    @Override
    public void visit(VoidType type) {

    }

    @Override
    public void visit(UnionType type) {

    }

    @Override
    public void visit(StructType type) {

    }

    @Override
    public void visit(NamedType type) {

    }

}

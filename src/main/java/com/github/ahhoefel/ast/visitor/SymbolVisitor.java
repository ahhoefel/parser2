package com.github.ahhoefel.ast.visitor;

import java.nio.file.Path;

import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.Declaration;
import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.FunctionDeclaration;
import com.github.ahhoefel.ast.Import;
import com.github.ahhoefel.ast.ImportCatalog;
import com.github.ahhoefel.ast.LValue;
import com.github.ahhoefel.ast.Target;
import com.github.ahhoefel.ast.TypeDeclaration;
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
import com.github.ahhoefel.ast.statements.AssignmentStatement;
import com.github.ahhoefel.ast.statements.ExpressionStatement;
import com.github.ahhoefel.ast.statements.ForStatement;
import com.github.ahhoefel.ast.statements.IfStatement;
import com.github.ahhoefel.ast.statements.ReturnStatement;
import com.github.ahhoefel.ast.statements.Statement;
import com.github.ahhoefel.ast.symbols.FileSymbols;
import com.github.ahhoefel.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.ast.symbols.LocalSymbols;
import com.github.ahhoefel.ast.type.NamedType;
import com.github.ahhoefel.ast.type.StructType;
import com.github.ahhoefel.ast.type.UnionType;
import com.github.ahhoefel.ast.type.Type.BooleanType;
import com.github.ahhoefel.ast.type.Type.IntType;
import com.github.ahhoefel.ast.type.Type.StringType;
import com.github.ahhoefel.ast.type.Type.VoidType;

public class SymbolVisitor implements Visitor {

    private Path source;

    public SymbolVisitor(Path source) {
        this.source = source;
    }

    @Override
    public void visit(File file, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        if (g.containsTarget(file.getTarget())) {
            return;
        }
        FileSymbols symbols = g.add(file.getTarget());
        file.getImports().accept(this, g, symbols);
        for (Declaration d : file.getDeclarations()) {
            d.accept(this, g, symbols);
        }
    }

    @Override
    public void visit(ImportCatalog imports, Object... objs) {
        for (Import i : imports.getImports()) {
            i.accept(this, objs);
        }
    }

    @Override
    public void visit(Import stmt, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        Target target = new Target(source, stmt.getTargetString());
        if (g.containsTarget(target)) {
            symbols.addImport(target, g.get(target));
        } else {
            g.addUnresolvedImport(target, symbols);
        }
    }

    @Override
    public void visit(FunctionDeclaration fn, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = new LocalSymbols();
        symbols.addFunction(fn);
        fn.getBlock().accept(this, g, symbols, locals);
    }

    @Override
    public void visit(Block block, Object... objs) {
        for (Statement s : block.statements) {
            s.accept(this, objs);
        }
    }

    @Override
    public void visit(AssignmentStatement stmt, Object... objs) {
        stmt.getLValue().accept(this, objs);
        stmt.getExpression().accept(this, objs);
    }

    @Override
    public void visit(LValue stmt, Object... objs) {
        if (stmt.isDeclaration()) {
            // Add to local symbols.
        } else {
            stmt.getExpression().accept(this, objs);
        }
    }

    @Override
    public void visit(AndExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(BooleanLiteralExpression expr, Object... objs) {
        // Do nothing
    }

    @Override
    public void visit(EqualExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(FunctionInvocationExpression expr, Object... objs) {

    }

    @Override
    public void visit(IntegerLiteralExpression expr, Object... objs) {
        // Do nothing
    }

    @Override
    public void visit(LessThanExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(LessThanOrEqualExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(MemberAccessExpression expr, Object... objs) {

    }

    @Override
    public void visit(NotExpression expr, Object... objs) {
        expr.getExpression().accept(this, objs);
    }

    @Override
    public void visit(OrExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(ProductExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(StructLiteralExpression expr, Object... objs) {

    }

    @Override
    public void visit(SubtractExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(SumExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(UnaryMinusExpression expr, Object... objs) {
        expr.getExpression().accept(this, objs);
    }

    @Override
    public void visit(VariableExpression expr, Object... objs) {

    }

    @Override
    public void visit(ExpressionStatement stmt, Object... objs) {

    }

    @Override
    public void visit(ForStatement stmt, Object... objs) {

    }

    @Override
    public void visit(IfStatement stmt, Object... objs) {

    }

    @Override
    public void visit(ReturnStatement stmt, Object... objs) {

    }

    @Override
    public void visit(TypeDeclaration decl, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        symbols.addType(decl.getIdentifier(), decl.getType());
    }

    @Override
    public void visit(VariableDeclaration decl, Object... objs) {

    }

    @Override
    public void visit(NotEqualExpression expr, Object... objs) {

    }

    @Override
    public void visit(ParenthesesExpression expr, Object... objs) {

    }

    @Override
    public void visit(IntType type, Object... objs) {

    }

    @Override
    public void visit(BooleanType type, Object... objs) {

    }

    @Override
    public void visit(StringType type, Object... objs) {

    }

    @Override
    public void visit(VoidType type, Object... objs) {

    }

    @Override
    public void visit(UnionType type, Object... objs) {

    }

    @Override
    public void visit(StructType type, Object... objs) {

    }

    @Override
    public void visit(NamedType type, Object... objs) {

    }

}

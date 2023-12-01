package com.github.ahhoefel.lang.ast.visitor;

import java.nio.file.Path;

import com.github.ahhoefel.lang.ast.Block;
import com.github.ahhoefel.lang.ast.Declaration;
import com.github.ahhoefel.lang.ast.File;
import com.github.ahhoefel.lang.ast.FunctionDeclaration;
import com.github.ahhoefel.lang.ast.Import;
import com.github.ahhoefel.lang.ast.ImportCatalog;
import com.github.ahhoefel.lang.ast.LValue;
import com.github.ahhoefel.lang.ast.Target;
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
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols;
import com.github.ahhoefel.lang.ast.symbols.SymbolReference;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols.SymbolIndex;
import com.github.ahhoefel.lang.ast.type.ExpressionType;
import com.github.ahhoefel.lang.ast.type.Type.BooleanType;
import com.github.ahhoefel.lang.ast.type.Type.IntType;
import com.github.ahhoefel.lang.ast.type.Type.StringType;
import com.github.ahhoefel.lang.ast.type.Type.VoidType;
import com.github.ahhoefel.lang.ast.type.Type.TypeType;

// Visitors aren't type safe. Here's the list of expected arguments and return types
// File(GlobalSymbols, FileSymbols)
// Declaration(GlobalSymbols, FileSymbols)
// Block(GlobalSymbols, FileSymbols, LocalSymbols, SymbolIndex)
// Statement(GlobalSymbols, FileSymbols, LocalSymbols, SymbolIndex, SymbolIndex [result])
// Expression(GlobalSymbols, FileSymbols, LocalSymbols, SymbolIndex, SymbolIndex [result])

public class SymbolVisitor implements Visitor {

    private Path source;

    public SymbolVisitor(Path source) {
        this.source = source;
    }

    @Override
    public void visit(File file, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
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
        // GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        Target target = new Target(source, stmt.getTargetString());
        symbols.addImport(stmt.getShortName(), target);
    }

    @Override
    public void visit(FunctionDeclaration fn, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = new LocalSymbols();
        symbols.addFunction(fn, locals);
        SymbolIndex prevSymbolIndex = new SymbolIndex(-1);
        SymbolIndex resultSymbolIndex = new SymbolIndex(-1);
        for (VariableDeclaration v : fn.getParameters()) {
            v.accept(this, g, symbols, locals, prevSymbolIndex, resultSymbolIndex);
            prevSymbolIndex = new SymbolIndex(resultSymbolIndex.value);
        }
        System.out.println(locals.toString());
        fn.getBlock().accept(this, g, symbols, locals, prevSymbolIndex);
    }

    @Override
    public void visit(Block block, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        SymbolIndex resultSymbolIndex = new SymbolIndex(prevSymbolIndex.value);
        for (Visitable stmt : block.getStatements()) {
            stmt.accept(this, g, symbols, locals, prevSymbolIndex, resultSymbolIndex);
            prevSymbolIndex = new SymbolIndex(resultSymbolIndex.value);
        }
    }

    @Override
    public void visit(AssignmentStatement stmt, Object... objs) {
        // GlobalSymbols g = (GlobalSymbols) objs[0];
        // FileSymbols symbols = (FileSymbols) objs[1];
        // LocalSymbols locals = (LocalSymbols) objs[2];
        // SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        // SymbolIndex resultSymbolIndex = (SymbolIndex) objs[4];
        if (stmt.getLValue().isPresent()) {
            stmt.getLValue().get().accept(this, objs);
        } else if (stmt.getVariableDeclaration().isPresent()) {
            stmt.getVariableDeclaration().get().accept(this, objs);
        } else {
            throw new RuntimeException("Assignment statement should have either an lvalue or variable declaration");
        }
        stmt.getExpression().accept(this, objs);
    }

    @Override
    public void visit(LValue stmt, Object... objs) {
        stmt.getExpression().accept(this, objs);
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
        GlobalSymbols globals = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        expr.setSymbolReference(new SymbolReference(expr.getIdentifier(), expr.getLocation(), globals, symbols, locals,
                prevSymbolIndex));
        if (expr.getImplicitArg().isPresent()) {
            expr.getImplicitArg().get().accept(this, objs);
        }
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
        expr.getStoredType().accept(this, objs);
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
        GlobalSymbols globals = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        // Optional<LocalSymbol> symbol = locals.get(expr.getIdentifier(),
        // prevSymbolIndex);
        // if (symbol.isEmpty()) {
        // throw new RuntimeException("Reference to undeclared variable: " +
        // expr.getIdentifier());
        // }
        expr.setSymbolReference(new SymbolReference(expr.getIdentifier(), expr.getLocation(), globals, symbols, locals,
                prevSymbolIndex));
    }

    @Override
    public void visit(ExpressionStatement stmt, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        // SymbolIndex resultSymbolIndex = (SymbolIndex) objs[4];
        stmt.getExpression().accept(this, g, symbols, locals, prevSymbolIndex);
    }

    @Override
    public void visit(IndexAccessExpression expr, Object... objs) {
        expr.getSubject().accept(this, objs);
        expr.getIndex().accept(this, objs);
    }

    @Override
    public void visit(ForStatement stmt, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        // SymbolIndex resultSymbolIndex = (SymbolIndex) objs[4];
        stmt.getCondition().accept(this, g, symbols, locals, prevSymbolIndex);
        stmt.getBlock().accept(this, g, symbols, locals, prevSymbolIndex);
    }

    @Override
    public void visit(IfStatement stmt, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        // SymbolIndex resultSymbolIndex = (SymbolIndex) objs[4];
        stmt.setLocalSymbolIndex(prevSymbolIndex);
        stmt.getCondition().accept(this, g, symbols, locals, prevSymbolIndex);
        stmt.getBlock().accept(this, g, symbols, locals, prevSymbolIndex);
    }

    @Override
    public void visit(ReturnStatement stmt, Object... objs) {
        GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        stmt.setLocalSymbolIndex(prevSymbolIndex);
        stmt.getExpression().accept(this, g, symbols, locals, prevSymbolIndex);
    }

    @Override
    public void visit(TypeDeclaration decl, Object... objs) {
        // GlobalSymbols g = (GlobalSymbols) objs[0];
        FileSymbols symbols = (FileSymbols) objs[1];
        symbols.addType(decl.getIdentifier(), decl.getType());
    }

    @Override
    public void visit(VariableDeclaration decl, Object... objs) {
        // GlobalSymbols g = (GlobalSymbols) objs[0];
        // FileSymbols symbols = (FileSymbols) objs[1];
        LocalSymbols locals = (LocalSymbols) objs[2];
        SymbolIndex prevSymbolIndex = (SymbolIndex) objs[3];
        SymbolIndex resultSymbolIndex = (SymbolIndex) objs[4];
        resultSymbolIndex.value = locals.put(decl, prevSymbolIndex).value;
    }

    @Override
    public void visit(NotEqualExpression expr, Object... objs) {
        expr.getLeft().accept(this, objs);
        expr.getRight().accept(this, objs);
    }

    @Override
    public void visit(ParenthesesExpression expr, Object... objs) {
        expr.getExpression().accept(this, objs);
    }

    @Override
    public void visit(IntType type, Object... objs) {
        // No implementation needed
    }

    @Override
    public void visit(BooleanType type, Object... objs) {
        // No implementation needed
    }

    @Override
    public void visit(StringType type, Object... objs) {
        // No implementation needed
    }

    @Override
    public void visit(VoidType type, Object... objs) {
        // No implementation needed
    }

    @Override
    public void visit(TypeType type, Object... objs) {
        // No implementation needed
    }

    @Override
    public void visit(ExpressionType type, Object... objs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
}

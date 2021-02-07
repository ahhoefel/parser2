package com.github.ahhoefel.ast.visitor;

import java.util.List;
import java.util.Map;

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
import com.github.ahhoefel.ast.ReturnStatement;
import com.github.ahhoefel.ast.Type;
import com.github.ahhoefel.ast.TypeDeclaration;
import com.github.ahhoefel.ast.VariableDeclaration;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.ast.expression.AndExpression;
import com.github.ahhoefel.ast.expression.BooleanLiteralExpression;
import com.github.ahhoefel.ast.expression.EqualExpression;
import com.github.ahhoefel.ast.expression.Expression;
import com.github.ahhoefel.ast.expression.FunctionInvocationExpression;
import com.github.ahhoefel.ast.expression.IntegerLiteralExpression;
import com.github.ahhoefel.ast.expression.LessThanExpression;
import com.github.ahhoefel.ast.expression.LessThanOrEqualExpression;
import com.github.ahhoefel.ast.expression.MemberAccessExpression;
import com.github.ahhoefel.ast.expression.NotExpression;
import com.github.ahhoefel.ast.expression.OrExpression;
import com.github.ahhoefel.ast.expression.ProductExpression;
import com.github.ahhoefel.ast.expression.StructLiteralExpression;
import com.github.ahhoefel.ast.expression.SubtractExpression;
import com.github.ahhoefel.ast.expression.SumExpression;
import com.github.ahhoefel.ast.expression.UnaryMinusExpression;
import com.github.ahhoefel.ast.expression.VariableExpression;
import com.github.ahhoefel.util.IndentedString;

public class FormatVisitor implements Visitor {

    private IndentedString out = new IndentedString();

    public FormatVisitor() {

    }

    public String toString() {
        return out.toString();
    }

    @Override
    public void visit(AndExpression expr) {
        expr.getLeft().accept(this);
        out.add(" && ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(BooleanLiteralExpression expr) {
        out.add(Boolean.toString(expr.getValue()));
    }

    @Override
    public void visit(EqualExpression expr) {
        expr.getLeft().accept(this);
        out.add(" == ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(FunctionInvocationExpression expr) {
        out.add(expr.getIdentifier());
        out.add("(");
        List<Expression> args = expr.getArgs();
        for (int i = 0; i < args.size(); i++) {
            args.get(i).accept(this);
            if (i != args.size() - 1) {
                out.add(", ");
            }
        }
        out.add(")");
    }

    @Override
    public void visit(IntegerLiteralExpression expr) {
        out.add(Integer.toString(expr.getValue()));
    }

    @Override
    public void visit(LessThanExpression expr) {
        expr.getLeft().accept(this);
        out.add(" < ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(LessThanOrEqualExpression expr) {
        expr.getLeft().accept(this);
        out.add(" <= ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(MemberAccessExpression expr) {
        expr.getExpression().accept(this);
        out.add(".");
        out.add(expr.getMember().getValue());
    }

    @Override
    public void visit(NotExpression expr) {
        out.add(".");
        expr.getExpression().accept(this);
    }

    @Override
    public void visit(OrExpression expr) {
        expr.getLeft().accept(this);
        out.add(" || ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(ProductExpression expr) {
        expr.getLeft().accept(this);
        out.add(" * ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(StructLiteralExpression expr) {
        out.add(expr.getType().toString()).add("{");
        out.endLine();
        out.indent();
        for (Map.Entry<String, Expression> entry : expr.getValues().entrySet()) {
            out.add(entry.getKey()).add(": ");
            entry.getValue().accept(this);
            out.endLine();
        }
        out.endLine();
    }

    @Override
    public void visit(SubtractExpression expr) {
        expr.getLeft().accept(this);
        out.add(" - ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(SumExpression expr) {
        expr.getLeft().accept(this);
        out.add(" + ");
        expr.getRight().accept(this);
    }

    @Override
    public void visit(UnaryMinusExpression expr) {
        out.add("-");
        expr.getExpression().accept(this);
    }

    @Override
    public void visit(VariableExpression expr) {
        out.add(expr.getIdentifier());
    }

    @Override
    public void visit(AssignmentStatement stmt) {
        out.add(stmt.getLValue().toString());
        out.add(" = ");
        stmt.getExpression().accept(this);
        out.endLine();
    }

    @Override
    public void visit(Block block) {
        for (int i = 0; i < block.size(); i++) {
            block.get(i).accept(this);
        }
    }

    @Override
    public void visit(ExpressionStatement stmt) {
        stmt.getExpression().accept(this);
        out.endLine();
    }

    @Override
    public void visit(ForStatement stmt) {
        out.add("for ");
        stmt.getCondition().accept(this);
        out.add(" {").endLine();
        stmt.getBlock().accept(this);
        out.addLine("}");
    }

    @Override
    public void visit(FunctionDeclaration fn) {
        out.add("func ");
        out.add(fn.getName());
        out.add("(");
        List<Type> paramTypes = fn.getParameterTypes();
        for (int i = 0; i < paramTypes.size(); i++) {
            out.add(fn.getParameterName(i));
            if (i != paramTypes.size() - 1) {
                out.add(", ");
            }
        }
        out.add(") {");
        out.endLine();
        fn.getBlock().accept(this);
        out.addLine("}");
    }

    @Override
    public void visit(IfStatement stmt) {
        out.add("if ");
        stmt.getCondition().accept(this);
        out.add(" {").endLine();
        stmt.getBlock().accept(this);
        out.addLine("}");
    }

    @Override
    public void visit(Import stmt) {
        out.add("import ").add(stmt.getShortName()).add(stmt.getPath()).endLine();
    }

    @Override
    public void visit(ImportCatalog imports) {
        for (Import i : imports.getImports()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(LValue stmt) {
        if (stmt.isDeclaration()) {
            out.add("var ");
            out.add(stmt.getIdentifier());
            out.add(stmt.getType().toString());
        } else {
            stmt.getExpression().accept(this);
        }
    }

    @Override
    public void visit(ReturnStatement stmt) {
        out.add("return ");
        stmt.getExpression().accept(this);
    }

    @Override
    public void visit(TypeDeclaration decl) {
        out.add("type ").add(decl.getIdentifier()).add(" ").add(decl.getType().toString());
    }

    @Override
    public void visit(VariableDeclaration decl) {
        out.add("var ");
        out.add(decl.getName());
        out.add(decl.getType().toString());
    }

    @Override
    public void visit(File file) {
        file.getImports().accept(this);
        out.endLine();
        for (FunctionDeclaration f : file.getFunctions()) {
            f.accept(this);
            out.endLine();
        }
    }
}

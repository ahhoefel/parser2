package com.github.ahhoefel.ast.visitor;

import java.util.List;
import java.util.Map;

import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.Declaration;
import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.FunctionDeclaration;
import com.github.ahhoefel.ast.Import;
import com.github.ahhoefel.ast.ImportCatalog;
import com.github.ahhoefel.ast.LValue;
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
import com.github.ahhoefel.ast.type.NamedType;
import com.github.ahhoefel.ast.type.StructType;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ast.type.UnionType;
import com.github.ahhoefel.ast.type.Type.BooleanType;
import com.github.ahhoefel.ast.type.Type.IntType;
import com.github.ahhoefel.ast.type.Type.StringType;
import com.github.ahhoefel.ast.type.Type.VoidType;
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
    public void visit(NotEqualExpression expr) {
        expr.getLeft().accept(this);
        out.add(" != ");
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
        out.add("!");
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
        out.add("new ");
        out.add(expr.getType().toString()).add(" {");
        out.endLine();
        out.indent();
        for (Map.Entry<String, Expression> entry : expr.getValues().entrySet()) {
            out.add(entry.getKey()).add(": ");
            entry.getValue().accept(this);
            out.add(",").endLine();
        }
        out.unindent();
        out.add("}");
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
    public void visit(ParenthesesExpression expr) {
        out.add("(");
        expr.getExpression().accept(this);
        out.add(")");
    }

    @Override
    public void visit(AssignmentStatement stmt) {
        stmt.getLValue().accept(this);
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
            out.add(fn.getParameterName(i)).add(" ");
            paramTypes.get(i).accept(this);
            if (i != paramTypes.size() - 1) {
                out.add(", ");
            }
        }
        out.add(")");
        if (fn.getReturnType() != null && fn.getReturnType() != Type.VOID) {
            out.add(" ");
            out.add(fn.getReturnType().toString());
        }
        out.add(" {");
        out.endLine();
        out.indent();
        fn.getBlock().accept(this);
        out.unindent();
        out.addLine("}");
    }

    @Override
    public void visit(IfStatement stmt) {
        out.add("if ");
        stmt.getCondition().accept(this);
        out.add(" {").endLine().indent();
        stmt.getBlock().accept(this);
        out.unindent().addLine("}");
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
            out.add(" ");
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
        out.add("type ").add(decl.getIdentifier()).add(" ");
        decl.getType().accept(this);
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
        List<Declaration> declarations = file.getDeclarations();
        for (int i = 0; i < declarations.size(); i++) {
            declarations.get(i).accept(this);
            if (i != declarations.size() - 1) {
                out.endLine();
            }
        }
    }

    @Override
    public void visit(IntType type) {
        out.add("int");
    }

    @Override
    public void visit(BooleanType type) {
        out.add("bool");
    }

    @Override
    public void visit(StringType type) {
        out.add("string");
    }

    @Override
    public void visit(VoidType type) {
        out.add("void");
    }

    @Override
    public void visit(UnionType type) {
        out.add("???");
    }

    @Override
    public void visit(StructType type) {
        out.add("struct {").endLine().indent();

        for (String memberName : type.memberNames()) {
            Type t = type.getMember(memberName);
            out.add(memberName).add(" ");
            t.accept(this);
            out.endLine();
        }
        out.unindent().add("}").endLine();
    }

    @Override
    public void visit(NamedType type) {
        out.add(type.getIdentifier());
    }
}

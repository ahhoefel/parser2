package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.expression.*;
import com.github.ahhoefel.ast.statements.AssignmentStatement;
import com.github.ahhoefel.ast.statements.ExpressionStatement;
import com.github.ahhoefel.ast.statements.ForStatement;
import com.github.ahhoefel.ast.statements.IfStatement;
import com.github.ahhoefel.ast.statements.ReturnStatement;
import com.github.ahhoefel.ast.type.NamedType;
import com.github.ahhoefel.ast.type.StructType;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ast.type.UnionType;

public interface Visitor {
    void visit(AndExpression expr, Object... objs);

    void visit(BooleanLiteralExpression expr, Object... objs);

    void visit(EqualExpression expr, Object... objs);

    void visit(FunctionInvocationExpression expr, Object... objs);

    void visit(IntegerLiteralExpression expr, Object... objs);

    void visit(LessThanExpression expr, Object... objs);

    void visit(LessThanOrEqualExpression expr, Object... objs);

    void visit(MemberAccessExpression expr, Object... objs);

    void visit(NotExpression expr, Object... objs);

    void visit(NotEqualExpression expr, Object... objs);

    void visit(OrExpression expr, Object... objs);

    void visit(ProductExpression expr, Object... objs);

    void visit(StructLiteralExpression expr, Object... objs);

    void visit(SubtractExpression expr, Object... objs);

    void visit(SumExpression expr, Object... objs);

    void visit(UnaryMinusExpression expr, Object... objs);

    void visit(VariableExpression expr, Object... objs);

    void visit(ParenthesesExpression expr, Object... objs);

    void visit(AssignmentStatement stmt, Object... objs);

    void visit(Block block, Object... objs);

    void visit(ExpressionStatement stmt, Object... objs);

    void visit(ForStatement stmt, Object... objs);

    void visit(FunctionDeclaration fn, Object... objs);

    void visit(IfStatement stmt, Object... objs);

    void visit(Import stmt, Object... objs);

    void visit(ImportCatalog imports, Object... objs);

    void visit(LValue stmt, Object... objs);

    void visit(ReturnStatement stmt, Object... objs);

    void visit(TypeDeclaration decl, Object... objs);

    void visit(VariableDeclaration decl, Object... objs);

    void visit(File file, Object... objs);

    void visit(Type.IntType type, Object... objs);

    void visit(Type.BooleanType type, Object... objs);

    void visit(Type.StringType type, Object... objs);

    void visit(Type.VoidType type, Object... objs);

    void visit(UnionType type, Object... objs);

    void visit(StructType type, Object... objs);

    void visit(NamedType type, Object... objs);

}

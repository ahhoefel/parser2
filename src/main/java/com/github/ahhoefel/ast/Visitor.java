package com.github.ahhoefel.ast;

import com.github.ahhoefel.ast.expression.*;

public interface Visitor {
    void visit(AndExpression expr);

    void visit(BooleanLiteralExpression expr);

    void visit(EqualExpression expr);

    void visit(FunctionInvocationExpression expr);

    void visit(IntegerLiteralExpression expr);

    void visit(LessThanExpression expr);

    void visit(LessThanOrEqualExpression expr);

    void visit(MemberAccessExpression expr);

    void visit(NotExpression expr);

    void visit(OrExpression expr);

    void visit(ProductExpression expr);

    void visit(StructLiteralExpression expr);

    void visit(SubtractExpression expr);

    void visit(SumExpression expr);

    void visit(UnaryMinusExpression expr);

    void visit(VariableExpression expr);

    void visit(AssignmentStatement stmt);

    void visit(Block block);

    void visit(ExpressionStatement stmt);

    void visit(ForStatement stmt);

    void visit(FunctionDeclaration fn);

    void visit(IfStatement stmt);

    void visit(Import stmt);

    void visit(ImportCatalog imports);

    void visit(LValue stmt);

    void visit(ReturnStatement stmt);

    void visit(TypeDeclaration decl);

    void visit(VariableDeclaration decl);

    void visit(File file);
}

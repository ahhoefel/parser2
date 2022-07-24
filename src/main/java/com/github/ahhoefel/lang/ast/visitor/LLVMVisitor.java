package com.github.ahhoefel.lang.ast.visitor;

import java.util.Optional;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;

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
import com.github.ahhoefel.lang.ast.symbols.Context;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols;
import com.github.ahhoefel.lang.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols;
import com.github.ahhoefel.lang.ast.symbols.FileSymbols.FunctionDefinition;
import com.github.ahhoefel.lang.ast.symbols.LocalSymbols.LocalSymbol;
import com.github.ahhoefel.lang.ast.type.NamedType;
import com.github.ahhoefel.lang.ast.type.StructType;
import com.github.ahhoefel.lang.ast.type.Type;
import com.github.ahhoefel.lang.ast.type.UnionType;
import com.github.ahhoefel.lang.ast.type.Type.BooleanType;
import com.github.ahhoefel.lang.ast.type.Type.IntType;
import com.github.ahhoefel.lang.ast.type.Type.StringType;
import com.github.ahhoefel.lang.ast.type.Type.VoidType;

public class LLVMVisitor implements Visitor {

    private enum ExpressionType {
        LVALUE, RVALUE
    }

    public static class Value<T> {
        public T value;

        public Value() {
        }

        public Value(T value) {
            this.value = value;
        }
    }

    public String error;
    private GlobalSymbols globals;

    public LLVMVisitor(GlobalSymbols globals) {
        this.globals = globals;
    }

    @Override
    public void visit(File file, Object... objs) {
        // BytePointer error = new BytePointer(); // Used to retrieve messages from
        // functions
        LLVM.LLVMLinkInMCJIT();
        LLVM.LLVMInitializeNativeAsmPrinter();
        LLVM.LLVMInitializeNativeAsmParser();
        LLVM.LLVMInitializeNativeDisassembler();
        LLVM.LLVMInitializeNativeTarget();
        Optional<FunctionDeclaration> main = file.getFunction("main");
        if (!main.isPresent()) {
            this.error = "No main function";
        }
        Value<LLVMValueRef> fnRef = new Value<>();
        FileSymbols fileSymbols = globals.get(file.getTarget());
        Context context = new Context(globals, fileSymbols);
        try {
            main.get().accept(this, context, fnRef);
        } catch (RuntimeException e) {
            throw new RuntimeException("File " + file.getTarget(), e);
        }
        @SuppressWarnings("unchecked")
        Value<LLVMModuleRef> out = (Value<LLVMModuleRef>) objs[0];
        LLVM.LLVMDumpValue(fnRef.value);
        // BytePointer b = LLVM.LLVMPrintValueToString(fnRef.value);
        // System.out.println("String:" + b.getString());
        BytePointer b = LLVM.LLVMPrintModuleToString(context.getFileModule());
        System.out.println("String: " + b.getString());

        out.value = context.getFileModule();
    }

    @Override
    public void visit(FunctionDeclaration fn, Object... objs) {
        Context context = (Context) objs[0];
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> fnRefValue = (Value<LLVMValueRef>) objs[1];

        // Return type
        Value<LLVMTypeRef> returnType = new Value<>();
        fn.getReturnType().accept(this, returnType);
        LLVMValueRef fnRef = LLVM.LLVMAddFunction(context.getFileModule(), fn.getName(),
                LLVM.LLVMFunctionType(returnType.value, LLVM.LLVMVoidType(), 0, 0));
        fnRefValue.value = fnRef;

        // Block setup
        LLVM.LLVMSetFunctionCallConv(fnRef, LLVM.LLVMCCallConv);
        LLVMBasicBlockRef block = LLVM.LLVMAppendBasicBlock(fnRef, "start");
        LLVMBuilderRef builder = LLVM.LLVMCreateBuilder();
        LLVM.LLVMPositionBuilderAtEnd(builder, block);

        // Params
        for (VariableDeclaration v : fn.getParameters()) {
            v.accept(this, context, builder);
        }

        // Block
        Value<Boolean> selfTerminates = new Value<>(false);
        fn.getBlock().accept(this, fnRef, block, builder, context, selfTerminates);
        if (selfTerminates.value && (fn.getReturnType() == Type.VOID)) {
            throw new RuntimeException(
                    "Function with void return type should not end in return statement: " +
                            fn.getName());
        }
        if (!selfTerminates.value && (fn.getReturnType() != Type.VOID)) {
            throw new RuntimeException("Non-void function needs return statement: " + fn.getName());
        }
    }

    @Override
    public void visit(Block block, Object... objs) {
        LLVMValueRef fn = (LLVMValueRef) objs[0];
        LLVMBasicBlockRef blockRef = (LLVMBasicBlockRef) objs[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[2];
        Context context = (Context) objs[3];
        @SuppressWarnings("unchecked")
        Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        for (Visitable statement : block.getStatements()) {
            statement.accept(this, fn, blockRef, builder, context, selfTerminates);
            System.out.println("Self terminates: " + selfTerminates.value);
        }
    }

    @Override
    public void visit(AssignmentStatement stmt, Object... objs) {
        // LLVMValueRef fn = (LLVMValueRef) objs[0];
        // LLVMBasicBlockRef block = (LLVMBasicBlockRef) objs[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[2];
        Context context = (Context) objs[3];
        @SuppressWarnings("unchecked")
        Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        selfTerminates.value = false;
        LLVMValueRef ref;
        if (stmt.getLValue().isPresent()) {
            Value<LLVMValueRef> refVal = new Value<>();
            stmt.getLValue().get().getExpression().accept(this, refVal, builder, context, ExpressionType.LVALUE);
            ref = refVal.value;
        } else if (stmt.getVariableDeclaration().isPresent()) {
            ref = LLVM.LLVMBuildAlloca(builder, LLVM.LLVMInt64Type(),
                    stmt.getVariableDeclaration().get().getName());
            context.getLocals().put(stmt.getVariableDeclaration().get(), LLVM.LLVMInt64Type(), ref);
        } else {
            throw new RuntimeException("Assignment statement should have either an lvalue or variable declaration");
        }

        Expression expression = stmt.getExpression();
        Value<LLVMValueRef> val = new Value<>();
        expression.accept(this, val, builder, context, ExpressionType.RVALUE);
        LLVM.LLVMBuildStore(builder, val.value, ref);
    }

    @Override
    public void visit(IfStatement stmt, Object... objs) {
        LLVMValueRef fn = (LLVMValueRef) objs[0];
        // LLVMBasicBlockRef block = (LLVMBasicBlockRef) objs[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[2];
        Context context = (Context) objs[3];
        @SuppressWarnings("unchecked")
        Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        selfTerminates.value = false;

        Context innerContext = new Context(context);
        LLVMBasicBlockRef ifBlock = LLVM.LLVMAppendBasicBlock(fn, "ifBlock");
        LLVMBasicBlockRef nextBlock = LLVM.LLVMAppendBasicBlock(fn, "nextBlock");
        Value<LLVMValueRef> condition = new Value<>();
        stmt.getCondition().accept(this, condition, builder, context, ExpressionType.RVALUE);
        LLVM.LLVMBuildCondBr(builder, condition.value, ifBlock, nextBlock);
        LLVM.LLVMPositionBuilderAtEnd(builder, ifBlock);

        Value<Boolean> blockSelfTerminates = new Value<>();
        stmt.getBlock().accept(this, fn, ifBlock, builder, innerContext, blockSelfTerminates);
        if (!blockSelfTerminates.value) {
            LLVM.LLVMBuildBr(builder, nextBlock);
        }
        LLVM.LLVMPositionBuilderAtEnd(builder, nextBlock);
    }

    @Override
    public void visit(BooleanLiteralExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        boolean value = expr.getValue();
        ref.value = LLVM.LLVMConstInt(LLVM.LLVMInt1Type(), value ? 1 : 0, 0);
    }

    @Override
    public void visit(IntegerLiteralExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        ref.value = LLVM.LLVMConstInt(LLVM.LLVMInt64Type(), expr.getValue(), 0);
    }

    @Override
    public void visit(SumExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        ExpressionType type = (ExpressionType) objs[3];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, type);
        expr.getRight().accept(this, right, builder, context, type);
        ref.value = LLVM.LLVMBuildAdd(builder, left.value, right.value, "sum");
    }

    @Override
    public void visit(AndExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context);
        expr.getRight().accept(this, right, builder, context);
        ref.value = LLVM.LLVMBuildAnd(builder, left.value, right.value, "and");
    }

    @Override
    public void visit(EqualExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, context, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntEQ, left.value, right.value, "eq");
    }

    @Override
    public void visit(LessThanExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, context, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntSLT, left.value, right.value, "signedLessThan");

    }

    @Override
    public void visit(LessThanOrEqualExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, context, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntSLE, left.value, right.value, "signedLessThanEqual");
    }

    @Override
    public void visit(NotExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> value = new Value<>();
        expr.getExpression().accept(this, value, builder, context, ExpressionType.RVALUE);
        LLVMValueRef one = LLVM.LLVMConstInt(LLVM.LLVMInt1Type(), 1, 0);
        ref.value = LLVM.LLVMBuildXor(builder, value.value, one, "not");
    }

    @Override
    public void visit(OrExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, context, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildOr(builder, left.value, right.value, "or");
    }

    @Override
    public void visit(ProductExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, context, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildMul(builder, left.value, right.value, "mult");
    }

    @Override
    public void visit(SubtractExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, context, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildSub(builder, left.value, right.value, "sub");
    }

    @Override
    public void visit(VariableExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        ExpressionType type = (ExpressionType) objs[3];
        Optional<LocalSymbols.LocalSymbol> symbol = context.getLocals().get(expr.getIdentifier());
        if (!symbol.isPresent()) {
            throw new RuntimeException("Unknown variable: " + expr.getIdentifier());
        }
        if (type == ExpressionType.LVALUE) {
            ref.value = symbol.get().value;
        } else {
            ref.value = LLVM.LLVMBuildLoad(builder, symbol.get().value, expr.getIdentifier());
        }
    }

    @Override
    public void visit(FunctionInvocationExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0]; // returned value
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        LLVMValueRef[] args = new LLVMValueRef[expr.getArgs().size()];
        for (int i = 0; i < expr.getArgs().size(); i++) {
            Value<LLVMValueRef> argValue = new Value<>();
            expr.getArgs().get(i).accept(this, argValue, builder, context, ExpressionType.RVALUE);
            args[i] = argValue.value;
        }
        Optional<LocalSymbol> fn = context.getLocals().get(expr.getIdentifier());
        LLVMValueRef fnRef;
        if (fn.isPresent()) {
            fnRef = fn.get().value;
        } else {
            Optional<FunctionDefinition> fnDef = context.getFileSymbols().getFunction(expr.getIdentifier());
            if (!fnDef.isPresent()) {
                throw new RuntimeException("Unknown function: " + expr.getIdentifier());
            }
            Value<LLVMValueRef> fnRefValue = new Value<>();
            fnDef.get().getDeclaration().accept(this,
                    new Context(context.getGlobalSymbols(), context.getFileSymbols()),
                    fnRefValue);
            fnRef = fnRefValue.value;
        }
        ref.value = LLVM.LLVMBuildCall(builder, fnRef, new PointerPointer<>(args), args.length, "fnReturnValueName");
    }

    @Override
    public void visit(MemberAccessExpression expr, Object... objs) {

    }

    @Override
    public void visit(StructLiteralExpression expr, Object... objs) {

    }

    @Override
    public void visit(UnaryMinusExpression expr, Object... objs) {

    }

    @Override
    public void visit(ExpressionStatement stmt, Object... objs) {

    }

    @Override
    public void visit(ForStatement stmt, Object... objs) {

    }

    @Override
    public void visit(Import stmt, Object... objs) {

    }

    @Override
    public void visit(ImportCatalog imports, Object... objs) {

    }

    @Override
    public void visit(LValue stmt, Object... objs) {

    }

    @Override
    public void visit(ReturnStatement stmt, Object... objs) {
        // LLVMValueRef fn = (LLVMValueRef) objs[0];
        // LLVMBasicBlockRef block = (LLVMBasicBlockRef) objs[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[2];
        Context context = (Context) objs[3];
        @SuppressWarnings("unchecked")
        Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        selfTerminates.value = true;
        Expression expression = stmt.getExpression();
        Value<LLVMValueRef> val = new Value<>();
        expression.accept(this, val, builder, context, ExpressionType.RVALUE);
        LLVM.LLVMBuildRet(builder, val.value);
    }

    @Override
    public void visit(TypeDeclaration decl, Object... objs) {

    }

    @Override
    public void visit(VariableDeclaration decl, Object... objs) {
        Context context = (Context) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Type type = decl.getType();
        Value<LLVMTypeRef> typeRef = new Value<>();
        type.accept(this, typeRef);
        LLVMValueRef ref = LLVM.LLVMBuildAlloca(builder, LLVM.LLVMInt64Type(), decl.getName());
        context.getLocals().put(decl, typeRef.value, ref);

    }

    @Override
    public void visit(NotEqualExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        Context context = (Context) objs[2];
        // ExpressionType type = (ExpressionType) objs[3];

        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, context, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, context, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntNE, left.value, right.value, "neq");
    }

    @Override
    public void visit(ParenthesesExpression expr, Object... objs) {
        expr.getExpression().accept(this, objs);
    }

    @Override
    public void visit(IntType type, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMTypeRef> out = (Value<LLVMTypeRef>) objs[0];
        out.value = LLVM.LLVMInt64Type();
    }

    @Override
    public void visit(BooleanType type, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMTypeRef> out = (Value<LLVMTypeRef>) objs[0];
        out.value = LLVM.LLVMInt1Type();
    }

    @Override
    public void visit(StringType type, Object... objs) {

    }

    @Override
    public void visit(VoidType type, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMTypeRef> out = (Value<LLVMTypeRef>) objs[0];
        out.value = LLVM.LLVMVoidType();
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

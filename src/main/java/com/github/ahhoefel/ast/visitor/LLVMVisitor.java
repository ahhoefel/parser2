package com.github.ahhoefel.ast.visitor;

import java.util.Optional;

import com.github.ahhoefel.ast.Block;
import com.github.ahhoefel.ast.File;
import com.github.ahhoefel.ast.FunctionDeclaration;
import com.github.ahhoefel.ast.Import;
import com.github.ahhoefel.ast.ImportCatalog;
import com.github.ahhoefel.ast.LValue;
import com.github.ahhoefel.ast.TypeDeclaration;
import com.github.ahhoefel.ast.VariableDeclaration;
import com.github.ahhoefel.ast.Visitor;
import com.github.ahhoefel.ast.expression.Expression;
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
import com.github.ahhoefel.ast.symbols.GlobalSymbols;
import com.github.ahhoefel.ast.symbols.LocalSymbols;
import com.github.ahhoefel.ast.type.NamedType;
import com.github.ahhoefel.ast.type.StructType;
import com.github.ahhoefel.ast.type.Type;
import com.github.ahhoefel.ast.type.UnionType;
import com.github.ahhoefel.ast.type.Type.BooleanType;
import com.github.ahhoefel.ast.type.Type.IntType;
import com.github.ahhoefel.ast.type.Type.StringType;
import com.github.ahhoefel.ast.type.Type.VoidType;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;

public class LLVMVisitor implements Visitor {

    private enum ExpressionType {
        LVALUE, RVALUE
    }

    public static class Value<T> {
        public T value;
    }

    public String error;
    private GlobalSymbols symbols;

    public LLVMVisitor(GlobalSymbols symbols) {
        this.symbols = symbols;
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
        LLVMModuleRef module = LLVM.LLVMModuleCreateWithName("file");
        Optional<FunctionDeclaration> main = file.getFunction("main");
        if (!main.isPresent()) {
            this.error = "No main function";
        }
        Value<LLVMValueRef> fnRef = new Value<>();
        main.get().accept(this, module, fnRef);
        @SuppressWarnings("unchecked")
        Value<LLVMMemoryBufferRef> out = (Value<LLVMMemoryBufferRef>) objs[0];
        LLVM.LLVMDumpValue(fnRef.value);
        out.value = LLVM.LLVMWriteBitcodeToMemoryBuffer(module);
    }

    @Override
    public void visit(FunctionDeclaration fn, Object... objs) {
        LLVMModuleRef module = (LLVMModuleRef) objs[0];
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> fnRefValue = (Value<LLVMValueRef>) objs[1];
        Value<LLVMTypeRef> returnType = new Value<>();
        fn.getReturnType().accept(this, returnType);
        LLVMValueRef fnRef = LLVM.LLVMAddFunction(module, fn.getName(),
                LLVM.LLVMFunctionType(returnType.value, LLVM.LLVMVoidType(), 0, 0));
        fnRefValue.value = fnRef;
        LLVM.LLVMSetFunctionCallConv(fnRef, LLVM.LLVMCCallConv);
        LLVMBasicBlockRef block = LLVM.LLVMAppendBasicBlock(fnRef, "start");
        LLVMBuilderRef builder = LLVM.LLVMCreateBuilder();
        LLVM.LLVMPositionBuilderAtEnd(builder, block);
        LocalSymbols symbols = new LocalSymbols();
        Value<Boolean> selfTerminates = new Value<>();
        fn.getBlock().accept(this, fnRef, block, builder, symbols, selfTerminates);
        if (selfTerminates.value && (fn.getReturnType() == Type.VOID)) {
            throw new RuntimeException(
                    "Function with void return type should not end in return statement: " + fn.getName());
        }
        if (!selfTerminates.value && (fn.getReturnType() != Type.VOID)) {
            throw new RuntimeException("Non-void function needs return statement: " + fn.getName());
        }
    }

    @Override
    public void visit(Block block, Object... objs) {
        // LLVMValueRef fn = (LLVMValueRef) objs[0];
        // LLVMBasicBlockRef block = (LLVMBasicBlockRef) objs[1];
        // LLVMBuilderRef builder = (LLVMBuilderRef) objs[2];
        // LocalSymbols symbols = (LocalSymbols) objs[3];
        // @SuppressWarnings("unchecked")
        // Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        for (Statement statement : block.statements) {
            statement.accept(this, objs);
        }
    }

    @Override
    public void visit(AssignmentStatement stmt, Object... objs) {
        // LLVMValueRef fn = (LLVMValueRef) objs[0];
        // LLVMBasicBlockRef block = (LLVMBasicBlockRef) objs[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[2];
        LocalSymbols symbols = (LocalSymbols) objs[3];
        @SuppressWarnings("unchecked")
        Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        selfTerminates.value = false;
        LValue lValue = stmt.getLValue();
        LLVMValueRef ref;
        if (lValue.isDeclaration()) {
            ref = LLVM.LLVMBuildAlloca(builder, LLVM.LLVMInt64Type(), lValue.getIdentifier());
        } else {
            // Lookup ref.
            Value<LLVMValueRef> refVal = new Value<>();
            lValue.getExpression().accept(this, refVal, builder, symbols, ExpressionType.LVALUE);
            ref = refVal.value;
        }
        Expression expression = stmt.getExpression();
        Value<LLVMValueRef> val = new Value<>();
        expression.accept(this, val, builder, symbols, ExpressionType.RVALUE);
        if (lValue.isDeclaration()) {
            symbols.put(stmt.getLValue(), LLVM.LLVMInt64Type(), ref);
        }
        LLVM.LLVMBuildStore(builder, val.value, ref);
    }

    @Override
    public void visit(IfStatement stmt, Object... objs) {
        LLVMValueRef fn = (LLVMValueRef) objs[0];
        // LLVMBasicBlockRef block = (LLVMBasicBlockRef) objs[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[2];
        LocalSymbols symbols = (LocalSymbols) objs[3];
        @SuppressWarnings("unchecked")
        Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        selfTerminates.value = false;

        LocalSymbols innerSymbols = new LocalSymbols(symbols);
        LLVMBasicBlockRef ifBlock = LLVM.LLVMAppendBasicBlock(fn, "ifBlock");
        LLVMBasicBlockRef nextBlock = LLVM.LLVMAppendBasicBlock(fn, "nextBlock");
        Value<LLVMValueRef> condition = new Value<>();
        stmt.getCondition().accept(this, condition, builder, symbols, ExpressionType.RVALUE);
        LLVM.LLVMBuildCondBr(builder, condition.value, ifBlock, nextBlock);
        LLVM.LLVMPositionBuilderAtEnd(builder, ifBlock);

        Value<Boolean> blockSelfTerminates = new Value<>();
        stmt.getBlock().accept(this, fn, ifBlock, builder, innerSymbols, blockSelfTerminates);
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
        LocalSymbols symbols = (LocalSymbols) objs[2];
        ExpressionType type = (ExpressionType) objs[3];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, type);
        expr.getRight().accept(this, right, builder, symbols, type);
        ref.value = LLVM.LLVMBuildAdd(builder, left.value, right.value, "sum");
    }

    @Override
    public void visit(AndExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols);
        expr.getRight().accept(this, right, builder, symbols);
        ref.value = LLVM.LLVMBuildAnd(builder, left.value, right.value, "and");
    }

    @Override
    public void visit(EqualExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, symbols, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntEQ, left.value, right.value, "eq");
    }

    @Override
    public void visit(LessThanExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, symbols, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntSLT, left.value, right.value, "signedLessThan");

    }

    @Override
    public void visit(LessThanOrEqualExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, symbols, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntSLE, left.value, right.value, "signedLessThanEqual");
    }

    @Override
    public void visit(NotExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> value = new Value<>();
        expr.getExpression().accept(this, value, builder, symbols, ExpressionType.RVALUE);
        LLVMValueRef one = LLVM.LLVMConstInt(LLVM.LLVMInt1Type(), 1, 0);
        ref.value = LLVM.LLVMBuildXor(builder, value.value, one, "not");
    }

    @Override
    public void visit(OrExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, symbols, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildOr(builder, left.value, right.value, "or");
    }

    @Override
    public void visit(ProductExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, symbols, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildMul(builder, left.value, right.value, "mult");
    }

    @Override
    public void visit(SubtractExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, symbols, ExpressionType.RVALUE);
        ref.value = LLVM.LLVMBuildSub(builder, left.value, right.value, "sub");
    }

    @Override
    public void visit(VariableExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        ExpressionType type = (ExpressionType) objs[3];
        Optional<LocalSymbols.LocalSymbol> symbol = symbols.get(expr.getIdentifier());
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
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        LLVMValueRef[] args = new LLVMValueRef[expr.getArgs().size()];
        for (int i = 0; i < expr.getArgs().size(); i++) {
            Value<LLVMValueRef> argValue = new Value<>();
            expr.getArgs().get(i).accept(this, argValue, builder, symbols, ExpressionType.RVALUE);
            args[i] = argValue.value;
        }
        ref.value = LLVM.LLVMBuildCall(builder, null, new PointerPointer<>(args), 1, "fac(n - 1)");
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
        LocalSymbols symbols = (LocalSymbols) objs[3];
        @SuppressWarnings("unchecked")
        Value<Boolean> selfTerminates = (Value<Boolean>) objs[4];
        selfTerminates.value = true;
        Expression expression = stmt.getExpression();
        Value<LLVMValueRef> val = new Value<>();
        expression.accept(this, val, builder, symbols, ExpressionType.RVALUE);
        LLVM.LLVMBuildRet(builder, val.value);
    }

    @Override
    public void visit(TypeDeclaration decl, Object... objs) {

    }

    @Override
    public void visit(VariableDeclaration decl, Object... objs) {

    }

    @Override
    public void visit(NotEqualExpression expr, Object... objs) {
        @SuppressWarnings("unchecked")
        Value<LLVMValueRef> ref = (Value<LLVMValueRef>) objs[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) objs[1];
        LocalSymbols symbols = (LocalSymbols) objs[2];
        // ExpressionType type = (ExpressionType) objs[3];

        Value<LLVMValueRef> left = new Value<>();
        Value<LLVMValueRef> right = new Value<>();
        expr.getLeft().accept(this, left, builder, symbols, ExpressionType.RVALUE);
        expr.getRight().accept(this, right, builder, symbols, ExpressionType.RVALUE);
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

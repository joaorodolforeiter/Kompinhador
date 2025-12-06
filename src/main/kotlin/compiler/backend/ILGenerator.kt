package compiler.backend

import compiler.common.SymbolType

class ILGenerator {
    private val code = StringBuilder()
    private var labelCounter = 0

    fun generateProgramHeader() {
        code.appendLine(".assembly extern mscorlib {}")
        code.appendLine(".assembly program {}")
        code.appendLine(".module program.exe")
        code.appendLine("")
        code.appendLine(".class public Program extends [mscorlib]System.Object {")
        code.appendLine("  .method public static void Main() cil managed {")
        code.appendLine("    .entrypoint")
        code.appendLine("    .maxstack 100")
    }

    fun generateProgramFooter() {
        emit("ret")
        code.appendLine("  }")
        code.appendLine("}")
    }

    fun declareLocal(name: String, type: String) {
        emit(".locals ($type $name)")
    }

    fun loadIntegerConstant(value: String) {
        emit("ldc.i8 $value")
        emit("conv.r8")
    }

    fun loadFloatConstant(value: String) {
        emit("ldc.r8 $value")
    }

    fun loadStringConstant(value: String) {
        emit("ldstr $value")
    }

    fun loadTrue() {
        emit("ldc.i4.1")
    }

    fun loadFalse() {
        emit("ldc.i4.0")
    }

    fun loadVariable(name: String) {
        emit("ldloc $name")
    }

    fun storeVariable(name: String) {
        emit("stloc $name")
    }

    fun convertInt64ToFloat64() {
        emit("conv.r8")
    }

    fun convertToInt64() {
        emit("conv.i8")
    }

    fun add() {
        emit("add")
    }

    fun subtract() {
        emit("sub")
    }

    fun multiply() {
        emit("mul")
    }

    fun divide() {
        emit("div")
    }

    fun negate() {
        emit("neg")
    }

    fun compareEqual() {
        emit("ceq")
    }

    fun compareNotEqual() {
        emit("ceq")
        emit("ldc.i4.0")
        emit("ceq")
    }

    fun compareLessThan() {
        emit("clt")
    }

    fun compareGreaterThan() {
        emit("cgt")
    }

    fun logicalAnd(line: Int) {
        emit("and")
    }

    fun logicalOr(line: Int) {
        emit("or")
    }

    fun logicalNot() {
        emit("ldc.i4.0")
        emit("ceq")
    }

    fun writeValue(type: SymbolType) {
        emit("call void [mscorlib]System.Console::Write($type)")
    }

    fun writeNewLine() {
        emit("ldstr \"\\n\"")
        emit("call void [mscorlib]System.Console::Write(string)")
    }

    fun readLine() {
        emit("call string [mscorlib]System.Console::ReadLine()")
    }

    fun parseInt64() {
        emit("call int64 [mscorlib]System.Int64::Parse(string)")
    }

    fun parseFloat64() {
        emit("call float64 [mscorlib]System.Double::Parse(string)")
    }

    fun createLabel(): String {
        return "L${labelCounter++}"
    }

    fun emitLabel(label: String) {
        code.appendLine("  $label:")
    }

    fun branch(label: String) {
        emit("br $label")
    }

    fun branchIfFalse(label: String) {
        emit("brfalse $label")
    }

    fun branchIfTrue(label: String) {
        emit("brtrue $label")
    }

    fun addComment(comment: String) {
        emit("// $comment")
    }

    // convertFloat64ToInt64
    fun convertFloat64ToInt64() {
        emit("conv.i8")
    }

    fun getCode(): String = code.toString()

    /**
     * Emite uma instrução com indentação
     */
    private fun emit(instruction: String) {
        code.appendLine("    $instruction")
    }
}
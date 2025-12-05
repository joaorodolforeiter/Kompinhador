package compiler.backend.visitors

import ExprParserBaseListener
import compiler.backend.ILGenerator
import compiler.backend.SemanticRegisters
import compiler.common.SymbolTable
import compiler.common.SymbolType
import compiler.exceptions.SemanticException

class ExprSemanticListener : ExprParserBaseListener() {

    private val ilGen = ILGenerator()
    private val reg = SemanticRegisters()
    private val symbolTable = SymbolTable()
    private var type: SymbolType? = null

    /**
     * AÇÃO #100: Gera o cabeçalho do programa IL
     */
    override fun enterProgram(ctx: ExprParser.ProgramContext) {
        ilGen.generateProgramHeader()
    }

    /**
     * AÇÃO #101: Finaliza o programa IL
     */
    override fun exitProgram(ctx: ExprParser.ProgramContext?) {
        ilGen.generateProgramFooter()
    }

    /**
     * AÇÃO #102: Escreve um valor na saída padrão
     */
    fun writeValue() {
        val type = reg.typeStack.pop()

        ilGen.writeValue(type)
    }

    override fun exitFator(ctx: ExprParser.FatorContext) {
        when {
            ctx.CINT() != null -> loadIntegerConstant(ctx.CINT().text)
            ctx.CFLOAT() != null -> loadFloatConstant(ctx.CFLOAT().text)
            ctx.STRING() != null -> loadStringConstant(ctx.STRING().text)
            ctx.MINUS() != null -> performUnaryNegation()
            ctx.IDENTIFICADOR() != null -> {
                //???
            }
        }
    }

    override fun exitAritmetica_(ctx: ExprParser.Aritmetica_Context) {
        when {
            ctx.PLUS() != null -> performAddition()
            ctx.MINUS() != null -> performSubtraction()
        }
    }

    override fun exitValor(ctx: ExprParser.ValorContext) {
        when {
            ctx.PR_TRUE() != null -> loadTrueConstant()
            ctx.PR_FALSE() != null -> loadFalseConstant()
            ctx.PR_NOT() != null -> performLogicalNot()
        }
    }

    override fun exitExpression_(ctx: ExprParser.Expression_Context) {
        when {
            ctx.PR_AND() != null -> performLogicalAnd()
            ctx.PR_OR() != null -> performLogicalOr()
        }
    }

    override fun exitExpressions(ctx: ExprParser.ExpressionsContext) {
        writeValue()
    }

    /**
     * AÇÃO #120: Armazenar tipo
     */
    override fun enterPrimitive(ctx: ExprParser.PrimitiveContext) {
        type = when (ctx.start.text) {
            "int" -> SymbolType.INT
            "float" -> SymbolType.FLOAT
            "string" -> SymbolType.STRING
            "bool" -> SymbolType.BOOL
            else -> throw SemanticException("Tipo desconhecido: $type")
        }

        reg.type = type
    }

    /**
     * AÇÃO #119: Declarar variáveis
     */
    override fun enterIdentifiers(ctx: ExprParser.IdentifiersContext) {
        val id = ctx.start.text
        declareVariable(id, ctx.start.line)
    }

    /**
     * AÇÃO #103: Carrega constante inteira
     */
    fun loadIntegerConstant(lexeme: String) {
        reg.typeStack.push(SymbolType.INT)
        ilGen.loadIntegerConstant(lexeme)
    }

    /**
     * AÇÃO #104: Carrega constante float
     */
    fun loadFloatConstant(lexeme: String) {
        reg.typeStack.push(SymbolType.FLOAT)
        ilGen.loadFloatConstant(lexeme)
    }

    /**
     * AÇÃO #105: Carrega constante string
     */
    fun loadStringConstant(lexeme: String) {
        reg.typeStack.push(SymbolType.STRING)
        ilGen.loadStringConstant(lexeme)
    }

    override fun exitTermo_(ctx: ExprParser.Termo_Context) {
        when {
            ctx.TIMES() != null -> performMultiplication()
            ctx.DIV() != null -> performDivision()
        }
    }

    /**
     * AÇÃO #106: Operador binário de adição
     */
    fun performAddition() {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        val resultType = determineArithmeticResultType(type1, type2)

        reg.typeStack.push(resultType)
        ilGen.add()
    }

    /**
     * AÇÃO #107: Operador binário de subtração
     */
    fun performSubtraction() {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        val resultType = determineArithmeticResultType(type1, type2)

        reg.typeStack.push(resultType)
        ilGen.subtract()
    }

    /**
     * AÇÃO #108: Operador binário de multiplicação
     */
    fun performMultiplication() {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        val resultType = determineArithmeticResultType(type1, type2)

        reg.typeStack.push(resultType)
        ilGen.multiply()
    }

    /**
     * AÇÃO #109: Operador binário de divisão
     */
    fun performDivision() {
        reg.typeStack.pop()
        reg.typeStack.pop()

        reg.typeStack.push(SymbolType.FLOAT)
        ilGen.divide()
    }

    /**
     * AÇÃO #110: Operador unário de negação
     */
    fun performUnaryNegation() {
        ilGen.negate()
    }

    /**
     * AÇÃO #111: Armazena operador relacional
     */
    fun storeRelationalOperator(operator: String) {
        reg.relationalOperator = operator
    }

    /**
     * AÇÃO #112: Operação relacional
     */
    fun performRelationalOperation() {
        reg.typeStack.pop()
        reg.typeStack.pop()

        reg.typeStack.push(SymbolType.BOOL)

        when (reg.relationalOperator) {
            "==" -> ilGen.compareEqual()
            "!=" -> ilGen.compareNotEqual()
            "<" -> ilGen.compareLessThan()
            ">" -> ilGen.compareGreaterThan()
        }
    }

    /**
     * AÇÃO #113: Operador lógico AND
     */
    fun performLogicalAnd() {
        reg.typeStack.pop()
        reg.typeStack.pop()

        reg.typeStack.push(SymbolType.BOOL)
        ilGen.logicalAnd()
    }

    /**
     * AÇÃO #114: Operador lógico OR
     */
    fun performLogicalOr() {
        reg.typeStack.pop()
        reg.typeStack.pop()

        reg.typeStack.push(SymbolType.BOOL)
        ilGen.logicalOr()
    }

    /**
     * AÇÃO #115: Constante TRUE
     */
    fun loadTrueConstant() {
        reg.typeStack.push(SymbolType.BOOL)
        ilGen.loadTrue()
    }

    /**
     * AÇÃO #116: Constante FALSE
     */
    fun loadFalseConstant() {
        reg.typeStack.push(SymbolType.BOOL)
        ilGen.loadFalse()
    }

    /**
     * AÇÃO #117: Operador lógico NOT
     */
    fun performLogicalNot() {
        ilGen.logicalNot()
    }

    /**
     * AÇÃO #118: Quebra de linha
     */
    fun writeNewLine() {
        ilGen.writeNewLine()
    }

    /**
     * AÇÃO #119: Declarar variável
     */
    fun declareVariable(id: String, line: Int) {
        val added = symbolTable.declare(id, type!!, line)

        if (!added) {
            throw SemanticException("linha $line: variável '$id' já declarada.")
        }

        ilGen.declareLocal(id, type.toString())
    }

    /**
     * AÇÃO #121: Armazenar identificador
     */
    fun storeIdentifier(id: String) {
        reg.identifierList.add(id)
    }

    /**
     * AÇÃO #122: Atribuição
     */
    fun performAssigment(line: Int) {
        val exprType = reg.typeStack.pop()

        if (exprType == SymbolType.INT) {
            ilGen.convertInt64ToFloat64()
        }

        val id = reg.identifierList.last()

        symbolTable.lookup(id)
            ?: throw SemanticException("linha $line: variável '$id' não declarada.")

        ilGen.storeVariable(id)

        symbolTable.markInitialized(id)
        reg.clearIdentifierList()
    }

    /**
     * AÇÃO #123: Comando READ
     */
    fun performRead(id: String, line: Int) {
        val symbol = symbolTable.lookup(id)
            ?: throw SemanticException("linha $line: variável '$id' não declarada.")

        if (symbol.type == SymbolType.BOOL) {
            throw SemanticException("linha $line: variável '$id' do tipo 'bool' não pode ser lida.")
        }

        ilGen.readLine()

        when (symbol.type) {
            SymbolType.INT -> ilGen.parseInt64()
            SymbolType.FLOAT -> ilGen.parseFloat64()
            SymbolType.STRING -> { /* já está em string */
            }

            else -> { /* nada deve ocorrer */
            }
        }

        ilGen.storeVariable(id)
        symbolTable.markInitialized(id)
    }

    override fun enterOpt_string(ctx: ExprParser.Opt_stringContext) {
        writePromptString(ctx.start.text)
        performRead(ctx.start.text, ctx.start.line)
    }

    /**
     * AÇÃO #124: String opcional no READ
     */
    fun writePromptString(cteString: String) {
        ilGen.loadStringConstant(cteString)
        ilGen.writeValue(SymbolType.STRING)
    }


    /**
     * Determina o tipo resultante de uma operação aritmética
     */
    private fun determineArithmeticResultType(type1: SymbolType, type2: SymbolType): SymbolType {
        return when {
            type1 == SymbolType.INT && type2 == SymbolType.INT -> SymbolType.INT
            type1 == SymbolType.FLOAT || type2 == SymbolType.FLOAT -> SymbolType.FLOAT
            else -> SymbolType.FLOAT
        }
    }

    fun getCode(): String {
        return ilGen.getCode()
    }
}
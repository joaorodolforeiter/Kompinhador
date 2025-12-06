package compiler.backend.listeners

import ExprParser
import ExprParserBaseListener
import compiler.backend.ILGenerator
import compiler.backend.SemanticRegisters
import compiler.common.SymbolTable
import compiler.common.SymbolType
import compiler.exceptions.SemanticException
import org.antlr.v4.runtime.tree.TerminalNodeImpl

class ExprSemanticListener : ExprParserBaseListener() {

    private val ilGen = ILGenerator()
    private val reg = SemanticRegisters()
    private val symbolTable = SymbolTable()
    private var type: SymbolType? = null
    private val processedAssignments = mutableSetOf<ExprParser.Assignment_valueContext>()

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
        var type = reg.typeStack.pop()

        if (type == SymbolType.INT) {
            ilGen.convertInt64ToFloat64()
            type = SymbolType.FLOAT
        }

        ilGen.writeValue(type)
    }

    override fun exitFator(ctx: ExprParser.FatorContext) {
        when {
            ctx.CINT() != null -> loadIntegerConstant(ctx.CINT().text)
            ctx.CFLOAT() != null -> loadFloatConstant(ctx.CFLOAT().text)
            ctx.STRING() != null -> loadStringConstant(ctx.STRING().text)
            ctx.MINUS() != null -> performUnaryNegation(ctx.start.line)
            ctx.PLUS() != null -> performUnaryPlus(ctx.start.line)
            ctx.IDENTIFICADOR() != null -> {

                val id = ctx.IDENTIFICADOR().text
                val symbol = symbolTable.lookup(id)
                    ?: throw SemanticException("linha ${ctx.start.line}: variável '$id' não declarada.")


                reg.typeStack.push(symbol.type)


                ilGen.loadVariable(id)


                if (symbol.type == SymbolType.INT) {
                    ilGen.convertInt64ToFloat64()
                }
            }
        }
    }

    private fun performUnaryPlus(line: Int) {
        val type = reg.typeStack.pop()
        if (type != SymbolType.INT && type != SymbolType.FLOAT) {
            throw SemanticException("linha ${line}: operador unário '+' aplicado a tipo inválido.")
        }
        reg.typeStack.push(type)
    }

    /**
     * AÇÃO #106/#107: Detecta operador de adição/subtração na lista de aritmética
     */
    override fun exitAritmetica_(ctx: ExprParser.Aritmetica_Context) {
        when {
            ctx.PLUS() != null -> performAddition(ctx.start.line)
            ctx.MINUS() != null -> performSubtraction(ctx.start.line)
        }
    }

    /**
     * AÇÃO #115/#116/#117: Valores lógicos e NOT
     */
    override fun exitValor(ctx: ExprParser.ValorContext) {
        when {
            ctx.PR_TRUE() != null -> loadTrueConstant()
            ctx.PR_FALSE() != null -> loadFalseConstant()
            ctx.PR_NOT() != null -> {
                val type = reg.typeStack.pop()

                if (type != SymbolType.BOOL) {
                    throw SemanticException("linha ${ctx.start.line}: operador lógico NOT aplicado a tipo inválido.")
                }

                reg.typeStack.push(type)

                performLogicalNot()
            }
        }
    }

    /**
     * AÇÃO #113/#114: Operadores lógicos binários (AND/OR)
     */
    override fun exitExpression_(ctx: ExprParser.Expression_Context) {
        when {
            ctx.PR_AND() != null -> logicalAnd(ctx)
            ctx.PR_OR() != null -> logicalOr(ctx)
        }
    }

    private fun logicalAnd(ctx: ExprParser.Expression_Context) {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        if (type1 != SymbolType.BOOL || type2 != SymbolType.BOOL) {
            throw SemanticException("linha ${ctx.start.line}: tipos incompatíveis para operação lógica.")
        }

        ilGen.logicalAnd(ctx.start.line)
    }

    private fun logicalOr(ctx: ExprParser.Expression_Context) {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        if (type1 != SymbolType.BOOL || type2 != SymbolType.BOOL) {
            throw SemanticException("linha ${ctx.start.line}: tipos incompatíveis para operação lógica.")
        }
        ilGen.logicalOr(ctx.start.line)
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

    /**
     * AÇÃO #108/#109: Operadores de termo (*, /)
     */
    override fun exitTermo_(ctx: ExprParser.Termo_Context) {
        when {
            ctx.TIMES() != null -> performMultiplication(ctx.start.line)
            ctx.DIV() != null -> performDivision(ctx.start.line)
        }
    }

    /**
     * AÇÃO #106: Operador binário de adição
     */
    fun performAddition(line: Int) {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        val resultType = determineArithmeticResultType(type1, type2, line)

        reg.typeStack.push(resultType)
        ilGen.add()
    }

    /**
     * AÇÃO #107: Operador binário de subtração
     */
    fun performSubtraction(line: Int) {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        val resultType = determineArithmeticResultType(type1, type2, line)

        reg.typeStack.push(resultType)
        ilGen.subtract()
    }

    /**
     * AÇÃO #108: Operador binário de multiplicação
     */
    fun performMultiplication(line: Int) {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        val resultType = determineArithmeticResultType(type1, type2, line)

        reg.typeStack.push(resultType)
        ilGen.multiply()
    }

    /**
     * AÇÃO #109: Operador binário de divisão
     */
    fun performDivision(line: Int) {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        if (type1 in listOf(SymbolType.INT, SymbolType.FLOAT) && type2 in listOf(SymbolType.INT, SymbolType.FLOAT)) {
            throw SemanticException("linha $line: tipos incompatíveis para operação de divisão.")
        }

        reg.typeStack.push(SymbolType.FLOAT)
        ilGen.divide()
    }

    /**
     * AÇÃO #110: Operador unário de negação
     */
    fun performUnaryNegation(line: Int) {
        type = reg.typeStack.pop()
        
        if (type != SymbolType.INT && type != SymbolType.FLOAT) {
            throw SemanticException("linha $line: operador unário '-' aplicado a tipo inválido.")
        }
        
        ilGen.negate()
    }

    /**
     * AÇÃO #111: Armazenar operador relacional
     */
    fun storeRelationalOperator(operator: String) {
        reg.relationalOperator = operator
    }

    /**
     * AÇÃO #112: Operação relacional
     */
    fun performRelationalOperation(line: Int) {
        val type1 = reg.typeStack.pop()
        val type2 = reg.typeStack.pop()

        if ((type1 !in listOf(SymbolType.INT, SymbolType.FLOAT, SymbolType.STRING)) ||
            (type2 !in listOf(SymbolType.INT, SymbolType.FLOAT, SymbolType.STRING))
        ) {
            throw SemanticException("linha $line: tipos incompatíveis para operação relacional.")
        }

        if (reg.relationalOperator in listOf("<", ">")) {
            if (type1 == SymbolType.STRING || type2 == SymbolType.STRING) {
                throw SemanticException("linha $line: tipos incompatíveis para operação relacional.")
            }
        }

        if (SymbolType.STRING in listOf(type1, type2) && type1 != type2) {
            throw SemanticException("linha $line: tipos incompatíveis para operação relacional.")
        }

        reg.typeStack.push(SymbolType.BOOL)

        when (reg.relationalOperator) {
            "==" -> ilGen.compareEqual()
            "~=" -> ilGen.compareNotEqual()
            "<" -> ilGen.compareLessThan()
            ">" -> ilGen.compareGreaterThan()
        }
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
     * AÇÃO #118: Quebra de linha (print new line)
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

        // No IL, int é representado como float64
        val ilType = if (type == SymbolType.INT) "float64" else type.toString()
        ilGen.declareLocal(id, ilType)
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
    fun performRead(id: String, text: String, line: Int) {
        val symbol = symbolTable.lookup(id)
        if (symbol == null) {
            throw SemanticException("variável $id não declarada; linha $line")
        }
        if (symbol.type == SymbolType.BOOL) {
            throw SemanticException("logico inválido para comando de entrada; linha $line")
        }

        // Ação 124
        writePromptString(text)
        ilGen.readLine()
        when (symbol.type) {
            SymbolType.INT -> {
                ilGen.parseInt64()
                ilGen.convertInt64ToFloat64()
            }
            SymbolType.FLOAT -> ilGen.parseFloat64()
            else -> {}
        }
        ilGen.storeVariable(id)
    }

    fun writePromptString(cteString: String) {
        ilGen.loadStringConstant(cteString)
        ilGen.writeValue(SymbolType.STRING)
    }

    private fun determineArithmeticResultType(type1: SymbolType, type2: SymbolType, line: Int): SymbolType {
        return when {
            type1 == SymbolType.INT && type2 == SymbolType.INT -> SymbolType.INT
            type1 == SymbolType.FLOAT || type2 == SymbolType.FLOAT -> SymbolType.FLOAT
            else -> throw SemanticException("linha $line: tipos incompatíveis para operação aritmética.")
        }
    }

    fun getCode(): String {
        return ilGen.getCode()
    }

    /**
     * AÇÃO #121: Armazenar identificador quando um comando começa com identificador
     */
    override fun enterCommand(ctx: ExprParser.CommandContext) {
        val idNode = ctx.IDENTIFICADOR()
        if (idNode != null) {
            storeIdentifier(idNode.text)
        }
    }

    /**
     * AÇÃO #121b: Armazenar identificador ao entrar em assignment_value
     */
    override fun enterAssignment_value(ctx: ExprParser.Assignment_valueContext) {
        val commandCtx = ctx.parent?.parent as? ExprParser.CommandContext
        val idNode = commandCtx?.IDENTIFICADOR()
        if (idNode != null) {
            storeIdentifier(idNode.text)
        }
    }

    /**
     * AÇÃO #122: Executa atribuição (quando o valor da atribuição foi processado)
     */
    override fun exitAssignment_value(ctx: ExprParser.Assignment_valueContext) {
        if (ctx in processedAssignments) return


        processedAssignments.add(ctx)
        performAssigment(ctx.start.line)
    }

    /**
     * AÇÃO #111: Armazenar operador relacional reconhecido
     */
    override fun exitOperador_relacional(ctx: ExprParser.Operador_relacionalContext) {
        when {
            ctx.EQEQ() != null -> storeRelationalOperator("==")
            ctx.NEQ() != null -> storeRelationalOperator("~=")
            ctx.LT() != null -> storeRelationalOperator("<")
            ctx.GT() != null -> storeRelationalOperator(">")
        }
    }

    /**
     * AÇÃO #112: Se existir operador relacional, realiza a operação relacional
     */
    override fun exitRelacional_(ctx: ExprParser.Relacional_Context) {
        if (ctx.operador_relacional() != null) {
            performRelationalOperation(ctx.start.line)
        }
    }

    /**
     * AÇÃO #118: Após print(...) escrever quebra de linha
     */
    override fun exitPrint_statement(ctx: ExprParser.Print_statementContext) {
        writeNewLine()
    }

    override fun exitRead_statement(ctx: ExprParser.Read_statementContext?) {
        if (ctx == null) {
            return;
        }

        var hasRemaingVariablesToRead = true;
        var input = (ctx.children[2] as ExprParser.Input_listContext);
        while (hasRemaingVariablesToRead) {
            val optionalStringContext = (input.children[0] as ExprParser.Opt_stringContext)
            val text = (optionalStringContext.children[0] as TerminalNodeImpl).symbol.text
            val id = (input.children[1] as TerminalNodeImpl).symbol.text

            performRead(id, text, ctx.start.line)
            val nextInput = (input.children[2] as ExprParser.Extra_input_identifiersContext)
            if (nextInput.children == null) {
                hasRemaingVariablesToRead = false;
            } else {
                input = (nextInput.children[1] as ExprParser.Input_listContext)
            }
        }

    }

    /**
     * AÇÃO #125: Início do comando de seleção (if) - valida expressão e cria rótulo de salto
     */
    override fun exitExpression(ctx: ExprParser.ExpressionContext) {
        when (ctx.parent) {
            is ExprParser.If_statementContext -> ifExpression(ctx)
            is ExprParser.Do_until_statementContext -> doWhileExpression(ctx)
            is ExprParser.ExpressionsContext -> writeValue()
        }
    }

    private fun ifExpression(ctx: ExprParser.ExpressionContext) {
        val exprType = reg.typeStack.pop()
        if (exprType != SymbolType.BOOL) {
            throw SemanticException("linha ${ctx.start.line}: expressão incompatível em comando de seleção")
        }

        val novoRotulo1 = ilGen.createLabel()

        ilGen.branchIfFalse(novoRotulo1)

        reg.labelStack.push(novoRotulo1)
    }

    private fun doWhileExpression(ctx: ExprParser.ExpressionContext) {
        val exprType = reg.typeStack.pop()
        if (exprType != SymbolType.BOOL) {
            throw SemanticException("linha ${ctx.start.line}: expressão incompatível em comando de repetição")
        }

        val rotuloDesempilhado = reg.labelStack.pop()
        ilGen.branchIfFalse(rotuloDesempilhado)
    }

    /**
     * AÇÃO #127: Inicio da cláusula ELSE - cria rótulo de saída e ajusta rótulos
     */
    override fun enterCommands(ctx: ExprParser.CommandsContext) {
        if (ctx.parent !is ExprParser.Else_statementContext) return

        val novoRotulo2 = ilGen.createLabel()

        ilGen.branch(novoRotulo2)

        val novoRotulo1 = reg.labelStack.pop()

        ilGen.emitLabel(novoRotulo1)

        reg.labelStack.push(novoRotulo2)
    }

    /**
     * AÇÃO #126: Final da construção IF - rotula a próxima instrução com o rótulo armazenado
     */
    override fun exitIf_statement(ctx: ExprParser.If_statementContext) {
        val rotuloDesempilhado = reg.labelStack.pop()
        ilGen.emitLabel(rotuloDesempilhado)
    }

    /**
     * AÇÃO #128: Início do comando de repetição (do) - cria rótulo e rotula a próxima instrução
     */
    override fun enterDo_until_statement(ctx: ExprParser.Do_until_statementContext) {
        val novoRotulo = ilGen.createLabel()
        ilGen.emitLabel(novoRotulo)
        reg.labelStack.push(novoRotulo)
    }

}

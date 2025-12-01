package compiler.backend.visitors

import ExprParserBaseVisitor
import compiler.backend.ILGenerator
import compiler.backend.analyzers.SemanticAnalyzer
import compiler.common.SymbolType

class ExprSemanticVisitor(
    private val symbolTable: SymbolType,
    private val semantic: SemanticAnalyzer,
    private val ilGen: ILGenerator
) : ExprParserBaseVisitor<SymbolType>() {

}
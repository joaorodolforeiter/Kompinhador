package compiler.backend

import compiler.common.SymbolType
import java.util.Stack

class SemanticRegisters {
    val typeStack = Stack<SymbolType>()
    val labelStack = Stack<String>()
    val identifierList = mutableListOf<String>()
    var relationalOperator: String = ""
    var type: SymbolType? = null

    fun clearIdentifierList() {
        identifierList.clear()
    }
}
package compiler.common

class SymbolTable {
    private val scopes = mutableListOf(mutableMapOf<String, Symbol>())
    private var currentScope = 0

    fun enterScope() {
        currentScope++
        scopes.add(mutableMapOf())
    }

    fun exitScope() {
        if (currentScope > 0) {
            scopes.removeAt(currentScope)
            currentScope--
        }
    }

    fun declare(name: String, type: SymbolType, isListType: Boolean = false,
                listSize: Int = 0, primitiveType: SymbolType? = null): Boolean {
        if (scopes[currentScope].containsKey(name)) {
            return false
        }
        scopes[currentScope][name] = Symbol(
            name, type, currentScope, false, isListType, listSize, primitiveType
        )
        return true
    }

    fun lookup(name: String): Symbol? {
        for (i in currentScope downTo 0) {
            scopes[i][name]?.let { return it }
        }
        return null
    }

    fun markInitialized(name: String) {
        for (i in currentScope downTo 0) {
            scopes[i][name]?.let {
                scopes[i][name] = it.copy(isInitialized = true)
                return
            }
        }
    }
}
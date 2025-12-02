package compiler.common

class SymbolTable {
    private val scopes = mutableMapOf<String, Symbol>()

    fun declare(
        name: String, type: SymbolType, line: Int, isListType: Boolean = false,
        listSize: Int = 0, primitiveType: SymbolType? = null
    ): Boolean {
        if (scopes.containsKey(name)) {
            return false
        }
        scopes[name] = Symbol(
            name, type, line, false, isListType, listSize, primitiveType
        )
        return true
    }

    fun lookup(name: String): Symbol? {
        return scopes[name]
    }

    fun markInitialized(name: String) {
        scopes[name]?.isInitialized = true
    }
}
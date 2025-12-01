package compiler.common

enum class SymbolType {
    INT, FLOAT, STRING, BOOL, LIST, VOID
}

data class Symbol(
    val name: String,
    val type: SymbolType,
    val scope: Int,
    val isInitialized: Boolean = false,
    val isListType: Boolean = false,
    val listSize: Int = 0,
    val primitiveType: SymbolType? = null
)

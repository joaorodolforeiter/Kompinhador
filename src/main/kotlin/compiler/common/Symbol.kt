package compiler.common

enum class SymbolType {
    INT, FLOAT, STRING, BOOL, LIST, VOID;

    override fun toString(): String {
        return when(this) {
            INT -> "int64"
            FLOAT -> "float64"
            STRING -> "string"
            BOOL -> "bool"
            LIST -> "list"
            VOID -> "void"
        }
    }
}

data class Symbol(
    val name: String,
    val type: SymbolType,
    var lineNumber: Int,
    var isInitialized: Boolean = false,
    val isListType: Boolean = false,
    val listSize: Int = 0,
    val primitiveType: SymbolType? = null
)

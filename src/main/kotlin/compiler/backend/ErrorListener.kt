package compiler.backend

import org.antlr.v4.runtime.*

class ErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        val token = offendingSymbol as? Token
        val errorMsg = when (token?.type) {
            ExprLexer.INVALID_IDENTIFIER -> "Erro: Identificador inválido '${token.text}' na linha $line:$charPositionInLine"
            else -> "Erro sintático: $msg na linha $line:$charPositionInLine"
        }
        println(errorMsg)  // Ou logue de outra forma
        // Aqui você pode throw se quiser, mas BailErrorStrategy já faz isso
    }
}

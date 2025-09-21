package compiler.backend

import ExprLexer
import org.antlr.v4.runtime.*

class Lexer() {

    private val errorListener = object : BaseErrorListener() {
        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) = throw LexerException("Erro léxico na linha $line, coluna $charPositionInLine: $msg")
    }

    fun tokenize(input: String): List<Token> {
        val lexer = ExprLexer(CharStreams.fromString(input)).apply {
            removeErrorListeners()
            addErrorListener(errorListener)
        }

        return generateSequence { lexer.nextToken() }.takeWhile { it.type != Token.EOF }.toList()
    }

}

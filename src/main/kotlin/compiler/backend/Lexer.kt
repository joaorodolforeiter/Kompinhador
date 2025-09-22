package compiler.backend

import ExprLexer
import org.antlr.v4.runtime.*

class Lexer() {

    fun tokenize(input: String): List<Token> {
        val lexer = ExprLexer(CharStreams.fromString(input)).apply {
            removeErrorListeners()
            addErrorListener(ErrorListener)
        }

        return generateSequence { lexer.nextToken() }.takeWhile { it.type != Token.EOF }.toList()
    }

}

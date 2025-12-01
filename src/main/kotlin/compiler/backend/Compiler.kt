package compiler.backend

import ExprLexer
import ExprParser
import compiler.backend.listeners.LexerErrorListener
import compiler.backend.listeners.ParserErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class Compiler {
    fun compile(text: String) {
        val stream = CharStreams.fromString(text)

        val lexer = ExprLexer(stream).apply {
            removeErrorListeners()
            addErrorListener(LexerErrorListener)
        }

        val tokens = CommonTokenStream(lexer)
        tokens.fill()

        val parser = ExprParser(tokens).apply {
            removeErrorListeners()
            addErrorListener(ParserErrorListener)
        }

        val tree = parser.program()
    }
}
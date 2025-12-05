package compiler.backend

import ExprLexer
import ExprParser
import compiler.backend.listeners.LexerErrorListener
import compiler.backend.listeners.ParserErrorListener
import compiler.backend.listeners.ExprSemanticListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream


class Compiler {
    fun compile(text: String): String {
        val stream = CharStreams.fromString(text)

        val lexer = ExprLexer(stream).apply {
            removeErrorListeners()
            addErrorListener(LexerErrorListener)
        }

        val tokens = CommonTokenStream(lexer)
        tokens.fill()

        val listener = ExprSemanticListener()

        val parser = ExprParser(tokens).apply {
            removeErrorListeners()
            addParseListener(listener)
            addErrorListener(ParserErrorListener)
            buildParseTree = true
        }

        var a = parser.program()
        println()

        print(a.toStringTree(parser))

        return listener.getCode()
    }
}
package compiler.backend

import ExprParser
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.IntervalSet


object ParserErrorListener : BaseErrorListener() {

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) {
        val lexer = recognizer as ExprParser

        lexer.ruleNames

        throw LexerException("Linha $line: encontrado ${found(lexer)} esperado ${expected(lexer.expectedTokens)}")
    }

    private fun found(lexer: ExprParser) = when (lexer.currentToken.type) {
        ExprParser.STRING -> "constante_string"

        ExprParser.EOF -> "EOF"

        else -> lexer.currentToken.text
    }

    private fun expected(expectedTokens: IntervalSet): String {
        if (expectedTokens.size() == 1) {
            return when (val token = expectedTokens[0]) {
                ExprParser.EOF -> "EOF"
                ExprParser.IDENTIFICADOR -> "identificador"
                ExprParser.CINT -> "constante_int"
                ExprParser.CFLOAT -> "constante_float"
                ExprParser.STRING -> "constante_string"
                ExprParser.PR_INT -> "int"
                ExprParser.PR_FLOAT -> "float"
                ExprParser.PR_BOOL -> "bool"
                ExprParser.PR_STRING -> "string"
                ExprParser.PR_LIST -> "list"
                else -> error("Unexpected token $token")
            }
        }

        return when (expectedTokens.toSet()) {

            setOf(
                ExprParser.PR_INT,
                ExprParser.PR_FLOAT,
                ExprParser.PR_BOOL,
                ExprParser.PR_STRING,
                ExprParser.PR_LIST
            ) -> "tipo"

            setOf(
                ExprParser.PR_INT,
                ExprParser.PR_FLOAT,
                ExprParser.PR_BOOL,
                ExprParser.PR_STRING,
            ) -> "tipo primitivo"


            else -> "ExprParser.VOCABULARY.getDisplayName(ExprParser)"
        }
    }

    private fun trim(i: Int): String {
        val text = ExprParser.VOCABULARY.getDisplayName(i)
        return text.substring(1, text.length - 1)
    }

}
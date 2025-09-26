package compiler.backend

import ExprLexer
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.LexerNoViableAltException
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.Interval


object ErrorListener : BaseErrorListener() {

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPosition: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        val lexer = recognizer as ExprLexer

        if (e is LexerNoViableAltException) throwError(lexer, e, line)
    }

    private fun throwError(lexico: ExprLexer, e: LexerNoViableAltException, line: Int) {
        val text = e.inputStream?.getText(Interval(e.startIndex, e.startIndex)) ?: ""
        val context = lexico.inputStream?.getText(
            Interval(0.coerceAtLeast(e.startIndex - 30), e.startIndex)) ?: ""

        throw LexerException("Linha $line: ${getErrorMessage(context, text)}")
    }

    private fun getErrorMessage(context: String, text: String): String = when {
        context[0].uppercaseChar() in 'A'..'Z'
            -> "$context identificador inválido"

        context.contains('"') && context.lastIndexOf('"') > context.lastIndexOf('\n')
            -> "constante_string inválida"

        context.lastIndexOf('{') > context.lastIndexOf('}')
            -> "comentário inválido ou não finalizado"

        else -> "$text símbolo inválido"
    }
}

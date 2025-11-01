package compiler.backend

import ExprParser
import compiler.exceptions.ParserException
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
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
        val parser = recognizer as? ExprParser
            ?: throw IllegalStateException("Recognizer não é ExprParser")

        val token = offendingSymbol as? Token
        val foundText = getFoundText(token)
        val expectedText = getExpectedText(parser.expectedTokens, parser)

        throw ParserException("linha $line: encontrado $foundText esperado $expectedText")
    }

    private fun getFoundText(token: Token?): String {
        if (token == null) return "desconhecido"

        return when (token.type) {
            ExprParser.EOF -> "EOF"
            ExprParser.STRING -> "constante_string"

            // Palavras reservadas e símbolos especiais - retornar o lexema
            else -> token.text
        }
    }

    private fun getExpectedText(expectedTokens: IntervalSet, parser: ExprParser): String {
        if (expectedTokens.isNil) return "fim de expressão"

        val tokenSet = expectedTokens.toSet()

        val allTypes = setOf(
            ExprParser.PR_INT,
            ExprParser.PR_FLOAT,
            ExprParser.PR_BOOL,
            ExprParser.PR_STRING,
            ExprParser.PR_LIST
        )

        if (tokenSet.containsAll(allTypes)) {
            val remaingTokens = (tokenSet - allTypes).sorted();
            val tokensNames = remaingTokens.map(::getTokenName) +  "tipo"
            return tokensNames.joinToString(separator = " ")
        }

        val primitiveTypes = setOf(
            ExprParser.PR_INT,
            ExprParser.PR_FLOAT,
            ExprParser.PR_BOOL,
            ExprParser.PR_STRING
        )
        if (tokenSet == primitiveTypes) {
            return "tipo primitivo"
        }

        // Verifica se está em contexto de expressão
        if (isExpressionContext(parser)) {
            return "expressão"
        }

        // Token único
        if (expectedTokens.size() == 1) {
            return getTokenName(expectedTokens.toList()[0])
        }

        // Múltiplos tokens - usa ordem natural dos IDs dos tokens (definida no lexer)
        val sortedTokens = expectedTokens.toList().sorted()
        val tokenNames = sortedTokens.joinToString(" ") { getTokenName(it) }

        return tokenNames
    }

    private fun isExpressionContext(parser: ExprParser): Boolean {
        val ruleStack = parser.ruleInvocationStack

        val expressionRules = setOf(
            "expression", "expression_", "valor", "relacional", "relacional_",
            "aritmetica", "aritmetica_", "termo", "termo_", "fator", "fator_"
        )

        return ruleStack.any { it in expressionRules }
    }

    private fun getTokenName(tokenType: Int): String {
        return when (tokenType) {
            ExprParser.EOF -> "EOF"
            ExprParser.IDENTIFICADOR -> "identificador"
            ExprParser.CINT -> "constante_int"
            ExprParser.CFLOAT -> "constante_float"
            ExprParser.STRING -> "constante_string"

            // Palavras reservadas
            ExprParser.PR_BEGIN -> "begin"
            ExprParser.PR_END -> "end"
            ExprParser.PR_IF -> "if"
            ExprParser.PR_ELSE -> "else"
            ExprParser.PR_DO -> "do"
            ExprParser.PR_UNTIL -> "until"
            ExprParser.PR_READ -> "read"
            ExprParser.PR_PRINT -> "print"
            ExprParser.PR_INT -> "int"
            ExprParser.PR_FLOAT -> "float"
            ExprParser.PR_BOOL -> "bool"
            ExprParser.PR_STRING -> "string"
            ExprParser.PR_LIST -> "list"
            ExprParser.PR_TRUE -> "true"
            ExprParser.PR_FALSE -> "false"
            ExprParser.PR_NOT -> "not"
            ExprParser.PR_AND -> "and"
            ExprParser.PR_OR -> "or"
            ExprParser.PR_ADD -> "add"
            ExprParser.PR_DELETE -> "delete"
            ExprParser.PR_COUNT -> "count"
            ExprParser.PR_SIZE -> "size"
            ExprParser.PR_ELEMENTOF -> "elementOf"

            // Símbolos especiais
            ExprParser.SEMI -> ";"
            ExprParser.COMMA -> ","
            ExprParser.LPAREN -> "("
            ExprParser.RPAREN -> ")"
            ExprParser.EQ -> "="
            ExprParser.ASSIGN -> "<-"
            ExprParser.EQEQ -> "=="
            ExprParser.NEQ -> "~="
            ExprParser.LT -> "<"
            ExprParser.GT -> ">"
            ExprParser.PLUS -> "+"
            ExprParser.MINUS -> "-"
            ExprParser.TIMES -> "*"
            ExprParser.DIV -> "/"

            else -> {
                val displayName = ExprParser.VOCABULARY.getDisplayName(tokenType)

                // Remove aspas simples se existirem
                if (displayName.startsWith("'") && displayName.endsWith("'")) {
                    displayName.substring(1, displayName.length - 1)
                } else {
                    displayName
                }
            }
        }
    }
}
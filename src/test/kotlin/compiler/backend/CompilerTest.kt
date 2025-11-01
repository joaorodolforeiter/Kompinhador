package compiler.backend

import compiler.exceptions.LexerException
import compiler.exceptions.ParserException
import org.antlr.v4.runtime.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class CompilerTest {
    private lateinit var compiler: Compiler

    @BeforeEach
    fun setup() {
        compiler = Compiler()
    }

    @Test
    fun `deve compilar sem erros 👌`() {
        val input =
            """
            begin
                int lado;
                read ("digite um valor para lado: ", lado);
                print ("o valor digitado foi: ", lado);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve lançar LexerException ao detectar string mal formada 🛑`() {
        val input =
            """
            begin
                int lado;
                
                read ("digite um valor para lado: , lado
                print ("o valor digitado foi: ", lado);
            end
            """.trimIndent()

        val exception = assertThrows<LexerException> {
            compiler.compile(input)
        }

        assertEquals("linha 4: constante_string inválida", exception.message)
    }

    @Test
    fun `deve lançar ParserException ao faltar parêntese na chamada de função 😞`() {
        val input =
            """
            begin
                int lado;
                
                read ("digite um valor para lado: ", lado
                print ("o valor digitado foi: ", lado);
            end
            """.trimIndent()

        val exception = assertThrows<ParserException> {
            compiler.compile(input)
        }

        assertEquals("linha 5: encontrado print esperado , )", exception.message)
    }
}
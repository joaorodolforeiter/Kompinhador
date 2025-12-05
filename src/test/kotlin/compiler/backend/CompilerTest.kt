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

    @Test
    fun `deve compilar print com string literal`() {
        val input = """
            begin
                print("Hello, World!");
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com número inteiro`() {
        val input = """
            begin
                print(42);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com número float`() {
        val input = """
            begin
                print(3.14);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com variável inteira`() {
        val input = """
            begin
                int x;
                x = 10;
                print(x);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com variável float`() {
        val input = """
            begin
                float y;
                y = 2.5;
                print(y);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com múltiplas expressões`() {
        val input = """
            begin
                print("Valor: ", 42, " é um número");
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com expressão aritmética`() {
        val input = """
            begin
                print(10 + 20);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com variável e expressão`() {
        val input = """
            begin
                int a;
                int b;
                a = 5;
                b = 3;
                print("Soma: ", a + b);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com string vazia`() {
        val input = """
            begin
                print("");
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar múltiplos prints`() {
        val input = """
            begin
                print("Primeiro");
                print("Segundo");
                print("Terceiro");
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com expressão complexa`() {
        val input = """
            begin
                int x;
                x = 10;
                print("Resultado: ", x * 2 + 5);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }

    @Test
    fun `deve compilar print com float e int`() {
        val input = """
            begin
                float f;
                int i;
                f = 1.5;
                i = 2;
                print("Float: ", f, " Int: ", i);
            end
            """.trimIndent()

        assertDoesNotThrow {
            compiler.compile(input)
        }
    }
}
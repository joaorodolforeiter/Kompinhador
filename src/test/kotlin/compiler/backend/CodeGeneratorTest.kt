package compiler.backend

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class CodeGeneratorTest {
    private lateinit var compiler: Compiler
    private val tempDir: Path = Files.createTempDirectory("kompinhador_test_")

    @BeforeEach
    fun setup() {
        compiler = Compiler()
    }

    @AfterEach
    fun cleanup() {
        tempDir.toFile().listFiles()?.forEach { it.delete() }
    }

    private fun findIlasmPath(): String {
        val possiblePaths = listOf(
            "ilasm",
            "C:\\Windows\\Microsoft.NET\\Framework64\\v4.0.30319\\ilasm.exe",
            "C:\\Windows\\Microsoft.NET\\Framework\\v4.0.30319\\ilasm.exe",
        )
        
        for (path in possiblePaths) {
            try {
                val testProcess = ProcessBuilder(path, "/?")
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start()
                testProcess.waitFor()
                if (testProcess.exitValue() == 0 || testProcess.exitValue() == 1) {
                    return path
                }
            } catch (e: Exception) {
                // Continua tentando outros caminhos
            }
        }
        
        throw AssertionError(
            "ilasm.exe não encontrado. " +
            "Certifique-se de que o .NET Framework SDK está instalado e ilasm está no PATH ou em um dos locais padrão."
        )
    }

    /**
     * Compila o código fonte, gera o IL, compila com ilasm e executa o executável
     * @param sourceCode Código fonte a ser compilado
     * @return Saída do executável gerado
     */
    private fun compileAndExecute(sourceCode: String): String {
        // 1. Compila o código fonte e gera o IL
        val ilCode = compiler.compile(sourceCode)
        
        // 2. Cria arquivo IL temporário
        val testId = UUID.randomUUID().toString().take(8)
        val ilFile = File(tempDir.toFile(), "test_$testId.il")
        ilFile.writeText(ilCode)
        
        // 3. Encontra o ilasm e compila para gerar o .exe
        val ilasmPath = findIlasmPath()
        val exeFile = File(tempDir.toFile(), "test_$testId.exe")
        val ilasmProcess = ProcessBuilder(
            ilasmPath,
            "/exe",
            "/output=${exeFile.absolutePath}",
            ilFile.absolutePath
        )
            .redirectErrorStream(true)
            .start()
        
        val ilasmOutput = ilasmProcess.inputStream.bufferedReader().readText()
        ilasmProcess.waitFor()
        
        if (ilasmProcess.exitValue() != 0) {
            val errorMsg = StringBuilder()
            errorMsg.append("Falha ao compilar IL com ilasm:\n")
            errorMsg.append(ilasmOutput)
            errorMsg.append("\n\nIL gerado:\n")
            errorMsg.append(ilCode)
            throw AssertionError(errorMsg.toString())
        }
        
        // 4. Executa o executável gerado
        val runProcess = ProcessBuilder(exeFile.absolutePath)
            .redirectErrorStream(true)
            .start()
        
        val runOutput = runProcess.inputStream.bufferedReader().readText()
        runProcess.waitFor()
        
        // Limpa arquivos temporários
        ilFile.delete()
        exeFile.delete()
        
        return runOutput
    }

    @Test
    fun `deve gerar e executar programa vazio`() {
        val code = """
            begin
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertEquals("", output.trim())
    }

    @Test
    fun `deve printar string literal`() {
        val code = """
            begin
                print("Hello, World!");
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertEquals("Hello, World!", output.trim())
    }

    @Test
    fun `deve printar número inteiro`() {
        val code = """
            begin
                print(42);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        // O número inteiro é convertido para float64, então pode aparecer como 42.0
        assertTrue(output.trim().contains("42"))
    }

    @Test
    fun `deve printar número float`() {
        val code = """
            begin
                print(3.14);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.trim().contains("3.14"))
    }

    @Test
    fun `deve printar variável inteira`() {
        val code = """
            begin
                int x;
                x = 10;
                print(x);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.trim().contains("10"))
    }

    @Test
    fun `deve printar variável float`() {
        val code = """
            begin
                float y;
                y = 2.5;
                print(y);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.trim().contains("2.5"))
    }

    @Test
    fun `deve printar múltiplas expressões`() {
        val code = """
            begin
                print("Valor: ", 42, " é um número");
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("Valor:"))
        assertTrue(output.contains("42"))
        assertTrue(output.contains("é um número"))
    }

    @Test
    fun `deve printar expressão aritmética de adição`() {
        val code = """
            begin
                print(10 + 20);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.trim().contains("30"))
    }

    @Test
    fun `deve printar expressão aritmética de subtração`() {
        val code = """
            begin
                print(50 - 20);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.trim().contains("30"))
    }

    @Test
    fun `deve printar expressão aritmética de multiplicação`() {
        val code = """
            begin
                print(5 * 4);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.trim().contains("20"))
    }

    @Test
    fun `deve printar expressão aritmética de divisão`() {
        val code = """
            begin
                print(20 / 4);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.trim().contains("5"))
    }

    @Test
    fun `deve printar expressão aritmética complexa`() {
        val code = """
            begin
                int x;
                x = 10;
                print("Resultado: ", x * 2 + 5);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("Resultado:"))
        assertTrue(output.contains("25"))
    }

    @Test
    fun `deve printar múltiplos prints`() {
        val code = """
            begin
                print("Primeiro");
                print("Segundo");
                print("Terceiro");
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("Primeiro"))
        assertTrue(output.contains("Segundo"))
        assertTrue(output.contains("Terceiro"))
    }

    @Test
    fun `deve printar string vazia`() {
        val code = """
            begin
                print("");
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        // String vazia deve gerar apenas uma quebra de linha
        assertTrue(output.trim().isEmpty() || output.trim() == "\n")
    }

    @Test
    fun `deve printar expressão com variáveis`() {
        val code = """
            begin
                int a;
                int b;
                a = 5;
                b = 3;
                print("Soma: ", a + b);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("Soma:"))
        assertTrue(output.contains("8"))
    }

    @Test
    fun `deve printar float e int juntos`() {
        val code = """
            begin
                float f;
                int i;
                f = 1.5;
                i = 2;
                print("Float: ", f, " Int: ", i);
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("Float:"))
        assertTrue(output.contains("1.5"))
        assertTrue(output.contains("Int:"))
        assertTrue(output.contains("2"))
    }

    // ============================================
    // TESTES DE OPERADORES RELACIONAIS
    // ============================================

    @Test
    fun `deve testar operador igualdade com valores iguais`() {
        val code = """
            begin
                if 5 == 5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador igualdade com valores diferentes`() {
        val code = """
            begin
                if 5 == 10
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("false"))
        assertFalse(output.contains("true"))
    }

    @Test
    fun `deve testar operador igualdade com variáveis iguais`() {
        val code = """
            begin
                int x;
                int y;
                x = 10;
                y = 10;
                if x == y
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador igualdade com variáveis diferentes`() {
        val code = """
            begin
                int x;
                int y;
                x = 10;
                y = 20;
                if x == y
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("false"))
        assertFalse(output.contains("true"))
    }

    @Test
    fun `deve testar operador igualdade com floats`() {
        val code = """
            begin
                if 3.14 == 3.14
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador diferente com valores diferentes`() {
        val code = """
            begin
                if 5 ~= 10
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador diferente com valores iguais`() {
        val code = """
            begin
                if 5 ~= 5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("false"))
        assertFalse(output.contains("true"))
    }

    @Test
    fun `deve testar operador diferente com variáveis`() {
        val code = """
            begin
                int x;
                int y;
                x = 15;
                y = 25;
                if x ~= y
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador menor que com valor menor`() {
        val code = """
            begin
                if 5 < 10
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador menor que com valor maior`() {
        val code = """
            begin
                if 10 < 5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("false"))
        assertFalse(output.contains("true"))
    }

    @Test
    fun `deve testar operador menor que com valores iguais`() {
        val code = """
            begin
                if 5 < 5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("false"))
        assertFalse(output.contains("true"))
    }

    @Test
    fun `deve testar operador menor que com variáveis`() {
        val code = """
            begin
                int x;
                int y;
                x = 3;
                y = 7;
                if x < y
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador menor que com floats`() {
        val code = """
            begin
                if 2.5 < 3.5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador maior que com valor maior`() {
        val code = """
            begin
                if 10 > 5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador maior que com valor menor`() {
        val code = """
            begin
                if 5 > 10
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("false"))
        assertFalse(output.contains("true"))
    }

    @Test
    fun `deve testar operador maior que com valores iguais`() {
        val code = """
            begin
                if 5 > 5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("false"))
        assertFalse(output.contains("true"))
    }

    @Test
    fun `deve testar operador maior que com variáveis`() {
        val code = """
            begin
                int x;
                int y;
                x = 8;
                y = 4;
                if x > y
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador maior que com floats`() {
        val code = """
            begin
                if 4.5 > 2.5
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador relacional com expressão aritmética`() {
        val code = """
            begin
                if 10 + 5 > 12
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }

    @Test
    fun `deve testar operador relacional com variáveis e expressões`() {
        val code = """
            begin
                int x;
                int y;
                x = 10;
                y = 5;
                if x * 2 == y * 4
                    print("true");
                else
                    print("false");
                end
            end
        """.trimIndent()

        val output = compileAndExecute(code)
        assertTrue(output.contains("true"))
        assertFalse(output.contains("false"))
    }
}
package compiler.backend

import compiler.exceptions.LexerException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@DisplayName("Testes de Erros Léxicos e Compilações Bem-Sucedidas")
class LexerErrorTest {
    private lateinit var lexicalAnalyser: LexicalAnalyser

    @BeforeEach
    fun setup() {
        lexicalAnalyser = LexicalAnalyser()
    }

    @Nested
    @DisplayName("Compilações Bem-Sucedidas")
    inner class SuccessfulCompilations {

        @Test
        @DisplayName("Programa simples com declaração e atribuição")
        fun `deve compilar programa simples com declaração e atribuição`() {
            val input = """
                begin
                    int numero;
                    numero = 42;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com múltiplas variáveis e tipos")
        fun `deve compilar programa com múltiplas variáveis e tipos`() {
            val input = """
                begin
                    int idade;
                    float altura;
                    string nome;
                    bool ativo;
                    idade = 25;
                    altura = 1.75;
                    nome = "João";
                    ativo = true;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com identificadores válidos com underscore e dígitos")
        fun `deve compilar programa com identificadores válidos com underscore e dígitos`() {
            val input = """
                begin
                    int variavel_123;
                    float valor_42;
                    variavel_123 = 10;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com identificadores válidos MAI MIN")
        fun `deve compilar programa com identificadores válidos MAI MIN`() {
            val input = """
                begin
                    int MeuValor;
                    float OutroValor;
                    MeuValor = 100;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com expressões aritméticas")
        fun `deve compilar programa com expressões aritméticas`() {
            val input = """
                begin
                    int resultado;
                    resultado = 10 + 20 * 3 - 5 / 2;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com expressões relacionais e lógicas")
        fun `deve compilar programa com expressões relacionais e lógicas`() {
            val input = """
                begin
                    bool condicao;
                    condicao = 10 == 10 and 5 > 3 or 2 < 8;
                    condicao = not false;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com comandos read e print")
        fun `deve compilar programa com comandos read e print`() {
            val input = """
                begin
                    int valor;
                    read ("Digite um valor: ", valor);
                    print ("O valor digitado foi: ", valor);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com if-else")
        fun `deve compilar programa com if-else`() {
            val input = """
                begin
                    int numero;
                    if numero > 0
                        print ("Positivo");
                    else
                        print ("Negativo ou zero");
                    end
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com do-until")
        fun `deve compilar programa com do-until`() {
            val input = """
                begin
                    int contador;
                    contador = 0;
                    do
                        contador = contador + 1;
                        print ("Contador: ", contador);
                    until contador == 10
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com listas")
        fun `deve compilar programa com listas`() {
            val input = """
                begin
                    list(int, 10) numeros;
                    numeros = add(numeros, 5);
                    numeros = delete(numeros);
                    int tamanho;
                    tamanho = size(numeros);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com comentários de linha")
        fun `deve compilar programa com comentários de linha`() {
            val input = """
                begin
                    # Este é um comentário de linha
                    int valor; # Outro comentário
                    valor = 42;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com comentários de bloco")
        fun `deve compilar programa com comentários de bloco`() {
            val input = """
                begin
                    { Este é um comentário de bloco }
                    int valor;
                    { Outro comentário
                      de múltiplas linhas }
                    valor = 42;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com strings válidas")
        fun `deve compilar programa com strings válidas`() {
            val input = """
                begin
                    string mensagem;
                    mensagem = "Hello, World!";
                    print ("Mensagem: ", mensagem);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com constantes numéricas")
        fun `deve compilar programa com constantes numéricas`() {
            val input = """
                begin
                    int inteiro;
                    float decimal;
                    inteiro = 0;
                    inteiro = 12345;
                    decimal = 0.0;
                    decimal = 3.14159;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com atribuição usando <-")
        fun `deve compilar programa com atribuição usando seta`() {
            val input = """
                begin
                    int valor;
                    valor <- 100;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Strings Inválidas")
    inner class StringErrors {

        @Test
        @DisplayName("String não finalizada na mesma linha")
        fun `deve lançar LexerException para string não finalizada na mesma linha`() {
            val input = """
                begin
                    string msg = "texto sem fechar
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("constante_string inválida") == true)
        }

        @Test
        @DisplayName("String não finalizada com quebra de linha")
        fun `deve lançar LexerException para string não finalizada com quebra de linha`() {
            val input = """
                begin
                    print ("texto sem fechar
                    int valor = 10;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("constante_string inválida") == true)
        }

        @Test
        @DisplayName("String vazia não fechada")
        fun `deve lançar LexerException para string vazia não fechada`() {
            val input = """
                begin
                    string msg = "
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("constante_string inválida") == true)
        }

        @Test
        @DisplayName("String não finalizada em read")
        fun `deve lançar LexerException para string não finalizada em read`() {
            val input = """
                begin
                    int valor;
                    read ("mensagem sem fechar, valor);
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("constante_string inválida") == true)
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Identificadores Inválidos")
    inner class IdentifierErrors {

        @Test
        @DisplayName("Identificador começando com dígito")
        fun `deve lançar LexerException para identificador começando com dígito`() {
            val input = """
                begin
                    int 123variavel;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true ||
                    exception.message?.contains("símbolo inválido") == true)
        }

        @Test
        @DisplayName("Identificador com dígito sem underscore")
        fun `deve lançar LexerException para identificador com dígito sem underscore`() {
            val input = """
                begin
                    int variavel123;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador começando com underscore")
        fun `deve lançar LexerException para identificador começando com underscore`() {
            val input = """
                begin
                    int _variavel;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador terminando com underscore")
        fun `deve lançar LexerException para identificador terminando com underscore`() {
            val input = """
                begin
                    int variavel_;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador com underscore seguido de letra")
        fun `deve lançar LexerException para identificador com underscore seguido de letra`() {
            val input = """
                begin
                    int variavel_abc;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador com apenas maiúsculas")
        fun `deve lançar LexerException para identificador com apenas maiúsculas`() {
            val input = """
                begin
                    int VARIAVEL;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador com maiúscula isolada no final")
        fun `deve lançar LexerException para identificador com maiúscula isolada no final`() {
            val input = """
                begin
                    int variavelA;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador com múltiplos underscores")
        fun `deve lançar LexerException para identificador com múltiplos underscores`() {
            val input = """
                begin
                    int variavel__123;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Comentários Inválidos")
    inner class CommentErrors {

        @Test
        @DisplayName("Comentário de bloco não finalizado")
        fun `deve lançar LexerException para comentário de bloco não finalizado`() {
            val input = """
                begin
                    { Este comentário não foi fechado
                    int valor = 10;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("comentário inválido ou não finalizado") == true)
        }

        @Test
        @DisplayName("Comentário de bloco com chaves aninhadas não fechadas")
        fun `deve lançar LexerException para comentário de bloco com chaves aninhadas não fechadas`() {
            val input = """
                begin
                    { Comentário { com aninhamento não fechado }
                    int valor = 10;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("comentário inválido ou não finalizado") == true)
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Símbolos Inválidos")
    inner class SymbolErrors {

        @Test
        @DisplayName("Símbolo inválido @")
        fun `deve lançar LexerException para símbolo inválido arroba`() {
            val input = """
                begin
                    int valor@teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }

        @Test
        @DisplayName("Símbolo inválido $")
        fun `deve lançar LexerException para símbolo inválido cifrão`() {
            val input = """
                begin
                    int valor${'$'}teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }

        @Test
        @DisplayName("Símbolo inválido %")
        fun `deve lançar LexerException para símbolo inválido percentual`() {
            val input = """
                begin
                    int valor%teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }

        @Test
        @DisplayName("Símbolo inválido &")
        fun `deve lançar LexerException para símbolo inválido e comercial`() {
            val input = """
                begin
                    int valor&teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }
    }

    @Nested
    @DisplayName("Casos Especiais")
    inner class SpecialCases {

        @Test
        @DisplayName("Programa vazio (apenas begin end)")
        fun `deve compilar programa vazio`() {
            val input = """
                begin
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com múltiplas declarações na mesma linha")
        fun `deve compilar programa com múltiplas declarações na mesma linha`() {
            val input = """
                begin
                    int a, b, c;
                    a = 1;
                    b = 2;
                    c = 3;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com expressões complexas aninhadas")
        fun `deve compilar programa com expressões complexas aninhadas`() {
            val input = """
                begin
                    int resultado;
                    resultado = (10 + 20) * (30 - 5) / 2;
                    bool condicao;
                    condicao = (resultado > 100) and (resultado < 1000) or (resultado == 0);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com strings contendo espaços e caracteres especiais")
        fun `deve compilar programa com strings contendo espaços e caracteres especiais`() {
            val input = """
                begin
                    string msg1 = "Hello, World!";
                    string msg2 = "Valor: 123";
                    string msg3 = "Teste com espaços e números 456";
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com constantes float válidas")
        fun `deve compilar programa com constantes float válidas`() {
            val input = """
                begin
                    float pi = 3.14159;
                    float zero = 0.0;
                    float pequeno = 0.0001;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com identificadores MAI MIN complexos")
        fun `deve compilar programa com identificadores MAI MIN complexos`() {
            val input = """
                begin
                    int MeuValorCompleto;
                    float OutroValorComplexo;
                    string MinhaString;
                    MeuValorCompleto = 100;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com identificadores minúsculos válidos")
        fun `deve compilar programa com identificadores minúsculos válidos`() {
            val input = """
                begin
                    int variavel;
                    float outrovalor;
                    string minhastring;
                    variavel = 10;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com operadores relacionais")
        fun `deve compilar programa com operadores relacionais`() {
            val input = """
                begin
                    bool resultado;
                    resultado = 10 == 20;
                    resultado = 10 ~= 20;
                    resultado = 10 < 20;
                    resultado = 10 > 20;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com listas e operações")
        fun `deve compilar programa com listas e operações`() {
            val input = """
                begin
                    list(int, 5) numeros;
                    numeros = add(numeros, 10);
                    numeros = add(numeros, 20);
                    int tamanho;
                    tamanho = size(numeros);
                    int elemento;
                    elemento = elementOf(numeros, 0);
                    numeros = delete(numeros);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com read sem string")
        fun `deve compilar programa com read sem string`() {
            val input = """
                begin
                    int valor;
                    read (valor);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com múltiplos reads")
        fun `deve compilar programa com múltiplos reads`() {
            val input = """
                begin
                    int a, b, c;
                    read ("Digite a: ", a);
                    read ("Digite b: ", b);
                    read (c);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com expressões com parênteses")
        fun `deve compilar programa com expressões com parênteses`() {
            val input = """
                begin
                    int resultado;
                    resultado = (10 + 5) * 2;
                    resultado = ((10 + 5) * 2) / 3;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com valores booleanos")
        fun `deve compilar programa com valores booleanos`() {
            val input = """
                begin
                    bool verdadeiro;
                    bool falso;
                    verdadeiro = true;
                    falso = false;
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }

        @Test
        @DisplayName("Programa com operador not")
        fun `deve compilar programa com operador not`() {
            val input = """
                begin
                    bool resultado;
                    resultado = not true;
                    resultado = not false;
                    resultado = not (10 > 5);
                end
            """.trimIndent()

            assertDoesNotThrow {
                lexicalAnalyser.validate(input)
            }
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Casos Adicionais de Strings")
    inner class AdditionalStringErrors {

        @Test
        @DisplayName("String não finalizada em print")
        fun `deve lançar LexerException para string não finalizada em print`() {
            val input = """
                begin
                    print ("texto sem fechar
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("constante_string inválida") == true)
        }

        @Test
        @DisplayName("String não finalizada em atribuição")
        fun `deve lançar LexerException para string não finalizada em atribuição`() {
            val input = """
                begin
                    string msg = "texto sem fechar;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("constante_string inválida") == true)
        }

        @Test
        @DisplayName("String não finalizada após vírgula")
        fun `deve lançar LexerException para string não finalizada após vírgula`() {
            val input = """
                begin
                    print ("texto, sem fechar
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("constante_string inválida") == true)
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Casos Adicionais de Identificadores")
    inner class AdditionalIdentifierErrors {

        @Test
        @DisplayName("Identificador com maiúscula no meio sem minúscula depois")
        fun `deve lançar LexerException para identificador com maiúscula no meio sem minúscula depois`() {
            val input = """
                begin
                    int variavelA123;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador com underscore no meio seguido de letra")
        fun `deve lançar LexerException para identificador com underscore no meio seguido de letra`() {
            val input = """
                begin
                    int variavel_abc123;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true)
        }

        @Test
        @DisplayName("Identificador com caractere especial no meio")
        fun `deve lançar LexerException para identificador com caractere especial no meio`() {
            val input = """
                begin
                    int variavel#teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("identificador inválido") == true ||
                    exception.message?.contains("símbolo inválido") == true)
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Casos Adicionais de Comentários")
    inner class AdditionalCommentErrors {

        @Test
        @DisplayName("Comentário de bloco não finalizado no início")
        fun `deve lançar LexerException para comentário de bloco não finalizado no início`() {
            val input = """
                { Comentário não fechado
                begin
                    int valor = 10;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("comentário inválido ou não finalizado") == true)
        }

        @Test
        @DisplayName("Comentário de bloco não finalizado no meio")
        fun `deve lançar LexerException para comentário de bloco não finalizado no meio`() {
            val input = """
                begin
                    int valor1 = 10;
                    { Comentário não fechado
                    int valor2 = 20;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("comentário inválido ou não finalizado") == true)
        }
    }

    @Nested
    @DisplayName("Erros Léxicos - Casos Adicionais de Símbolos")
    inner class AdditionalSymbolErrors {

        @Test
        @DisplayName("Símbolo inválido !")
        fun `deve lançar LexerException para símbolo inválido exclamacao`() {
            val input = """
                begin
                    int valor!teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }

        @Test
        @DisplayName("Símbolo inválido ?")
        fun `deve lançar LexerException para símbolo inválido interrogacao`() {
            val input = """
                begin
                    int valor?teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }

        @Test
        @DisplayName("Símbolo inválido ^")
        fun `deve lançar LexerException para símbolo inválido circunflexo`() {
            val input = """
                begin
                    int valor^teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }

        @Test
        @DisplayName("Símbolo inválido |")
        fun `deve lançar LexerException para símbolo inválido pipe`() {
            val input = """
                begin
                    int valor|teste;
                end
            """.trimIndent()

            val exception = assertThrows<LexerException> {
                lexicalAnalyser.validate(input)
            }

            assertTrue(exception.message?.contains("símbolo inválido") == true)
        }
    }
}


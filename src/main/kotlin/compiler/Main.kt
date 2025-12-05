package compiler

import compiler.backend.Compiler

import java.io.File

fun main() {
    //    FlatLightLaf.setup()
    //    JFrame.setDefaultLookAndFeelDecorated(true)

    val ams = Compiler().compile(
        """
            begin
            float a;
            a = 10;
            do print("Hello, World!"); a = a - 1; until a == 0;
            end
            """.trimIndent()
    )

    print(ams)

    // Write IL code to file
    val ilFile = File("output.il")
    ilFile.writeText(ams)

    // Compile with ilasm
    val ilasmProcess = ProcessBuilder("ilasm", "/exe", "/output=output.exe", ilFile.absolutePath)
        .redirectErrorStream(true)
        .start()

    val ilasmOutput = ilasmProcess.inputStream.bufferedReader().readText()
    ilasmProcess.waitFor()

    if (ilasmProcess.exitValue() == 0) {
        println(ilasmOutput)

        println("\nRunning the executable:")
        val runProcess = ProcessBuilder("mono", "output.exe")
            .redirectErrorStream(true)
            .start()

        val runOutput = runProcess.inputStream.bufferedReader().readText()
        println(runOutput)
        runProcess.waitFor()
    } else {
        println("Compilation failed:")
        println(ilasmOutput)
    }

    //invokeLater { CodeEditor() }
}
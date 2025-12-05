.assembly extern mscorlib {}
.assembly program {}
.module program.exe

.class public Program extends [mscorlib]System.Object {
  .method public static void Main() cil managed {
    .entrypoint
    .maxstack 100
    ldstr "asd"
    call void [mscorlib]System.Console::Write(string)
    ret
  }
}


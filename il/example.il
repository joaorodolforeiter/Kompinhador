//programa fonte:
//
// begin
//   print("ola mundo!");
// end


.assembly extern mscorlib {}
.assembly programa{}
.module programa.exe

.class public _unica{
    .method static public void _principal()
    {
        .entrypoint

        ldstr "ola mundo!"
        call void [mscorlib]System.Console::Write(string)

        ret
    }
}

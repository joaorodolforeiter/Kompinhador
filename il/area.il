// begin
//     int lado, area;
// 
//     read (lado);
//     area = lado * lado;
//     print(area);
// end 

.assembly extern mscorlib {}
.assembly programa{}
.module programa.exe

.class public _unica{
    .method static public void _principal()
    {
        .entrypoint

        .locals (int64 lado)
        .locals (int64 area)

        call string 
            [mscorlib]System.Console::ReadLine()
        
        call int64
            [mscorlib]System.Int64::Parse(string)

        stloc lado

        ldloc lado
        conv.r8
        ldloc lado
        conv.r8

        mul
        conv.i8
        stloc area

        ldloc area
        conv.r8
        conv.i8

        call void 
            [mscorlib]System.Console::Write(int64)

        ret
    }
}

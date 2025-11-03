//programa fonte:
//
// begin
//  int contador;			(1)
//  contador = 1;			(2)
//
//  float numero, resultado;		(3)
//  read (numero);			(4)
//  do 					(5)
//    resultado = numero * contador;	(6)
//    print (resultado, "   ");		(7)
//    contador = contador + 1;		(8)
//  until contador == 10;		(9)
// end					(10)



.assembly extern mscorlib {}
.assembly programa{}
.module programa.exe

.class public _unica{
  .method static public void _principal(){
     .entrypoint


   // linha (1): int contador;
   .locals(int64 contador)

   // linha (2): contador = 1;
   ldc.i8 1
   conv.r8
   conv.i8
   stloc contador

   // linha (3): float numero, resultado;
   .locals(float64 numero, float64 resultado)

   // linha (4): read (numero);
   call string [mscorlib]System.Console::ReadLine()
   call float64 [mscorlib]System.Double::Parse(string)
   stloc numero

   // linha (5): do
   L1:

   // linha (6): resultado = numero * contador;
   ldloc numero
   ldloc contador
   conv.r8
   mul
   stloc resultado

   // linha (7): print (resultado, "   ");	
   ldloc resultado
   call void [mscorlib]System.Console::Write(float64)
   ldstr " "
   call void [mscorlib]System.Console::Write(string)

   // linha (8): contador = contador + 1;	
   ldloc contador
   conv.r8
   ldc.i8 1
   conv.r8
   add
   conv.i8
   stloc contador

   // linha (9): until contador == 10;
   ldloc contador
   conv.r8
   ldc.r8 10
// constantes int podem ser carregadas diretamente com ldc.r8, sendo desnecessária a instrução conv.r8
// ldc.i8 10
// conv.r8
   ceq
   brfalse L1

     ret
  }
}

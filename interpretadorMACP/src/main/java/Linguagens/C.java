package Linguagens;

import java.util.HashSet;

public class C extends Linguagem{
   public C() {
	// TODO Auto-generated constructor stub
	   super();
   }
   
   public static HashSet<String> getReservadas() {
	   HashSet<String> cReservadas = new HashSet<String>();
	   cReservadas.add("asm");
	   cReservadas.add("auto");
	   cReservadas.add("break");
	   cReservadas.add("case");
	   cReservadas.add("const");
	   cReservadas.add("continue");
	   cReservadas.add("default");
	   cReservadas.add("do");
	   cReservadas.add("else");
	   cReservadas.add("enum");
	   cReservadas.add("extern");
	   cReservadas.add("for");
	   cReservadas.add("goto");
	   cReservadas.add("if");
	   cReservadas.add("main");
	   cReservadas.add("printf");
	   cReservadas.add("scanf");
	   cReservadas.add("register");
	   cReservadas.add("return");
	   cReservadas.add("signed");
	   cReservadas.add("sizeof");
	   cReservadas.add("static");
	   cReservadas.add("struct");
	   cReservadas.add("switch");
	   cReservadas.add("typedef");
	   cReservadas.add("union");
	   cReservadas.add("unsigned");
	   cReservadas.add("volatile");
	   cReservadas.add("while");
	   cReservadas.add("#include");
	   return cReservadas;
	   
   }
   
   public static HashSet<String> getTipos() {
	   HashSet<String> cTipos = new HashSet<String>();
	   cTipos.add("char ");
	   cTipos.add("double ");
	   cTipos.add("float ");
	   cTipos.add("int ");
	   cTipos.add("long ");
	   cTipos.add("short ");
	   cTipos.add("void ");
	   return cTipos;
   }
   
}

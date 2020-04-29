package Linguagens;

import java.util.HashSet;

public class Python extends Linguagem{
	public Python() {
		// TODO Auto-generated constructor stub
		super();
	}
   
   public static HashSet<String> getReservadas() {
	   HashSet<String> pReservadas = new HashSet<String>();
	   pReservadas.add("and");
	   pReservadas.add("as");
	   pReservadas.add("assert");
	   pReservadas.add("break");
	   pReservadas.add("class");
	   pReservadas.add("continue");
	   pReservadas.add("def");
	   pReservadas.add("del");
	   pReservadas.add("elif");
	   pReservadas.add("else");
	   pReservadas.add("except");
	   pReservadas.add("exec");
	   pReservadas.add("finally");
	   pReservadas.add("for");
	   pReservadas.add("from");
	   pReservadas.add("global");
	   pReservadas.add("if");
	   pReservadas.add("import");
	   pReservadas.add("in");
	   pReservadas.add("is");
	   pReservadas.add("lambda");
	   pReservadas.add("nonlocal");
	   pReservadas.add("not");
	   pReservadas.add("or");
	   pReservadas.add("pass");
	   pReservadas.add("print");
	   pReservadas.add("raise");
	   pReservadas.add("return");
	   pReservadas.add("try");
	   pReservadas.add("while");
	   pReservadas.add("with");
	   pReservadas.add("yield");
	   pReservadas.add("true");
	   pReservadas.add("false");
	   pReservadas.add("none");
	   return pReservadas;
	   
   }
   
   public static HashSet<String> getTipos() {
	   HashSet<String> pTipos = new HashSet<String>();
	   pTipos.add("bool ");
	   pTipos.add("complex ");
	   pTipos.add("float ");
	   pTipos.add("int ");
	   pTipos.add("str ");
	   return pTipos;
   }
	
}

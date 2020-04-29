package Linguagens;

import java.util.HashSet;

public class Pascal extends Linguagem{
	
		public Pascal() {
			super();
		
		}
	   
	   public static HashSet<String> getReservadas() {
		   HashSet<String> pReservadas = new HashSet<String>();
		   pReservadas.add("AND");
		   pReservadas.add("APPEND");
		   pReservadas.add("ASSIGN");
		   pReservadas.add("BEGIN");
		   pReservadas.add("BLINK");
		   pReservadas.add("CHR");
		   pReservadas.add("CLOSE");
		   pReservadas.add("CLRSCR");
		   pReservadas.add("CONST");
		   pReservadas.add("DIV");
		   pReservadas.add("DO");
		   pReservadas.add("DOWTO");
		   pReservadas.add("ELSE");
		   pReservadas.add("END");
		   pReservadas.add("EOF");
		   pReservadas.add("FOR");
		   pReservadas.add("FUNCTION");
		   pReservadas.add("GOTOXY");
		   pReservadas.add("IF");
		   pReservadas.add("LENGTH");
		   pReservadas.add("MOD");
		   pReservadas.add("NOT");
		   pReservadas.add("OF");
		   pReservadas.add("OR");
		   pReservadas.add("ORD");
		   pReservadas.add("PROCEDURE");
		   pReservadas.add("PROGRAM");
		   pReservadas.add("READ");
		   pReservadas.add("READKEY");
		   pReservadas.add("READLN");
		   pReservadas.add("RECORD");
		   pReservadas.add("REPEAT");
		   pReservadas.add("RESET");
		   pReservadas.add("REWRITE");
		   pReservadas.add("TEXT");
		   pReservadas.add("TEXTBACKGROUND");
		   pReservadas.add("TEXTCOLOR");
		   pReservadas.add("THEN");
		   pReservadas.add("TO");
		   pReservadas.add("TYPE");
		   pReservadas.add("UNTIL");
		   pReservadas.add("VAR");
		   pReservadas.add("WHILE");
		   pReservadas.add("WRITE");
		   pReservadas.add("WRITELN");
		   return pReservadas;
		   
	   }
	   
	   public static HashSet<String> getTipos() {
		   HashSet<String> pTipos = new HashSet<String>();
		   pTipos.add("ARRAY");
		   pTipos.add("BOOLEAN");
		   pTipos.add("CHAR");
		   pTipos.add("REAL");
		   pTipos.add("STRING");
		   return pTipos;
	   }
	
}

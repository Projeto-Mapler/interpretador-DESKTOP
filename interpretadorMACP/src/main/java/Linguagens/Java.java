package Linguagens;

import java.util.HashSet;

public class Java extends Linguagem{
	public Java() {
		super();
	}
	
	public static HashSet<String> getReservadas() {
		HashSet<String> jReservadas = new HashSet<String>();
		jReservadas.add("private");
		jReservadas.add("protected");
		jReservadas.add("public");
		jReservadas.add("main");
		jReservadas.add("abstract");
		jReservadas.add("class");
		jReservadas.add("extends");
		jReservadas.add("final");
		jReservadas.add("implements");
		jReservadas.add("interface");
		jReservadas.add("native");
		jReservadas.add("new");
		jReservadas.add("static");
		jReservadas.add("strictfp");
		jReservadas.add("synchronized");
		jReservadas.add("transient");
		jReservadas.add("volatile");
		jReservadas.add("break");
		jReservadas.add("case");
		jReservadas.add("continue");
		jReservadas.add("default");
		jReservadas.add("do");
		jReservadas.add("else");
		jReservadas.add("for");
		jReservadas.add("if");
		jReservadas.add("instanceof");
		jReservadas.add("return");
		jReservadas.add("switch");
		jReservadas.add("while");
		jReservadas.add("assert");
		jReservadas.add("catch");
		jReservadas.add("finally");
		jReservadas.add("throw");
		jReservadas.add("throws");
		jReservadas.add("try");
		jReservadas.add("import");
		jReservadas.add("package");
		jReservadas.add("super");
		jReservadas.add("this");
		jReservadas.add("const");
		jReservadas.add("goto");
		return jReservadas;
	}
	
	public static HashSet<String> getTipos() {
		HashSet<String> jTipos = new HashSet<String>();
		jTipos.add("boolean ");
		jTipos.add("byte ");
		jTipos.add("char ");
		jTipos.add("double ");
		jTipos.add("float ");
		jTipos.add("int ");
		jTipos.add("long ");
		jTipos.add("short ");
		jTipos.add("String ");
		jTipos.add("void ");
		return jTipos;
	}
}

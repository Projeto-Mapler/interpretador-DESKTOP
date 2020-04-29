package Linguagens;

import java.util.HashSet;

public class Linguagem {
	public static HashSet<String> reservadas, tipos;
	
	public Linguagem() {
		// TODO Auto-generated constructor stub
	}
	
	public static void setReservadas(HashSet<String> n) {
		Linguagem.reservadas = n;
	}
	
	public static void setTipos(HashSet<String> n) {
		Linguagem.tipos = n;
	}
	
	public static HashSet<String> getReservadas(){
		return Linguagem.reservadas;
	}
	
	public static HashSet<String> getTipos(){
		return Linguagem.tipos;
	}
}

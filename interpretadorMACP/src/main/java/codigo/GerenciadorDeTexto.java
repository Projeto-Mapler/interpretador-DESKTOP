package codigo;

import java.util.HashSet;
import java.util.Iterator;

import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;

import Linguagens.*;

public class GerenciadorDeTexto {
	
	private static void setPortugol() {
		Linguagem.setReservadas(Portugol.getReservadas());
		Linguagem.setTipos(Portugol.getTipos());
	}
	
	private static void setC() {
		Linguagem.setReservadas(C.getReservadas());
		Linguagem.setTipos(C.getTipos());
	}
	
	private static void setCPlus() {
		Linguagem.setReservadas(CPlus.getReservadas());
		Linguagem.setTipos(CPlus.getTipos());
	}
	
	private static void setPascal() {
		Linguagem.setReservadas(Pascal.getReservadas());
		Linguagem.setTipos(Pascal.getTipos());
	}
	
	private static void setJava() {
		Linguagem.setReservadas(Java.getReservadas());
		Linguagem.setTipos(Java.getTipos());
	}
	
	private static void setPython() {
		Linguagem.setReservadas(Python.getReservadas());
		Linguagem.setTipos(Python.getTipos());
	}
	
	public static StyleClassedTextArea setCores(StyleClassedTextArea area) {
    	GerenciadorDeTexto.setPortugol();
		area.setStyleClass(0, area.getText().length(), "black");
    	Iterator it = Linguagem.getReservadas().iterator();
    	int in;
    	String texto = area.getText();
    	while(it.hasNext()) {
    		String str = it.next().toString();
    		Iterator e = getIndices(str, texto).iterator();
    		while(e.hasNext()) {
    			in =  Integer.parseInt(e.next().toString());
    			if(str.equals(" inicio ") || str.equals(" inicio\n") || str.equals(" inicio\t") ||  str.equals("\ninicio ") || str.equals("\ninicio\n") || str.equals("\ninicio\t") || str.equals("\tinicio ") || str.equals("\tinicio\n") || str.equals("\tinicio\t")) {
    				if(in == 0)
    					continue;
    			}
    			if(str.equals("variaveis ") || str.equals("variaveis\n") || str.equals("variaveis\t") || str.equals("\tvariaveis ") || str.equals("\tvariaveis\n") || str.equals("\tvariaveis\t") || str.equals("\nvariaveis ") || str.equals("\nvariaveis\n") || str.equals("\nvariaveis\t")) {
    				if(in != 0)
    					continue;
    			}
    			area.setStyleClass(in, in+str.length(), "blue");
    		}

    	}
    	
    	it = Linguagem.getTipos().iterator();
    	while(it.hasNext()) {
    		String str = it.next().toString();
    		Iterator e = getIndices(str, texto).iterator();
    		while(e.hasNext()) {
    			in = Integer.parseInt(e.next().toString());
    			area.setStyleClass(in, in+str.length(), "orange");
    		}
    	}
    	
    	in =  texto.toLowerCase().indexOf("'");
    	int out = texto.toLowerCase().indexOf("'",in+1);
    	while(in != -1 && out != -1) {
    		area.setStyleClass(in, out+1, "red");
    		in =  texto.toLowerCase().indexOf("'",out+1);
    		out = texto.toLowerCase().indexOf("'",in+1);
    	}
    	
    	in =  texto.toLowerCase().indexOf(";");
    	while(in != -1) {
    		area.setStyleClass(in, in+1, "black");
    		in =  texto.toLowerCase().indexOf(";",in+1);
    	}
    	area.setStyleClass(area.getText().length(), area.getText().length(), "black");
    	return area;
    }
    
    public static HashSet<String> getIndices(String palavra, String texto) {
    	HashSet<String> i = new HashSet<String>();
    	String txt = texto;
    	int in = txt.toLowerCase().indexOf(palavra.toLowerCase());
    	while(in != -1) {
    		i.add(""+in);
    		in = txt.toLowerCase().indexOf(palavra.toLowerCase(),in+1);
    	}
    	return i;
    }
    
/*
    public static HashSet<String> getPalavrasChaves(){
    	HashSet<String> hs = new HashSet<String>();
    	hs.add("variaveis ");
    	hs.add("variaveis\n");
    	hs.add("variaveis\t");
    	hs.add(" variaveis ");
    	hs.add(" variaveis\n");
    	hs.add("\nvariaveis ");
    	hs.add("\nvariaveis\n");
    	hs.add("\tvariaveis ");
    	hs.add("\tvariaveis\n");
    	hs.add("\tvariaveis\t");
    	hs.add("inicio ");
    	hs.add(" inicio ");
    	hs.add(" inicio\n");
    	hs.add("\ninicio ");
    	hs.add("\ninicio\n");
    	hs.add("\tinicio ");
    	hs.add("\tinicio\n");
    	hs.add("\tinicio\t");
    	//hs.add("fim ");
    	hs.add(" fim ");
    	hs.add(" fim\n");
    	hs.add("\nfim ");
    	hs.add("\nfim\n");
    	hs.add("\nfim");
    	hs.add("\tfim ");
    	hs.add("\tfim\n");
    	hs.add("\tfim\t");
    	//hs.add("ler ");
    	hs.add(" ler ");
    	hs.add(" ler\n");
    	hs.add("\nler ");
    	hs.add("\nler\n");
    	hs.add("\tler ");
    	hs.add("\tler\n");
    	hs.add("\tler\t");
    	//hs.add("escrever ");
    	hs.add(" escrever ");
    	hs.add(" escrever\n");
    	hs.add("\nescrever ");
    	hs.add("\nescrever\n");
    	hs.add("\tescrever ");
    	hs.add("\tescrever\n");
    	hs.add("\tescrever\t");
    	//hs.add("se ");
    	hs.add(" se ");
    	hs.add(" se\n");
    	hs.add("\nse ");
    	hs.add("\nse\n");
    	hs.add("\tse ");
    	hs.add("\tse\n");
    	hs.add("\tse\t");
    	//hs.add("entao ");
    	hs.add(" entao ");
    	hs.add(" entao\n");
    	hs.add("\nentao ");
    	hs.add("\nentao\n");
    	hs.add("\tentao ");
    	hs.add("\tentao\n");
    	hs.add("\tentao\t");
    	//hs.add("senao ");
    	hs.add(" senao ");
    	hs.add(" senao\n");
    	hs.add("\nsenao ");
    	hs.add("\tsenao ");
    	hs.add("\tsenao\n");
    	hs.add("\nsenao\n");
    	hs.add("\tsenao ");
    	hs.add("\tsenao\n");
    	hs.add("\tsenao\t");
    	hs.add(" e ");
    	hs.add(" e\n");
    	hs.add("\ne ");
    	hs.add("\ne\n");
    	hs.add("\te ");
    	hs.add("\te\n");
    	hs.add("\te\t");
    	hs.add(" verdadeiro ");
    	hs.add(" verdadeiro\n");
    	hs.add("\nverdadeiro ");
    	hs.add("\nverdadeiro\n");
    	hs.add("\tverdadeiro ");
    	hs.add("\tverdadeiro\n");
    	hs.add("\tverdadeiro\t");
    	hs.add("\nverdadeiro;");
    	hs.add("\tverdadeiro;");
    	hs.add(" verdadeiro;");
    	hs.add(" falso ");
    	hs.add(" falso\n");
    	hs.add("\nfalso ");
    	hs.add("\nfalso\n");
    	hs.add("\tfalso ");
    	hs.add("\tfalso\n");
    	hs.add("\tfalso\t");
    	hs.add("\nfalso;");
    	hs.add("\tfalso;");
    	hs.add(" falso;");
    	hs.add(" caso ");
    	hs.add(" caso\n");
    	hs.add("\ncaso ");
    	hs.add("\ncaso\n");
    	hs.add("\tcaso ");
    	hs.add("\tcaso\n");
    	hs.add("\tcaso\t");
    	hs.add(" ou ");
    	hs.add(" ou\n");
    	hs.add("\nou ");
    	hs.add("\nou\n");
    	hs.add("\tou ");
    	hs.add("\tou\n");
    	hs.add("\tou\t");
    	hs.add(" nao ");
    	hs.add(" nao\n");
    	hs.add("\nnao ");
    	hs.add("\nnao\n");
    	hs.add("\tnao ");
    	hs.add("\tnao\n");
    	hs.add("\tnao\t");
    	hs.add(" faca ");
    	hs.add(" faca\n");
    	hs.add("\nfaca ");
    	hs.add("\nfaca\n");
    	hs.add("\tinicio ");
    	hs.add("\tinicio\n");
    	hs.add("\tinicio\t");
    	hs.add(" enquanto ");
    	hs.add(" enquanto\n");
    	hs.add("\nenquanto ");
    	hs.add("\nenquanto\n");
    	hs.add("\tenquanto ");
    	hs.add("\tenquanto\n");
    	hs.add("\tenquanto\t");
    	hs.add(" para ");
    	hs.add(" para\n");
    	hs.add("\npara ");
    	hs.add("\npara\n");
    	hs.add("\tpara ");
    	hs.add("\tpara\n");
    	hs.add("\tpara\t");
    	hs.add(" de ");
    	hs.add(" de\n");
    	hs.add("\nde ");
    	hs.add("\nde\n");
    	hs.add("\tde ");
    	hs.add("\tde\n");
    	hs.add("\tde\t");
    	hs.add(" ate ");
    	hs.add(" ate\n");
    	hs.add("\nate ");
    	hs.add("\nate\n");
    	hs.add("\tate ");
    	hs.add("\tate\n");
    	hs.add("\tate\t");
    	hs.add(" passo ");
    	hs.add(" passo\n");
    	hs.add("\npasso ");
    	hs.add("\npasso\n");
    	hs.add("\tpasso ");
    	hs.add("\tpasso\n");
    	hs.add("\tpasso\t");
    	return hs;
    }
*/   
/*
    public static HashSet<String> getPalavrasTipos(){
    	HashSet<String> hs = new HashSet<String>();
    	hs.add("real;");
    	hs.add("cadeia;");
    	hs.add("inteiro;");
    	hs.add("logico;");
    	hs.add("vetor[");
    	hs.add("vetor ");
    	hs.add("caractere;");
    	return hs;
    }
*/
   
    public static StyleClassedTextArea setCoresCodigo(StyleClassedTextArea area, int ling) {
    	if(ling == 1) {
    		setC();
    	}else if(ling == 2) {
    		setCPlus();
    	}else if(ling == 3) {
    		setPascal();
    	}else if(ling == 4) {
    		setJava();
    	}else if(ling == 5) {
    		setPython();
    	}
    	
    	
    	area.setStyleClass(0, area.getText().length(), "black");
    	Iterator it = Linguagem.getReservadas().iterator();
    	int in;
    	String texto = area.getText();
    	while(it.hasNext()) {
    		String str = it.next().toString();
    		Iterator e = getIndicesCodigo(str, texto).iterator();
    		while(e.hasNext()) {
    			in =  Integer.parseInt(e.next().toString());
    			area.setStyleClass(in, in+str.length(), "blue");
    		}
    	}
    	
    	it = Linguagem.getTipos().iterator();
    	while(it.hasNext()) {
    		String str = it.next().toString();
    		Iterator e = getIndicesCodigo(str, texto).iterator();
    		while(e.hasNext()) {
    			in =  Integer.parseInt(e.next().toString());
    			area.setStyleClass(in, in+str.length(), "orange");
    		}
    	}
    	
    	in =  texto.toLowerCase().indexOf("\"");
    	int out = texto.toLowerCase().indexOf("\"",in+1);
    	while(in != -1 && out != -1) {
    		area.setStyleClass(in, out+1, "red");
    		in =  texto.toLowerCase().indexOf("\"",out+1);
    		out = texto.toLowerCase().indexOf("\"",in+1);
    	}
    	
    	in =  texto.toLowerCase().indexOf("'");
    	out = texto.toLowerCase().indexOf("'",in+1);
    	while(in != -1 && out != -1) {
    		area.setStyleClass(in, out+1, "red");
    		in =  texto.toLowerCase().indexOf("'",out+1);
    		out = texto.toLowerCase().indexOf("'",in+1);
    	}
    	area.setStyleClass(area.getText().length(), area.getText().length(), "black");
    	return area;
    }

    public static HashSet<String> getIndicesCodigo(String palavra, String texto) {
    	HashSet<String> i = new HashSet<String>();
    	String txt = texto;
    	int in = txt.toLowerCase().indexOf(palavra.toLowerCase());
    	while(in != -1) {
    		i.add(""+in);
    		in = txt.toLowerCase().indexOf(palavra.toLowerCase(),in+1);
    	}
    	return i;
    }
}

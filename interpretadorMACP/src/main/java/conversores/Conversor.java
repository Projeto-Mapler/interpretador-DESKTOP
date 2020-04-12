package conversores;

import java.util.HashMap;
import java.util.Map;

import interpreter.VariavelVetor;
import model.TokenType;
import tree.Declaracao;
import tree.Expressao;
import tree.Declaracao.Programa;

public abstract class Conversor {
	
	protected Escritor escritor;
	protected Programa programa;
	private Map<String, VariavelVetor> mapaVariaveisVetor; // salva as variaveis do tipo vetor
	
	public Conversor(Programa programa) {
		this.escritor = new Escritor();
		this.programa = programa;
		this.mapaVariaveisVetor = new HashMap<String, VariavelVetor>();
	}
	
	public abstract String converter();
	
	protected void addVariavelVetor(String nome, VariavelVetor variavel) {
		this.mapaVariaveisVetor.put(nome, variavel);
	}
	
	protected VariavelVetor getVariavelVetor(String nome) {
		if(this.mapaVariaveisVetor.containsKey(nome)) 
			return this.mapaVariaveisVetor.get(nome);
		return null;
	}
	
	protected String tipoVariavel(TokenType tipo) {
		switch (tipo) {
			case TIPO_CADEIA :
				return "String";
			case TIPO_INTEIRO :
				return "int";
			case TIPO_REAL :
				return "double";
			case TIPO_CARACTERE :
				return "char";
			// case TIPO_VETOR : return "arrya";
			case TIPO_LOGICO :
				return "boolean";
			default :
				// throw error?
				return "";
		}
	}
}

package interpreter;

import static model.TokenType.ASTERISCO;
import static model.TokenType.BARRA;
import static model.TokenType.MAIS;
import static model.TokenType.MENOS;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import main.Principal;
import model.Token;
import model.TokenType;
import parser.RuntimeError;
import tree.Declaracao;
import tree.Declaracao.Bloco;
import tree.Declaracao.Enquanto;
import tree.Declaracao.Ler;
import tree.Declaracao.Para;
import tree.Declaracao.Print;
import tree.Declaracao.Programa;
import tree.Declaracao.Repita;
import tree.Declaracao.Se;
import tree.Declaracao.Var;
import tree.Declaracao.VariavelArray;
import tree.Expressao;
import tree.Expressao.Atribuicao;
import tree.Expressao.AtribuicaoArray;
import tree.Expressao.Binario;
import tree.Expressao.ExpParentizada;
import tree.Expressao.Grupo;
import tree.Expressao.Literal;
import tree.Expressao.Logico;
import tree.Expressao.Unario;
import tree.Expressao.Variavel;

public class JavaConversorTeste
		implements
			Expressao.Visitor<Void>,
			Declaracao.Visitor<Void> {

	private StringBuilder builder;
	private int indexIdentacao = 0;
	private Map<String, VariavelVetor> mapaVariaveisArray; 

	public JavaConversorTeste() {
		this.builder = new StringBuilder();
		this.mapaVariaveisArray = new HashMap<String, VariavelVetor>();
	}

	public String converter(Declaracao.Programa programa) {
		try {
			this.visitProgramaDeclaracao(programa);
			String programaJava = this.builder.toString();
			this.reset();
			if (programaJava.length() > 0) {
				return programaJava;
			}
		} catch (RuntimeError error) {
			Principal.runtimeError(error);
		} finally {
			this.reset();
		}
		return null;
	}

	/**
	 * Adiciona uma linha ao builder do resultado
	 * 
	 * @param linha
	 * @param identar
	 *            -> nulo = nao altera o index da identacao, true - aumenta o
	 *            index, false - diminui o index
	 * @param novaLinha
	 *            -> append \n
	 */
	private void addLinha(String linha, Boolean identar, Boolean novaLinha) {

		String identacao = "";
		for (int i = 0; i < indexIdentacao; i++) {
			identacao = identacao.concat("\t");
		}

		String quebraLinha = "";
		if (novaLinha == true)
			quebraLinha = "\n";

		this.builder.append(identacao + linha + quebraLinha);
		if (identar != null) {
			if (identar == true) {
				this.indexIdentacao++;
			}
			if (identar == false) {
				this.indexIdentacao--;
				if (this.indexIdentacao < 0)
					this.indexIdentacao = 0;
			}
		}
	}
	private void concaternarNaLinha(String linha, Boolean identar,
			Boolean novaLinha) {
		String quebraLinha = "";
		if (novaLinha == true)
			quebraLinha = "\n";
		this.builder.append(linha + quebraLinha);
		if (identar != null) {
			if (identar == true) {
				this.indexIdentacao++;
			}
			if (identar == false) {
				this.indexIdentacao--;
				if (this.indexIdentacao < 0)
					this.indexIdentacao = 0;
			}
		}
	}

	private void reset() {
		this.indexIdentacao = 0;
		this.builder = new StringBuilder();
	}

	private String tipoVariavel(TokenType tipo) {
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

	private String getOperadorLogico(TokenType op) {
		switch (op) {
			case OU :
				return "||";
			case E :
				return "&&";
			default :
				return "";
		}
	}
	

	private void evaluate(Expressao expressao) {
		expressao.accept(this);
	}

	private void execute(Declaracao declaracao) {
		declaracao.accept(this);
	}

	@Override
	public Void visitBlocoDeclaracao(Bloco declaracao) {
		for (Declaracao statement : declaracao.declaracoes) {
			execute(statement);
		}
		return null;
	}

	@Override
	public Void visitExpressaoDeclaracao(tree.Declaracao.Expressao declaracao) {
		addLinha("", null, false);
		evaluate(declaracao.expressao);
		concaternarNaLinha(";", null, true);
		return null;
	}

	@Override
	public Void visitPrintDeclaracao(Print declaracao) {

		addLinha("System.out.println(", null, false);
		evaluate(declaracao.expressao);
		concaternarNaLinha(");", null, true);
		return null;
	}

	@Override
	public Void visitSeDeclaracao(Se declaracao) {
		addLinha("if (", null, false);
		evaluate(declaracao.condicao);
		concaternarNaLinha(") {", true, true);
		execute(declaracao.entaoBloco);
		this.indexIdentacao--;
		if (declaracao.senaoBloco == null) {
			addLinha("}", false, true);
			return null;
		}
		addLinha("} else {", true, true);
		execute(declaracao.senaoBloco);
		this.indexIdentacao--;
		addLinha("}", null, true);
		return null;
	}

	@Override
	public Void visitLerDeclaracao(Ler declaracao) {
		Expressao atribuicao = declaracao.atribuicao;
		if (atribuicao instanceof Expressao.Atribuicao) {
			String lexeme = ((Expressao.Atribuicao) atribuicao).nome.lexeme;
			addLinha(lexeme + " = entrada.nextByte();", null, true);
			
		}
		if (atribuicao instanceof Expressao.AtribuicaoArray) {
			String lexeme =((Expressao.AtribuicaoArray) atribuicao).nome.lexeme;
			addLinha(lexeme + "[", null, false);
			evaluate(((Expressao.AtribuicaoArray) atribuicao).index);
			
			concaternarNaLinha("] = entrada.nextByte();", null, true);
//			addLinha(lexeme + "["+ +"] = " + "entrada.nextByte();", null, true);
			

		}
		return null;
	}

	@Override
	public Void visitVarDeclaracao(Var declaracao) {

		String tipo = this.tipoVariavel(declaracao.tipo.type);
		// TODO interceptar tipo vetor...
		addLinha("public static " + tipo + " " + declaracao.nome.lexeme + ";",
				null, true);
		return null;
	}

	@Override
	public Void visitVariavelArrayDeclaracao(VariavelArray declaracao) {
		String tipo = this.tipoVariavel(declaracao.tipo.type);
		VariavelVetor vv = new VariavelVetor(
				declaracao.tipo.type, 
				(int)((Expressao.Literal) declaracao.intervaloI).valor, 
				(int)((Expressao.Literal) declaracao.intervaloF).valor);

		addLinha("public static " + tipo + " " + declaracao.nome.lexeme + "["+ vv.getTamanho() +"];",
				null, true);
		this.mapaVariaveisArray.put(
				declaracao.nome.lexeme, 
				vv);
		return null;
	}

	

	@Override
	public Void visitParaDeclaracao(Para declaracao) {
		addLinha("for (", null, false);
		evaluate(declaracao.atribuicao);
		concaternarNaLinha("; ", null, false);
		evaluate(declaracao.condicao);
		concaternarNaLinha("; ", null, false);
		evaluate(declaracao.incremento);
		concaternarNaLinha(") {", true, true);
		execute(declaracao.facaBloco);
		this.indexIdentacao--;
		addLinha("}", null, true);
		return null;
	}

	@Override
	public Void visitEnquantoDeclaracao(Enquanto declaracao) {
		addLinha("while (", null, false);
		evaluate(declaracao.condicao);
		concaternarNaLinha(") {", true, true);
		execute(declaracao.corpo);
		this.indexIdentacao--;
		addLinha("}", null, true);
		return null;
	}

	@Override
	public Void visitProgramaDeclaracao(Programa declaracao) {
		addLinha("import java.util.Scanner;", null, true);
		addLinha("public class Programa {", true, true);
		addLinha("", null, true);
		for (Declaracao variaveis : declaracao.variaveis) {
			execute(variaveis);
		}
		addLinha("", null, true);
		addLinha("public static void main(String[] args) {", true, true);
		addLinha("Scanner entrada = new Scanner(System.in);", null, true);
		concaternarNaLinha("", null, true);
		for (Declaracao corpo : declaracao.corpo) {
			execute(corpo);
		}
		this.indexIdentacao--;
		addLinha("}", false, true); // main
		addLinha("}", null, true); // class
		return null;
	}

	@Override
	public Void visitBinarioExpressao(Binario expressao) {
		evaluate(expressao.esquerda);
		switch (expressao.operador.type) {
			case MENOS :
				concaternarNaLinha(" - ", null, false);
				break;
			case BARRA :
				concaternarNaLinha(" / ", null, false);
				break;
			case ASTERISCO :
				concaternarNaLinha(" * ", null, false);
				break;
			case MAIS :
				concaternarNaLinha(" + ", null, false);
				break;
			case MAIOR_QUE :
				concaternarNaLinha(" > ", null, false);
				break;
			case MAIOR_IQUAL :
				concaternarNaLinha(" >= ", null, false);
				break;
			case MENOR_QUE :
				concaternarNaLinha(" < ", null, false);
				break;
			case MENOR_IGUAL :
				concaternarNaLinha(" <= ", null, false);
				break;
			case DIFERENTE :
				concaternarNaLinha(" != ", null, false);
				break;
			case IGUAL :
				concaternarNaLinha(" == ", null, false);
				break;
			default :
				break; // throw erro?
		}
		evaluate(expressao.direita);
		return null;
	}

	@Override
	public Void visitGrupoExpressao(Grupo expressao) {
		evaluate(expressao.expressao);
		return null;
	}

	@Override
	public Void visitLiteralExpressao(Literal expressao) {
		Object valor = expressao.valor;
		if (valor instanceof String) {
			valor = "\"".concat((String) valor).concat("\"");
		}
		concaternarNaLinha(valor.toString(), null, false);
		return null;
	}

	@Override
	public Void visitLogicoExpressao(Logico expressao) {
		evaluate(expressao.esquerda);

		concaternarNaLinha(
				" " + getOperadorLogico(expressao.operador.type) + " ", null,
				false);

		evaluate(expressao.direita);
		return null;
	}

	@Override
	public Void visitUnarioExpressao(Unario expressao) {

		switch (expressao.operador.type) {
			case NAO :
				concaternarNaLinha("!", null, false);
				break;
			case MENOS :
				concaternarNaLinha("-", null, false);
				break;

			default :
				break; // throw error?
		}
		evaluate(expressao.direira);
		return null;
	}

	@Override
	public Void visitAtribuicaoExpressao(Atribuicao expressao) {
		concaternarNaLinha(expressao.nome.lexeme + " = ", null, false);
		evaluate(expressao.valor);

		return null;
	}

	@Override
	public Void visitAtribuicaoArrayExpressao(AtribuicaoArray expressao) {

		concaternarNaLinha(expressao.nome.lexeme, null, false);
		concaternarNaLinha("[", null, false);
		
		if(expressao.index instanceof Expressao.Literal) {
			
			int valorIndex = (int)((Expressao.Literal) expressao.index).valor;
			concaternarNaLinha(
					this.mapaVariaveisArray.get(expressao.nome.lexeme).resolverIndex(valorIndex) + "",
					null, 
					false);
		} else {
			evaluate(expressao.index);
		}	
		concaternarNaLinha("] = ", null, false);
		evaluate(expressao.valor);
		
		return null;
	}

	@Override
	public Void visitVariavelArrayExpressao(
			tree.Expressao.VariavelArray expressao) {
		String nome = expressao.nome.lexeme;
		if(expressao.index == null) {
			concaternarNaLinha(nome, null, false);
		}else {			
			concaternarNaLinha(nome + "[", null, false);
			evaluate(expressao.index);
			concaternarNaLinha("]", null, false);
		}
		
		
		return null;
	}

	@Override
	public Void visitVariavelExpressao(Variavel expressao) {
		concaternarNaLinha(expressao.nome.lexeme, null, false);
		return null;
	}

	@Override
	public Void visitExpParentizadaExpressao(ExpParentizada expressao) {
		concaternarNaLinha("(", null, false);
		evaluate(expressao.grupo);
		concaternarNaLinha(")", null, false);
		return null;
	}

	@Override
	public Void visitRepitaDeclaracao(Repita declaracao) {
		addLinha("do {", true, true);
		execute(declaracao.corpo);
		this.indexIdentacao--;
		addLinha("while (", null, false);
		evaluate(declaracao.condicao);
		concaternarNaLinha(");", null, true);
		return null;
	}

}

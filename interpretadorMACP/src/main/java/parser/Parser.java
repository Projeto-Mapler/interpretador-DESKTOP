package parser;

import static model.TokenType.ASTERISCO;
import static model.TokenType.ATE;
import static model.TokenType.ATRIBUICAO;
import static model.TokenType.BARRA;
import static model.TokenType.CADEIA;
import static model.TokenType.CARACTERE;
import static model.TokenType.DE;
import static model.TokenType.DIFERENTE;
import static model.TokenType.DIR_CHAVES;
import static model.TokenType.DIR_COLCHETE;
import static model.TokenType.DIR_PARENTESES;
import static model.TokenType.DOIS_PONTOS;
import static model.TokenType.E;
import static model.TokenType.ENQUANTO;
import static model.TokenType.ENTAO;
import static model.TokenType.EOF;
import static model.TokenType.ESCREVER;
import static model.TokenType.ESQ_CHAVES;
import static model.TokenType.ESQ_COLCHETE;
import static model.TokenType.ESQ_PARENTESES;
import static model.TokenType.FACA;
import static model.TokenType.FALSO;
import static model.TokenType.FIM;
import static model.TokenType.IDENTIFICADOR;
import static model.TokenType.IGUAL;
import static model.TokenType.INICIO;
import static model.TokenType.INTEIRO;
import static model.TokenType.INTERVALO;
import static model.TokenType.LER;
import static model.TokenType.MAIOR_IQUAL;
import static model.TokenType.MAIOR_QUE;
import static model.TokenType.MAIS;
import static model.TokenType.MENOR_IGUAL;
import static model.TokenType.MENOR_QUE;
import static model.TokenType.MENOS;
import static model.TokenType.NAO;
import static model.TokenType.OU;
import static model.TokenType.PARA;
import static model.TokenType.PASSO;
import static model.TokenType.PONTO_VIRGULA;
import static model.TokenType.REAL;
import static model.TokenType.REPITA;
import static model.TokenType.SE;
import static model.TokenType.SENAO;
import static model.TokenType.TIPO_CADEIA;
import static model.TokenType.TIPO_CARACTERE;
import static model.TokenType.TIPO_INTEIRO;
import static model.TokenType.TIPO_LOGICO;
import static model.TokenType.TIPO_MODULO;
import static model.TokenType.TIPO_REAL;
import static model.TokenType.TIPO_VETOR;
import static model.TokenType.VARIAVEIS;
import static model.TokenType.VERDADEIRO;
import static model.TokenType.VIRGULA;

import java.util.ArrayList;
import java.util.List;

import main.Principal;
import model.ParserError;
import model.Token;
import model.TokenType;
import tree.Declaracao;
import tree.Expressao;

public class Parser {
	private final List<Token> tokens;
	private int current = 0;
	private Principal runTimer;

	public Parser(Principal runTimer, List<Token> tokens) {
		this.runTimer = runTimer;
		this.tokens = tokens;
	}

	public Declaracao.Programa parse() {
		List<Declaracao> variaveis = new ArrayList<Declaracao>();
		List<Declaracao> corpo = new ArrayList<Declaracao>();
		List<Declaracao> modulos = new ArrayList<Declaracao>();
		try {

			Token variaveisToken = consume(VARIAVEIS, "Esperado \"variaveis\"");
			while (!isAtEnd() && peek().type != INICIO) {
				variaveis.add(declaracaoVariaveis());
			}
			consume(INICIO, "Esperado \"inicio\"");

			while (!isAtEnd() && peek().type != FIM) {
				corpo.add(declaracao());
			}
			consume(FIM, "Esperado \"fim\"");

			while (!isAtEnd()) {
				modulos.add(declaracaoModulo());
			}
			return new Declaracao.Programa(variaveisToken.line, variaveis, corpo, modulos);

		} catch (ParserError e) {
			e.printStackTrace();
		}
		return null;
	}

	// NAVEGADORES:
	private boolean isAtEnd() {
		return peek().type == EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

	private Token advance() {
		if (!isAtEnd())
			current++;
		return previous();
	}

	private boolean check(TokenType type) {
		if (isAtEnd())
			return false;
		return peek().type == type;
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}

		return false;
	}

	private Token consume(TokenType type, String message) {
		if (check(type))
			return advance();

		throw error(peek(), message);
	}

	private void synchronize() {
		advance();

		while (!isAtEnd()) {
			if (previous().type == PONTO_VIRGULA)
				return;

			switch (peek().type) {
			case VARIAVEIS:
			case INICIO:
			case FIM:
			case ENQUANTO:
			case PARA:
			case SE:
			case LER:
			case ESCREVER:
			case REPITA:
				return;
			}

			advance();
		}
	}

	private ParserError error(Token token, String mensagem) {
		ParserError erro = new ParserError(token, mensagem);
		runTimer.error(erro);
		return erro;
	}

	// REGRAS:
	/**
	 * declaracaoVariaveis → IDENTIFICADOR ("," IDENTIFICADOR) ":" TIPO_DADO |
	 * varArray ";"
	 * 
	 * @return
	 */
	private Declaracao declaracaoVariaveis() {
		List<Declaracao> retorno = new ArrayList<Declaracao>();
		List<Token> nomes = new ArrayList<Token>();
		do {
			nomes.add(consume(IDENTIFICADOR, "Esperado nome da variavel."));
		} while (match(VIRGULA));
		consume(DOIS_PONTOS, "Esperado ':' ");

		if (match(TIPO_VETOR)) {
			consume(ESQ_COLCHETE, "Esperado [");
			Token intervaloI = consume(INTEIRO, "Esperado valor inteiro positivo");
			consume(INTERVALO, "Esperado ..");
			Token intervaloF = consume(INTEIRO, "Esperado valor inteiro positivo");
			consume(DIR_COLCHETE, "Esperado ]");
			consume(DE, "Esperado de");
			Token tipoDoVetor = tipoDado();
			for (Token nome : nomes) {
				Declaracao declaracaoVariavelArray = declaracaoVariavelArray(nome, intervaloI, intervaloF, tipoDoVetor);
				retorno.add(declaracaoVariavelArray);
			}
		} else {
			Token tipo = tipoDado();
			for (Token nome : nomes) {
				Declaracao.Var declaracao = new Declaracao.Var(nome.line, nome, tipo);
				retorno.add(declaracao);
			}
		}
		consume(PONTO_VIRGULA, "Esperado ;");
		return new Declaracao.VarDeclaracoes(nomes.get(0).line, retorno);
	}

	/**
	 * declaracao → expressaoDeclarativa | escrever | ler | bloco | se | enquanto |
	 * para | repita
	 * 
	 * @return
	 */
	private Declaracao declaracao() {
		try {
			if (match(SE))
				return seDeclaracao();
			if (match(PARA))
				return paraDeclaracao();
			if (match(ENQUANTO))
				return enquantoDeclaracao();
			if (match(ESCREVER))
				return escreverDeclaracao();
			if (match(LER))
				return lerDeclaracao();
			if (match(ESQ_CHAVES))
				return new Declaracao.Bloco(previous().line, bloco());
			if (match(REPITA))
				return repitaDeclaracao();
			return expressaoDeclaracao();
		} catch (ParserError error) {
			synchronize();
			return null;
		}
	}

	/**
	 * "{" (declaracao)* "}"
	 * 
	 * @return
	 */
	private List<Declaracao> bloco() {
		List<Declaracao> declaracoes = new ArrayList<>();

		while (!check(DIR_CHAVES) && !isAtEnd()) {
			declaracoes.add(declaracao());
		}

		consume(DIR_CHAVES, "Esperado '}' depois do bloco.");
		return declaracoes;
	}

	/**
	 * escrever → "escrever" expressao";"
	 * 
	 * @return
	 */
	private Declaracao escreverDeclaracao() {
		List<Expressao> expressoes = new ArrayList<Expressao>();
		do {
			expressoes.add(expressao());
		} while (match(VIRGULA));
		consume(PONTO_VIRGULA, "Esperado ';' depois do valor.");
		return new Declaracao.Escreva(previous().line, expressoes);
	}

	/**
	 * ler → "ler" variavel ";"
	 * 
	 * @return
	 */
	private Declaracao lerDeclaracao() {
		Expressao expressao = ou();
		Declaracao retorno = null;
		if (expressao instanceof Expressao.VariavelArray) {
			Token nome = ((Expressao.VariavelArray) expressao).nome;
			Expressao index = ((Expressao.VariavelArray) expressao).index;
			retorno = new Declaracao.Ler(nome.line, new Expressao.AtribuicaoArray(nome.line, nome, index, null));
		} else if (expressao instanceof Expressao.Variavel) {
			Token nome = ((Expressao.Variavel) expressao).nome;
			retorno = new Declaracao.Ler(nome.line, new Expressao.Atribuicao(nome.line, nome, null));
		} else {
			error(previous(), "Esperado uma variável");
		}

		consume(PONTO_VIRGULA, "Esperado ';' depois do valor.");
		return retorno;
	}

	/**
	 * expressaoDeclarativa → expressao ";"
	 * 
	 * @return
	 */
	private Declaracao expressaoDeclaracao() {
		Expressao expressao = expressao();
		consume(PONTO_VIRGULA, "Esperado ';' depois do valor.");
		if (expressao instanceof Expressao.Variavel) {
			return chamadaModulo(((Expressao.Variavel) expressao).nome);
		}
		return new Declaracao.Expressao(previous().line, expressao);
	}

	/**
	 * expressao → atribuicao
	 * 
	 * @return
	 */
	private Expressao expressao() {
		return atribuicao();
	}

	/**
	 * expParentizada → "(" expressao ")"
	 * 
	 * @return
	 */
	private Expressao expParentizada() {
		Expressao expressao = expressao();
		consume(DIR_PARENTESES, "Esperado ')' depois da expressao.");
		return new Expressao.ExpParentizada(previous().line,new Expressao.Grupo(previous().line,expressao));
	}

	/**
	 * atribuicao → (IDENTIFICADOR "<-" atribuicao) | ou
	 * 
	 * @return
	 */
	private Expressao atribuicao() {
		Expressao expressao = ou();
		if (match(ATRIBUICAO)) {
			Token atribuicao = previous();
			Expressao valor = atribuicao();

			if (expressao instanceof Expressao.Variavel) {
				Token nome = ((Expressao.Variavel) expressao).nome;
				return new Expressao.Atribuicao(nome.line, nome, valor);
			}
			if (expressao instanceof Expressao.VariavelArray) {
				Token nome = ((Expressao.VariavelArray) expressao).nome;
				Expressao index = ((Expressao.VariavelArray) expressao).index;
				return new Expressao.AtribuicaoArray(nome.line, nome, index, valor);
			}
			error(atribuicao, "Atribuicao inválida");
		}
		return expressao;
	}

	/**
	 * ou → e ("ou" e)*
	 * 
	 * @return
	 */
	private Expressao ou() {
		Expressao expressao = e();

		while (match(OU)) {
			Token operador = previous();
			Expressao direita = e();
			expressao = new Expressao.Logico(operador.line, expressao, operador, direita);
		}
		return expressao;
	}

	/**
	 * e → igualdade ("e" igualdade)*
	 * 
	 * @return
	 */
	private Expressao e() {
		Expressao expressao = igualdade();

		while (match(E)) {
			Token operador = previous();
			Expressao direita = igualdade();
			expressao = new Expressao.Logico(operador.line, expressao, operador, direita);
		}
		return expressao;
	}

	/**
	 * igualdade → comparacao ( ( "!=" | "==" ) comparacao)*
	 * 
	 * @return
	 */
	private Expressao igualdade() {
		Expressao expressao = comparacao(); // esquerda

		while (match(DIFERENTE, IGUAL)) {
			Token operador = previous();
			Expressao direita = comparacao();
			expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
		}

		return expressao; // esquerda ou Binario
	}

	/**
	 * comparacao → adicao( ( ">" | ">=" | "<" | "<=" ) adicao)*
	 * 
	 * @return
	 */
	private Expressao comparacao() {
		Expressao expressao = adicao(); // esquerda

		while (match(MAIOR_QUE, MAIOR_IQUAL, MENOR_QUE, MENOR_IGUAL)) {
			Token operador = previous();
			Expressao direita = adicao();
			expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
		}

		return expressao; // esquerda ou Binario
	}

	/**
	 * adicao → multiplication ( ( "-" | "+" ) multiplication )*
	 * 
	 * @return
	 */
	private Expressao adicao() {
		Expressao expressao = multiplicacao(); // esquerda

		while (match(MENOS, MAIS)) {
			Token operador = previous();
			Expressao direita = multiplicacao();
			expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
		}

		return expressao; // esquerda ou Binario
	}

	/**
	 * multiplicacao → unario ( ( "/" | "*" ) unario)*
	 * 
	 * @return
	 */
	private Expressao multiplicacao() {
		Expressao expressao = unario(); // esquerda

		while (match(BARRA, ASTERISCO)) {
			Token operador = previous();
			Expressao direita = unario();
			expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
		}

		return expressao; // esquerda ou Binario
	}

	/**
	 * unario → ( "nao" | "-" ) unario | primario
	 * 
	 * @return
	 */
	private Expressao unario() {
		if (match(NAO, MENOS)) {
			Token operador = previous();
			Expressao direita = unario();
			return new Expressao.Unario(operador.line, operador, direita);
		}
		return primario();
	}

	/**
	 * primario → INTEIRO | REAL | CADEIA | CARACTERE | VERDADEIRO | FALSO |
	 * variavel | expParentizada
	 * 
	 * @return
	 */
	private Expressao primario() {
		if (match(IDENTIFICADOR)) {
			Token identificador = previous();
			if (match(ESQ_COLCHETE)) {
				Expressao index = ou();
				consume(DIR_COLCHETE, "Esperado ]");
				return new Expressao.VariavelArray(identificador.line, identificador, index);
			}
			return new Expressao.Variavel(identificador.line, identificador);
		}
		if (match(FALSO))
			return new Expressao.Literal(previous().line,false);
		if (match(VERDADEIRO))
			return new Expressao.Literal(previous().line,true);

		if (match(INTEIRO, REAL, CADEIA, CARACTERE)) {
			return new Expressao.Literal(previous().line,previous().literal);
		}

		if (match(ESQ_PARENTESES)) {
			return expParentizada();
		}
		throw error(peek(), "Esperado expressao.");
	}

	/**
	 * TIPO_DADO → "inteiro" | "real" | "cadeia" | "caractere" | "logico"
	 * 
	 * @return
	 */
	private Token tipoDado() {
		if (match(TIPO_INTEIRO, TIPO_CADEIA, TIPO_CARACTERE, TIPO_LOGICO, TIPO_REAL, TIPO_MODULO)) {
			return previous();
		}
		throw error(peek(), "Tipo inválido.");
	}

	/**
	 * se → "se" igualdade "entao" bloco ("senao" bloco)*
	 * 
	 * @return
	 */
	private Declaracao seDeclaracao() {
		Expressao condicao = ou();
		Token inicio = consume(ENTAO, "Esperado 'entao' depois da expressao.");
		consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
		Declaracao.Bloco entaoBloco = new Declaracao.Bloco(previous().line,bloco());
		Declaracao.Bloco senaoBloco = null;
		if (match(SENAO)) {
			consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
			senaoBloco = new Declaracao.Bloco(previous().line,bloco());
		}
		return new Declaracao.Se(inicio.line,condicao, entaoBloco, senaoBloco);

	}

	/**
	 * enquanto → "enquanto" ou "faca" bloco
	 * 
	 * @return
	 */
	private Declaracao enquantoDeclaracao() {
		Expressao condicao = ou();
		Token inicio = consume(FACA, "Esperado 'faca' depois da expressao.");
		consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
		Declaracao.Bloco corpo = new Declaracao.Bloco(previous().line,bloco());
		return new Declaracao.Enquanto(inicio.line,condicao, corpo);
	}

	/**
	 * para → "para" IDENTIFICADOR "de" INTEGER "ate" INTEGER "passo" INTEGER "faca"
	 * bloco
	 * 
	 * @return
	 */
	private Declaracao paraDeclaracao() {

		Token identificador = consume(IDENTIFICADOR, "Esperado 'identificador' depois da expressao.");
		consume(DE, "Esperado 'de' depois da expressao.");
		Expressao de = adicao();
		int linhaOperador = consume(ATE, "Esperado 'ate' depois da expressao.").line;
		Expressao ate = adicao();
		consume(PASSO, "Esperado 'passo' depois da expressao.");
		Expressao passo = adicao();
		consume(FACA, "Esperado 'faca' depois da expressao.");
		consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
		Declaracao.Bloco corpo = new Declaracao.Bloco(identificador.line,bloco());

		Expressao.Variavel variavel = new Expressao.Variavel(identificador.line,identificador);

		// atribuicao
		Expressao.Atribuicao atribuicaoExpressao = new Expressao.Atribuicao(identificador.line,identificador, de);

		// condicao
		Expressao.Binario condicao = new Expressao.Binario(identificador.line,variavel, new Token(MENOR_IGUAL, "<=", null, linhaOperador),
				ate);

		// incremento
		Expressao.Binario operacaoIncremento = new Expressao.Binario(identificador.line,variavel,
				new Token(MAIS, "+", null, linhaOperador), passo);
		Expressao.Atribuicao incrementoExpressao = new Expressao.Atribuicao(identificador.line,identificador, operacaoIncremento);

		return new Declaracao.Para(identificador.line,atribuicaoExpressao, condicao, incrementoExpressao, corpo);

	}

	/**
	 * repita → "repita" bloco "ate" ou ";"
	 * 
	 * @return
	 */
	private Declaracao repitaDeclaracao() {
		Token inicio = previous();
		consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
		Declaracao.Bloco corpo = new Declaracao.Bloco(previous().line,bloco());
		consume(ATE, "Esperado 'ate' depois da expressão.");
		Expressao condicao = ou();
		consume(PONTO_VIRGULA, "Esperado ';' depois do valor.");

		return new Declaracao.Repita(inicio.line,corpo, condicao);

	}

	/**
	 * varArray → IDENTIFICADOR : "vetor" "[" INTEIRO ".." INTEIRO "]" "de"
	 * TIPO_DADO
	 * 
	 * @return
	 */
	private Declaracao declaracaoVariavelArray(Token nome, Token intervaloI, Token intervaloF, Token tipo) {

		return new Declaracao.VariavelArray(nome.line, nome, new Expressao.Literal(nome.line, intervaloI.literal),
				new Expressao.Literal(nome.line, intervaloF.literal), tipo);
	}

	/**
	 * declaracaoModulo → "modulo" IDENTIFICADOR bloco
	 * 
	 * @return
	 */
	private Declaracao declaracaoModulo() {
		Token inicio = previous();
		consume(TIPO_MODULO, "Esperado 'modulo'");
		Token nome = consume(IDENTIFICADOR, "Esperado nome do módulo");
		consume(ESQ_CHAVES, "Esperado {");
		Declaracao.Bloco corpo = new Declaracao.Bloco(previous().line,bloco());

		return new Declaracao.Modulo(inicio.line,nome, corpo);

	}

	/**
	 * chamadaModulo → IDENTIFICADOR ";"
	 * 
	 * @return
	 */
	private Declaracao chamadaModulo(Token nome) {
		return new Declaracao.ChamadaModulo(nome.line, nome);
	}
}

package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.Principal;
import model.Token;
import model.TokenType;

import static model.TokenType.*;

import tree.Declaracao;
import tree.Expressao;
import tree.Declaracao.Bloco;
import tree.Expressao.Binario;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
	this.tokens = tokens;
    }

    public List<Declaracao> parse() {
	List<Declaracao> declaracoes = new ArrayList<>();
	consume(VARIAVEIS, "Esperado \"variaveis\"");
	while (!isAtEnd() && peek().type != INICIO) {
	    declaracoes.add(declaracaoVariaveis());
	}
	consume(INICIO, "Esperado \"inicio\"");

	while (!isAtEnd() && peek().type != FIM) {
	    declaracoes.add(declaracao());
	}
	consume(FIM, "Esperado \"fim\"");

	return declaracoes;
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
		return;
	    }

	    advance();
	}
    }

    private ParserError error(Token token, String message) {
	Principal.error(token, message);
	return new ParserError();
    }

    // REGRAS:
    /**
     * declaracaoVariaveis → IDENTIFICADOR ("," IDENTIFICADOR) ":" TIPO_DADO ";"
     * 
     * @return
     */
    private Declaracao declaracaoVariaveis() {
	// TODO: aceitar x,y,z: inteiro;
	Token nome = consume(IDENTIFICADOR, "Esperado nome da variavel.");
//		Expressao inicializador = null;
	consume(DOIS_PONTOS, "Esperado ':' ");

	Token tipo = tipoDado();

	consume(PONTO_VIRGULA, "Esperado ;");

	return new Declaracao.Var(nome, tipo);
    }

    /**
     * declaracao → expressaoDeclarativa | escrever | ler | bloco
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
		return printDeclaracao();
	    if (match(LER))
		return lerDeclaracao();
	    if (match(ESQ_CHAVES))
		return new Declaracao.Bloco(bloco());
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
    private Declaracao printDeclaracao() {
	Expressao expressao = expressao();
	consume(PONTO_VIRGULA, "Esperado ';' depois do valor.");
	return new Declaracao.Print(expressao);
    }

    /**
     * ler → "ler" ????????? ";"
     * 
     * @return
     */
    private Declaracao lerDeclaracao() {
	Expressao expressao = expressao();
	consume(PONTO_VIRGULA, "Esperado ';' depois do valor.");
	return new Declaracao.Ler(expressao);
    }

    /**
     * expressaoDeclarativa → expressao ";"
     * 
     * @return
     */
    private Declaracao expressaoDeclaracao() {
	Expressao expressao = expressao();
	consume(PONTO_VIRGULA, "Esperado ';' depois do valor.");
	return new Declaracao.Expressao(expressao);
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
		return new Expressao.Atribuicao(nome, valor);
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
	    expressao = new Expressao.Logico(expressao, operador, direita);
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
	    expressao = new Expressao.Logico(expressao, operador, direita);
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
	    expressao = new Expressao.Binario(expressao, operador, direita);
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
	    expressao = new Expressao.Binario(expressao, operador, direita);
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
	    expressao = new Expressao.Binario(expressao, operador, direita);
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
	    expressao = new Expressao.Binario(expressao, operador, direita);
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
	    return new Expressao.Unario(operador, direita);
	}
	return primario();
    }

    /**
     * primario → INTEIRO | REAL | CADEIA | CARACTERE | VERDADEIRO | FALSO |
     * "("expressao ")" | IDENTIFICADOR
     * 
     * @return
     */
    private Expressao primario() {
	if (match(IDENTIFICADOR)) {
	    return new Expressao.Variavel(previous());
	}
	if (match(FALSO))
	    return new Expressao.Literal(false);
	if (match(VERDADEIRO))
	    return new Expressao.Literal(true);

	if (match(INTEIRO, REAL, CADEIA, CARACTERE)) {
	    return new Expressao.Literal(previous().literal);
	}

	if (match(ESQ_PARENTESES)) {
	    Expressao expresao = expressao();
	    consume(DIR_PARENTESES, "Esperado ')' depois da expressao.");
	    return new Expressao.Grupo(expresao);
	}
	throw error(peek(), "Esperado expressao.");
    }

    /**
     * TIPO_DADO → "inteiro" | "real" | "cadeia" | "caractere" | "logico" | "vetor["
     * INTEIRO "..."I NTEIRO "] de" TIPO_DADO
     * 
     * @return
     */
    private Token tipoDado() {
	if (match(TIPO_INTEIRO, TIPO_CADEIA, TIPO_CARACTERE, TIPO_LOGICO, TIPO_REAL, TIPO_VETOR)) {
	
	    return previous();
	}
	throw error(peek(), "TIPO nao informado.");
    }

    /**
     * se → "se" igualdade "entao" bloco ("senao" bloco)*
     * 
     * @return
     */
    private Declaracao seDeclaracao() {
	Expressao condicao = ou();
	consume(ENTAO, "Esperado 'entao' depois da expressao.");
	consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
	Declaracao.Bloco entaoBloco = new Declaracao.Bloco(bloco());
	Declaracao.Bloco senaoBloco = null;
	if (match(SENAO)) {
	    consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
	    senaoBloco = new Declaracao.Bloco(bloco());
	}
	return new Declaracao.Se(condicao, entaoBloco, senaoBloco);

    }

    /**
     * enquanto → "enquanto" ou "faca" bloco
     * 
     * @return
     */
    private Declaracao enquantoDeclaracao() {
	Expressao condicao = ou();
	consume(FACA, "Esperado 'faca' depois da expressao.");
	consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
	Declaracao.Bloco corpo = new Declaracao.Bloco(bloco());
	return new Declaracao.Enquanto(condicao, corpo);
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
	Token deInteiro = consume(INTEIRO, "Esperado 'INTEIRO' depois da expressao.");
	int linhaOperador = consume(ATE, "Esperado 'ate' depois da expressao.").line;
	Token ateInteiro = consume(INTEIRO, "Esperado 'INTEIRO' depois da expressao.");
	consume(PASSO, "Esperado 'passo' depois da expressao.");
	Token passoInteiro = consume(INTEIRO, "Esperado 'INTEIRO' depois da expressao.");
	consume(FACA, "Esperado 'faca' depois da expressao.");
	consume(ESQ_CHAVES, "Esperado '{' depois da expressao.");
	Declaracao.Bloco corpo = new Declaracao.Bloco(bloco());

	Expressao.Variavel variavel = new Expressao.Variavel(identificador);

	// atribuicao
	Expressao.Literal valorAtribuicao = new Expressao.Literal(passoInteiro.literal);
	Expressao.Atribuicao atribuicaoExpressao = new Expressao.Atribuicao(identificador, valorAtribuicao);
//		Declaracao.Expressao atribuicaoDeclaracao = new Declaracao.Expressao(atribuicaoExpressao);

	// condicao
//		Expressao.Literal deValor = new Expressao.Literal(deInteiro.literal);
	Expressao.Literal ateValor = new Expressao.Literal(ateInteiro.literal);
	Token operadorC = new Token(MENOR_IGUAL, "<=", null, linhaOperador);
	Expressao.Binario condicao = new Expressao.Binario(variavel, operadorC, ateValor);

	// incremento
	Expressao.Literal incrementoValor = new Expressao.Literal(1);
	Token operadorI = new Token(MAIS, "+", null, linhaOperador);
	Expressao.Binario valorIncremento = new Expressao.Binario(variavel, operadorI, incrementoValor);
	Expressao.Atribuicao incrementoExpressao = new Expressao.Atribuicao(identificador, valorIncremento);
//		Declaracao.Expressao incrementoDeclaracao = new Declaracao.Expressao(incrementoExpressao);

	// while aux
//		Declaracao.Bloco corpoIncremento = new Declaracao.Bloco(Arrays.asList(corpo, incrementoDeclaracao));
//		Declaracao.Enquanto enquanto = new Declaracao.Enquanto(condicao, corpoIncremento);

//		Declaracao retorno = new Declaracao.Bloco(Arrays.asList(atribuicaoDeclaracao, enquanto));
	return new Declaracao.Para(atribuicaoExpressao, condicao, incrementoExpressao, corpo);
//		return retorno;

    }
}

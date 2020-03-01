package scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Principal;
import model.Token;
import model.TokenType;

import static model.TokenType.*;

public class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private static final Map<String, TokenType> keywords;

	static {
		keywords = new HashMap<>();
		keywords.put("e", E);
		keywords.put("ou", OU);
		keywords.put("nao", NAO);
		keywords.put("verdadeiro", VERDADEIRO);
		keywords.put("falso", FALSO);
		keywords.put("se", SE);
		keywords.put("entao", ENTAO);
		keywords.put("caso", CASO);
		keywords.put("senao", SENAO);
		keywords.put("faca", FACA);
		keywords.put("enquanto", ENQUANTO);
		keywords.put("repita", REPITA);
		keywords.put("para", PARA);
		keywords.put("de", DE);
		keywords.put("ate", ATE);
		keywords.put("passo", PASSO);
		keywords.put("escrever", ESCREVER);
		keywords.put("ler", LER);
		keywords.put("variaveis", VARIAVEIS);
		keywords.put("inicio", INICIO);
		keywords.put("fim", FIM);
		keywords.put("inteiro", TIPO_INTEIRO);
		keywords.put("real", TIPO_REAL);
		keywords.put("logico", TIPO_LOGICO);
		keywords.put("cadeia", TIPO_CADEIA);
		keywords.put("caractere", TIPO_CARACTERE);
		keywords.put("..", INTERVALO);
		keywords.put("modulo", TIPO_MODULO);
		keywords.put("vetor", TIPO_VETOR);
	}

	private int comeco = 0;
	private int atual = 0;
	private int linha = 1;

	public Scanner(String source) {
		this.source = source;
	}

	public List<Token> scanTokens() {
		while (!isFinal()) {
			// comeco do proximo lexeme
			comeco = atual;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, linha));
		return tokens;
	}

	private boolean isFinal() {
		return atual >= source.length();
	}

	private void scanToken() {
		char c = avancar();
		switch (c) {
			case '(' :
				addToken(ESQ_PARENTESES);
				break;
			case ')' :
				addToken(DIR_PARENTESES);
				break;
			case '[' :
				addToken(ESQ_COLCHETE);
				break;
			case ']' :
				addToken(DIR_COLCHETE);
				break;
			case '{' :
				addToken(ESQ_CHAVES);
				break;
			case '}' :
				addToken(DIR_CHAVES);
				break;
			case ',' :
				addToken(VIRGULA);
				break;
			case '.' :
				if (comparar('.')) {
					addToken(INTERVALO);
				} else {
					addToken(PONTO);
				}
				break;
			case '-' :
				addToken(MENOS);
				break;
			case '+' :
				addToken(MAIS);
				break;
			case ';' :
				addToken(PONTO_VIRGULA);
				break;
			case '*' :
				addToken(ASTERISCO);
				break;
			case ':' :
				addToken(DOIS_PONTOS);
				break;

			case '=' :
				addToken(IGUAL);
				break;
			case '<' :
				if (comparar('=')) {
					addToken(MENOR_IGUAL);
				} else if (comparar('-')) {
					addToken(ATRIBUICAO);
				} else if (comparar('>')) {
					addToken(DIFERENTE);
				} else {
					addToken(MENOR_QUE);
				}

				break;
			case '>' :
				addToken(comparar('=') ? MAIOR_IQUAL : MAIOR_QUE);
				break;
			// comentario:
			case '/' :
				if (comparar('/')) {
					// comentario termina no fim da linha.
					while (checar() != '\n' && !isFinal())
						avancar();
				} else {
					addToken(BARRA);
				}
				break;
			// inuteis:
			case ' ' :
			case '\r' :
			case '\t' :
				// ignora espaco em branco
				break;
			case '\n' :
				linha++;
				break;
			case '"' :
				cadeia();
				break;
			default :
				if (isNumerico(c)) {
					numero();
				} else if (isLetra(c)) {
					identificador();
				} else {
					Principal.error(linha, "caractere não identificado.");
				}
				break;
		}
	}

	private boolean isNumerico(char c) {
		return c >= '0' && c <= '9';
	}

	private void identificador() {
		while (isLetraOuNumero(checar()))
			avancar();
		String text = source.substring(comeco, atual);

		TokenType type = keywords.get(text);
		if (type == null)
			type = IDENTIFICADOR;
		addToken(type);
	}

	private boolean isLetra(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isLetraOuNumero(char c) {
		return isLetra(c) || isNumerico(c);
	}

	private void numero() {
		while (isNumerico(checar()))
			avancar();

		// Procura pela parte fracionada
		if (checar() == '.' && isNumerico(checkProximo())) {
			// consome "."
			avancar();

			while (isNumerico(checar()))
				avancar();
		}
		String valorString = source.substring(comeco, atual);
		if (valorString.contains(".")) {
			addToken(REAL, Double.parseDouble(valorString));
		} else {
			addToken(INTEIRO, Integer.parseInt(valorString));
		}
	}

	private char checkProximo() {
		if (atual + 1 >= source.length())
			return '\0';
		return source.charAt(atual + 1);
	}

	private void cadeia() {
		// " ou ' ??
		while (checar() != '"' && !isFinal()) {
			if (checar() == '\n')
				linha++; // suporte para cadeia multi-line
			avancar();
		}

		// Unterminated string.
		if (isFinal()) {
			Principal.error(linha, "cadeia não determinada.");
			return;
		}

		// The closing ".
		avancar();

		// Trim the surrounding quotes.
		String value = source.substring(comeco + 1, atual - 1);
		if (value.length() == 1) {
			addToken(CARACTERE, value);
		} else {
			addToken(CADEIA, value);
		}
	}

	private char checar() {
		if (isFinal())
			return '\0';
		return source.charAt(atual);
	}

	private boolean comparar(char esperado) {
		if (checar() != esperado)
			return false;

		atual++;
		return true;
	}

	private char avancar() {
		atual++;
		return source.charAt(atual - 1);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String text = source.substring(comeco, atual);
		tokens.add(new Token(type, text, literal, linha));
	}
}

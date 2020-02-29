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
	keywords.put("vetor", TIPO_VETOR);
    }

    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
	this.source = source;
    }

    public List<Token> scanTokens() {
	while (!isAtEnd()) {
	    // We are at the beginning of the next lexeme.
	    start = current;
	    scanToken();
	}

	tokens.add(new Token(EOF, "", null, line));
	return tokens;
    }

    private boolean isAtEnd() {
	return current >= source.length();
    }

    private void scanToken() {
	char c = advance();
	switch (c) {
	case '(':
	    addToken(ESQ_PARENTESES);
	    break;
	case ')':
	    addToken(DIR_PARENTESES);
	    break;
	case '[':
	    addToken(ESQ_COLCHETE);
	    break;
	case ']':
	    addToken(DIR_COLCHETE);
	    break;
	case '{':
	    addToken(ESQ_CHAVES);
	    break;
	case '}':
	    addToken(DIR_CHAVES);
	    break;
	case ',':
	    addToken(VIRGULA);
	    break;
	case '.':
	    if(match('.')) {
		addToken(INTERVALO);
	    } else {		
		addToken(PONTO);
	    }
	    break;
	case '-':
	    addToken(MENOS);
	    break;
	case '+':
	    addToken(MAIS);
	    break;
	case ';':
	    addToken(PONTO_VIRGULA);
	    break;
	case '*':
	    addToken(ASTERISCO);
	    break;
	case ':':
	    addToken(DOIS_PONTOS);
	    break;

	case '=':
	    addToken(IGUAL);
	    break;
	case '<':
	    if (match('=')) {
		addToken(MENOR_IGUAL);
	    } else if (match('-')) {
		addToken(ATRIBUICAO);
	    } else if (match('>')) {
		addToken(DIFERENTE);
	    } else {
		addToken(MENOR_QUE);
	    }

	    break;
	case '>':
	    addToken(match('=') ? MAIOR_IQUAL : MAIOR_QUE);
	    break;
	// comments:
	case '/':
	    if (match('/')) {
		// A comment goes until the end of the line.
		while (peek() != '\n' && !isAtEnd())
		    advance();
	    } else {
		addToken(BARRA);
	    }
	    break;
	// inuteis:
	case ' ':
	case '\r':
	case '\t':
	    // Ignore whitespace.
	    break;

	case '\n':
	    line++;
	    break;
	case '"':
	    string();
	    break;
	default:
	    if (isDigit(c)) {
		number();
	    } else if (isAlpha(c)) {
		identifier();
	    } else {
		Principal.error(line, "caracter não identificado.");
	    }
	    break;
	}
    }

    private boolean isDigit(char c) {
	return c >= '0' && c <= '9';
    }

    private void identifier() {
	while (isAlphaNumeric(peek()))
	    advance();
	String text = source.substring(start, current);

	TokenType type = keywords.get(text);
	if (type == null)
	    type = IDENTIFICADOR;
	addToken(type);
    }

    private boolean isAlpha(char c) {
	return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
	return isAlpha(c) || isDigit(c);
    }

    private void number() {
	while (isDigit(peek()))
	    advance();

	// Look for a fractional part.
	if (peek() == '.' && isDigit(peekNext())) {
	    // Consume the "."
	    advance();

	    while (isDigit(peek()))
		advance();
	}
	String valorString = source.substring(start, current);
	if (valorString.contains(".")) {
	    addToken(REAL, Double.parseDouble(valorString));
	} else {
	    addToken(INTEIRO, Integer.parseInt(valorString));
	}
    }

    private char peekNext() {
	if (current + 1 >= source.length())
	    return '\0';
	return source.charAt(current + 1);
    }

    private void string() {
	// " ou ' ??
	while (peek() != '"' && !isAtEnd()) {
	    if (peek() == '\n')
		line++; // suporte para multi-line strings
	    advance();
	}

	// Unterminated string.
	if (isAtEnd()) {
	    Principal.error(line, "string não determinada.");
	    return;
	}

	// The closing ".
	advance();

	// Trim the surrounding quotes.
	String value = source.substring(start + 1, current - 1);
	if (value.length() == 1) {
	    addToken(CARACTERE, value);
	} else {
	    addToken(CADEIA, value);
	}
    }

    private char peek() {
	if (isAtEnd())
	    return '\0';
	return source.charAt(current);
    }

    private boolean match(char expected) {
	if (peek() != expected)
	    return false;

	current++;
	return true;
    }

    private char advance() {
	current++;
	return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
	addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
	String text = source.substring(start, current);
	tokens.add(new Token(type, text, literal, line));
    }
}

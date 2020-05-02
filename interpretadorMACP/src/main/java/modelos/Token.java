package modelos;

/**
 * Representação de uma palavra/simbolo valido na pseudoLinguagem
 * @author Kerlyson
 *
 */
public class Token {
	/*
	 * tipo de token
	 */
	public final TiposToken type;
	/*
	 * String da palavra/simbolo no arquivo fonte
	 */
	public final String lexeme;
	/**
	 * Possivel valor em java que a palavra/simbolo representa
	 */
	public final Object literal;
	/**
	 * linha onde o token se encontra no arquivo fonte
	 */
	public final int line;

	public Token(TiposToken type, String lexeme, Object literal, int line) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
	}

	public String toString() {
		return type + " " + lexeme + " " + literal;
	}
	

}

package modelos;

public class Token {
	public final TiposToken type;
	public final String lexeme;
	public final Object literal;
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

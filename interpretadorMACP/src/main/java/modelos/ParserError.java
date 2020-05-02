package modelos;

public class ParserError extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final Token token;
	public final String mensagem;
	public final int linha;
	
	public ParserError(Token token, String mensagem) {
		this.token = token;
		this.linha = this.token.line;
		this.mensagem = mensagem;
	}
	public ParserError(int linha, String mensagem) {
		this.token = null;
		this.linha = linha;
		this.mensagem = mensagem;
	}
	
}

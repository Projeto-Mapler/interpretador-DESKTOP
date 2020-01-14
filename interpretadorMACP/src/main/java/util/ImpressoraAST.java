package util;
import model.Token;
import model.TokenType;
import tree.Expressao;
import tree.Expressao.Atribuicao;
import tree.Expressao.Binario;
import tree.Expressao.Grupo;
import tree.Expressao.Literal;
import tree.Expressao.Logico;
import tree.Expressao.Unario;
import tree.Expressao.Variavel;
public class ImpressoraAST implements Expressao.Visitor<String> {

	@Override
	public String visitBinarioExpressao(Binario expressao) {
		return parenthesize(expressao.operador.lexeme, expressao.esquerda,
				expressao.direita);
	}

	@Override
	public String visitGrupoExpressao(Grupo expressao) {
		return parenthesize("grupo", expressao.expressao);
	}

	@Override
	public String visitLiteralExpressao(Literal expressao) {
		if (expressao.valor == null)
			return "nulo";
		return expressao.valor.toString();
	}

	@Override
	public String visitUnarioExpressao(Unario expressao) {
		return parenthesize(expressao.operador.lexeme, expressao.direira);
	}

	private String parenthesize(String nome, Expressao... expressoes) {
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(nome);
		for (Expressao expr : expressoes) {
			builder.append(" ");
			builder.append(expr.accept(this));
		}
		builder.append(")");

		return builder.toString();
	}

	public String print(Expressao expressao) {
		return expressao.accept(this);
	}
	public static void main(String[] args) {
		Expressao esquerda = new Expressao.Unario(
				new Token(TokenType.MENOS, "-", null, 1),
				new Expressao.Literal(123));
		Expressao direita = new Expressao.Grupo(new Expressao.Literal(45.67));
		Expressao expression = new Expressao.Binario(esquerda,
				new Token(TokenType.ASTERISCO, "*", null, 1), direita);

		System.out.println(new ImpressoraAST().print(expression));
	}

	@Override
	public String visitVariavelExpressao(Variavel expressao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitAtribuicaoExpressao(Atribuicao expressao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitLogicoExpressao(Logico expressao) {
		// TODO Auto-generated method stub
		return null;
	}
}

package util;
import javax.lang.model.type.DeclaredType;
import javax.swing.tree.TreeCellEditor;

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
import tree.Declaracao;
import tree.Declaracao.Bloco;
import tree.Declaracao.Enquanto;
import tree.Declaracao.Ler;
import tree.Declaracao.Para;
import tree.Declaracao.Print;
import tree.Declaracao.Se;
import tree.Declaracao.Var;
public class ImpressoraAST implements Expressao.Visitor<String>,  Declaracao.Visitor<String>{

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
	public void printASTTree() {}

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

	@Override
	public String visitBlocoDeclaracao(Bloco declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitExpressaoDeclaracao(
			tree.Declaracao.Expressao declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitPrintDeclaracao(Print declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitSeDeclaracao(Se declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitLerDeclaracao(Ler declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitVarDeclaracao(Var declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitParaDeclaracao(Para declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitEnquantoDeclaracao(Enquanto declaracao) {
		// TODO Auto-generated method stub
		return null;
	}
}

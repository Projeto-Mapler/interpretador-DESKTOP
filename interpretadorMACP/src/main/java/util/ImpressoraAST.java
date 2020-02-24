package util;
import java.util.List;

import javax.lang.model.type.DeclaredType;
import javax.swing.tree.TreeCellEditor;

import main.Principal;
import model.Token;
import model.TokenType;
import parser.RuntimeError;
import tree.Expressao;
import tree.Expressao.Atribuicao;
import tree.Expressao.AtribuicaoArray;
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
import tree.Declaracao.Programa;
import tree.Declaracao.Se;
import tree.Declaracao.Var;
import tree.Declaracao.VariavelArray;
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
		if(expressao.valor instanceof Boolean) {
			if((boolean)expressao.valor) {
				return "verdadeiro";
			} else {
				return "falso";
			}
		}
		if(expressao.valor instanceof String) {
			return "\""+expressao.valor.toString()+"\"";
		}
		return expressao.valor.toString();
	}

	@Override
	public String visitUnarioExpressao(Unario expressao) {
		return parenthesize(expressao.operador.lexeme, expressao.direira);
	}

	private String parenthesize(String nome, Object... expressoes) {
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(nome);
		if(expressoes != null) {
			
			for (Object expr : expressoes) {
				if(expr != null) {
					builder.append(" ");	
					if(expr instanceof Expressao) {						
						builder.append(((Expressao)expr).accept(this));
					} else if(expr instanceof Expressao) {						
						builder.append(((Declaracao)expr).accept(this));
					}
					// THROW ERROR Expressao ou Expressao nao encontrado
				}
			}
		}
		builder.append(")");

		return builder.toString();
	}

	public String print(Expressao expressao) {
		String retorno = expressao.accept(this);
//		System.out.println(retorno);
		return retorno;
	}
	public String print(Declaracao declaracao) {
		String retorno = declaracao.accept(this);
//		System.out.println(retorno);
		return retorno;
	}
	public void print(List<Declaracao> declaracoes) {
		StringBuilder builder = new StringBuilder();
		try {
			for (Declaracao declaracao : declaracoes) {
				builder.append(print(declaracao));
			}
		} catch (RuntimeError error) {
			Principal.runtimeError(error);
		}
		System.out.println( builder.toString());
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
		return expressao.nome.lexeme;
	}

	@Override
	public String visitAtribuicaoExpressao(Atribuicao expressao) {
		return parenthesize("atribuicao " + expressao.nome.lexeme, expressao.valor);
	}

	@Override
	public String visitLogicoExpressao(Logico expressao) {
		return parenthesize("lógico "+expressao.operador.lexeme, expressao.esquerda, expressao.direita);
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
		return parenthesize("escreva ", declaracao.expressao);
	}

	@Override
	public String visitSeDeclaracao(Se declaracao) {
		return parenthesize("se ", declaracao.condicao, declaracao.entaoBloco, declaracao.senaoBloco);
	}

	@Override
	public String visitLerDeclaracao(Ler declaracao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitVarDeclaracao(Var declaracao) {
		return parenthesize("variavel " + declaracao.nome.lexeme, declaracao.tipo.lexeme);
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

	@Override
	public String visitProgramaDeclaracao(Programa declaracao) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public String visitVariavelArrayDeclaracao(VariavelArray declaracao) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public String visitAtribuicaoArrayExpressao(AtribuicaoArray expressao) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public String visitVariavelArrayExpressao(tree.Expressao.VariavelArray expressao) {
	    // TODO Auto-generated method stub
	    return null;
	}
}

package conversores;

import java.util.List;

import debug.GerenciadorEventos;
import modelos.RuntimeError;
import modelos.TiposToken;
import modelos.Token;
import modelos.VariavelVetor;
import tree.Declaracao;
import tree.Declaracao.Bloco;
import tree.Declaracao.ChamadaModulo;
import tree.Declaracao.Enquanto;
import tree.Declaracao.Escreva;
import tree.Declaracao.Ler;
import tree.Declaracao.Modulo;
import tree.Declaracao.Para;
import tree.Declaracao.Programa;
import tree.Declaracao.Repita;
import tree.Declaracao.Se;
import tree.Declaracao.Var;
import tree.Declaracao.VarDeclaracoes;
import tree.Declaracao.VariavelArray;
import tree.Expressao;
import tree.Expressao.Atribuicao;
import tree.Expressao.AtribuicaoArray;
import tree.Expressao.Binario;
import tree.Expressao.ExpParentizada;
import tree.Expressao.Grupo;
import tree.Expressao.Literal;
import tree.Expressao.Logico;
import tree.Expressao.Unario;
import tree.Expressao.Variavel;

/**
 * Converte pseudoCodigo para C++
 * 
 * @author Kerlyson
 *
 */
public class ConversorCpp extends Conversor implements Expressao.Visitor<Void>, Declaracao.Visitor<Void> {

    public ConversorCpp(Declaracao.Programa programa, GerenciadorEventos gerenciadorEventos) {
	super(programa, gerenciadorEventos);
    }

    private void evaluate(Expressao expressao) {
	expressao.accept(this);
    }

    private void execute(Declaracao declaracao) {
	declaracao.accept(this);
    }

    @Override
    protected String getOperadorLogico(TiposToken op) {
	switch (op) {
	case OU:
	    return "||";
	case E:
	    return "&&";
	case NAO:
	    return "!";

	default:
	    return "";
	}
    }

    @Override
    protected String tipoVariavel(TiposToken tipo) {
	switch (tipo) {
	case TIPO_CADEIA:
	    return "string";
	case TIPO_INTEIRO:
	    return "int";
	case TIPO_REAL:
	    return "double";
	case TIPO_CARACTERE:
	    return "char";
	// case TIPO_VETOR : return "arrya";
	case TIPO_LOGICO:
	    return "bool";
	default:
	    // throw error?
	    return "";
	}
    }

    @Override
    public String converter() {
	try {
	    visitProgramaDeclaracao(programa);
	    String programa = escritor.getResultado();

	    if (programa.length() > 0) {
		return programa;
	    }
	} catch (RuntimeError error) {
	    super.throwRuntimeErro(error);
	} finally {
	    escritor.reset();
	}
	return null;
    }

    @Override
    public Void visitBlocoDeclaracao(Bloco declaracao) {
	for (Declaracao statement : declaracao.declaracoes) {
	    execute(statement);
	}
	return null;
    }

    @Override
    public Void visitExpressaoDeclaracao(tree.Declaracao.Expressao declaracao) {
	escritor.concatenarNaLinha("");
	evaluate(declaracao.expressao);
	escritor.concatenarNaLinha(";").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitEscrevaDeclaracao(Escreva declaracao) {
	escritor.concatenarNaLinha("cout << ");
	List<tree.Expressao> expressoes = declaracao.expressoes;
	for (int i = 0; i < expressoes.size(); i++) {
	    evaluate(expressoes.get(i));
	    if (i < (expressoes.size() - 1)) {
		escritor.concatenarNaLinha(" << ");
	    }
	}
	escritor.concatenarNaLinha(";").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitSeDeclaracao(Se declaracao) {
	escritor.concatenarNaLinha("if (");
	evaluate(declaracao.condicao);
	escritor.concatenarNaLinha(") {").indentar().addQuebraLinha();
	execute(declaracao.entaoBloco);
	escritor.removerIdentacao();
	if (declaracao.senaoBloco == null) {
	    escritor.concatenarNaLinha("}").addQuebraLinha();
	    return null;
	}
	escritor.concatenarNaLinha("} else {").indentar().addQuebraLinha();
	execute(declaracao.senaoBloco);
	escritor.removerIdentacao().concatenarNaLinha("}").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitLerDeclaracao(Ler declaracao) {
	Expressao atribuicao = declaracao.atribuicao;
	if (atribuicao instanceof Expressao.Atribuicao) {
	    String lexeme = ((Expressao.Atribuicao) atribuicao).nome.lexeme;
	    escritor.concatenarNaLinha("cin >> " + lexeme + ";").addQuebraLinha();

	}
	if (atribuicao instanceof Expressao.AtribuicaoArray) {
	    String lexeme = ((Expressao.AtribuicaoArray) atribuicao).nome.lexeme;
	    escritor.concatenarNaLinha("cin >> " + lexeme + "[");
	    evaluate(((Expressao.AtribuicaoArray) atribuicao).index);

	    escritor.concatenarNaLinha("];").addQuebraLinha();
	}
	return null;
    }

    @Override
    public Void visitVarDeclaracao(Var declaracao) {
	if (declaracao.tipo.type == TiposToken.TIPO_MODULO) {
	    return null;
	}
	String tipo = this.tipoVariavel(declaracao.tipo.type);
	escritor.concatenarNaLinha(tipo + " " + declaracao.nome.lexeme + ";").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitVarDeclaracoesDeclaracao(VarDeclaracoes declaracao) {
	if (declaracao.variaveis.size() <= 1) {
	    execute(declaracao.variaveis.get(0));
	    return null;
	}
	List<Declaracao> lista = declaracao.variaveis;
	Declaracao primeiro = lista.get(0);

	if (primeiro instanceof Declaracao.Var) {
	    Token tipoVar = ((Declaracao.Var) primeiro).tipo;
	    if (tipoVar.type == TiposToken.TIPO_MODULO) {
		return null;
	    }
	    String tipo = this.tipoVariavel(tipoVar.type);
	    escritor.concatenarNaLinha(tipo + " ");

	    for (int i = 0; i < lista.size(); i++) {
		Declaracao.Var varriavel = (Declaracao.Var) lista.get(i);
		escritor.concatenarNaLinha(varriavel.nome.lexeme);
		if (i < lista.size() - 1) {
		    escritor.concatenarNaLinha(", ");
		}
	    }

	    escritor.concatenarNaLinha(";").addQuebraLinha();
	} else if (primeiro instanceof Declaracao.VariavelArray) {
	    Token tipoVar = ((Declaracao.VariavelArray) primeiro).tipo;
	    String tipo = this.tipoVariavel(tipoVar.type);

	    escritor.concatenarNaLinha(tipo + " ");

	    for (int i = 0; i < lista.size(); i++) {
		Declaracao.VariavelArray varriavel = (Declaracao.VariavelArray) lista.get(i);
		VariavelVetor vv = new VariavelVetor(
						     varriavel.tipo.type,
						     (int) ((Expressao.Literal) varriavel.intervaloI).valor,
						     (int) ((Expressao.Literal) varriavel.intervaloF).valor);
		addVariavelVetor(varriavel.nome.lexeme, vv);
		escritor.concatenarNaLinha(varriavel.nome.lexeme + "[" + vv.getTamanho() + "]");
		if (i < lista.size() - 1) {
		    escritor.concatenarNaLinha(", ");
		}
	    }
	    escritor.concatenarNaLinha(";").addQuebraLinha();

	}

	return null;
    }

    @Override
    public Void visitVariavelArrayDeclaracao(VariavelArray declaracao) {
	String tipo = this.tipoVariavel(declaracao.tipo.type);
	VariavelVetor vv = new VariavelVetor(
					     declaracao.tipo.type,
					     (int) ((Expressao.Literal) declaracao.intervaloI).valor,
					     (int) ((Expressao.Literal) declaracao.intervaloF).valor);

	escritor.concatenarNaLinha(tipo + " " + declaracao.nome.lexeme + "[" + vv.getTamanho() + "];").addQuebraLinha();
	addVariavelVetor(declaracao.nome.lexeme, vv);
	return null;
    }

    @Override
    public Void visitParaDeclaracao(Para declaracao) {
	escritor.concatenarNaLinha("for (");
	evaluate(declaracao.atribuicao);
	escritor.concatenarNaLinha("; ");
	evaluate(declaracao.condicao);
	escritor.concatenarNaLinha("; ");
	evaluate(declaracao.incremento);
	escritor.concatenarNaLinha(") {").indentar().addQuebraLinha();
	execute(declaracao.facaBloco);
	escritor.removerIdentacao().concatenarNaLinha("}").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitEnquantoDeclaracao(Enquanto declaracao) {
	escritor.concatenarNaLinha("while (");
	evaluate(declaracao.condicao);
	escritor.concatenarNaLinha(") {").addQuebraLinha().indentar();
	execute(declaracao.corpo);
	escritor.removerIdentacao().concatenarNaLinha("}").addQuebraLinha();

	return null;
    }

    @Override
    public Void visitRepitaDeclaracao(Repita declaracao) {
	escritor.concatenarNaLinha("do {").indentar().addQuebraLinha();
	execute(declaracao.corpo);
	escritor.removerIdentacao().concatenarNaLinha("while (");
	evaluate(declaracao.condicao);
	escritor.concatenarNaLinha(");").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitModuloDeclaracao(Modulo declaracao) {
	escritor.concatenarNaLinha("void " + declaracao.nome.lexeme + " () { ").indentar().addQuebraLinha();
	execute(declaracao.corpo);
	escritor.removerIdentacao().concatenarNaLinha("}").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitChamadaModuloDeclaracao(ChamadaModulo declaracao) {
	escritor.concatenarNaLinha(declaracao.identificador.lexeme + "();").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitProgramaDeclaracao(Programa declaracao) {

	escritor
		.concatenarNaLinha("#include <iostream>")
		.addQuebraLinha()
		.concatenarNaLinha("#include <string>")
		.addQuebraLinha(2)
		.concatenarNaLinha("using namespace std;")
		.addQuebraLinha(2);

	// converte variaveis
	for (Declaracao variaveis : declaracao.variaveis) {
	    execute(variaveis);
	}

	// converte modulos-funcoes
	for (Declaracao modulo : declaracao.modulos) {
	    execute(modulo);
	}

	escritor.concatenarNaLinha("int main() {").addQuebraLinha().indentar();

	// converte inicio-fim
	for (Declaracao corpo : declaracao.corpo) {
	    execute(corpo);
	}

	escritor.concatenarNaLinha("return 0;").addQuebraLinha().removerIdentacao().concatenarNaLinha("}"); // fim main

	return null;
    }

    @Override
    public Void visitBinarioExpressao(Binario expressao) {
	evaluate(expressao.esquerda);
	switch (expressao.operador.type) {
	case MENOS:
	    escritor.concatenarNaLinha(" - ");
	    break;
	case BARRA:
	    escritor.concatenarNaLinha(" / ");
	    break;
	case ASTERISCO:
	    escritor.concatenarNaLinha(" * ");
	    break;
	case MAIS:
	    escritor.concatenarNaLinha(" + ");
	    break;
	case MAIOR_QUE:
	    escritor.concatenarNaLinha(" > ");
	    break;
	case MAIOR_IQUAL:
	    escritor.concatenarNaLinha(" >= ");
	    break;
	case MENOR_QUE:
	    escritor.concatenarNaLinha(" < ");
	    break;
	case MENOR_IGUAL:
	    escritor.concatenarNaLinha(" <= ");
	    break;
	case DIFERENTE:
	    escritor.concatenarNaLinha(" != ");
	    break;
	case IGUAL:
	    escritor.concatenarNaLinha(" == ");
	    break;
	default:
	    break; // throw erro?
	}
	evaluate(expressao.direita);
	return null;
    }

    @Override
    public Void visitGrupoExpressao(Grupo expressao) {
	evaluate(expressao.expressao);
	return null;
    }

    @Override
    public Void visitExpParentizadaExpressao(ExpParentizada expressao) {
	escritor.concatenarNaLinha("(");
	evaluate(expressao.grupo);
	escritor.concatenarNaLinha(")");
	return null;
    }

    @Override
    public Void visitLiteralExpressao(Literal expressao) {
	Object valor = expressao.valor;
	if (valor instanceof String) {
	    valor = "\"".concat((String) valor).concat("\"");
	} else if (valor instanceof Character) {
	    valor = "'".concat(((Character) valor).toString()).concat("'");
	}
	escritor.concatenarNaLinha(valor.toString());
	return null;
    }

    @Override
    public Void visitLogicoExpressao(Logico expressao) {
	evaluate(expressao.esquerda);
	escritor.concatenarNaLinha(" " + getOperadorLogico(expressao.operador.type) + " ");

	evaluate(expressao.direita);
	return null;
    }

    @Override
    public Void visitUnarioExpressao(Unario expressao) {
	switch (expressao.operador.type) {
	case NAO:
	    escritor.concatenarNaLinha("!");
	    break;
	case MENOS:
	    escritor.concatenarNaLinha("-");
	    break;

	default:
	    break; // throw error?
	}
	evaluate(expressao.direira);
	return null;
    }

    @Override
    public Void visitAtribuicaoExpressao(Atribuicao expressao) {
	escritor.concatenarNaLinha(expressao.nome.lexeme + " = ");
	evaluate(expressao.valor);
	return null;
    }

    @Override
    public Void visitAtribuicaoArrayExpressao(AtribuicaoArray expressao) {
	escritor.concatenarNaLinha(expressao.nome.lexeme + "[");

	if (expressao.index instanceof Expressao.Literal) {

	    int valorIndex = (int) ((Expressao.Literal) expressao.index).valor;
	    escritor.concatenarNaLinha(getVariavelVetor(expressao.nome.lexeme).resolverIndex(valorIndex) + "");
	} else {
	    evaluate(expressao.index);
	}
	escritor.concatenarNaLinha("] = ");
	evaluate(expressao.valor);
	return null;
    }

    @Override
    public Void visitVariavelArrayExpressao(tree.Expressao.VariavelArray expressao) {
	String nome = expressao.nome.lexeme;
	if (expressao.index == null) {
	    escritor.concatenarNaLinha(nome);
	} else if (expressao.index instanceof tree.Expressao.Literal) {
	    escritor.concatenarNaLinha(nome + "[");
	    int i = (int) ((tree.Expressao.Literal) expressao.index).valor;
	    VariavelVetor vv = getVariavelVetor(nome);
	    escritor.concatenarNaLinha(vv.resolverIndex(i) + "");
	    escritor.concatenarNaLinha("]");
	} else {
	    escritor.concatenarNaLinha(nome + "[");

	    evaluate(expressao.index);
	    escritor.concatenarNaLinha("]");
	}

	return null;
    }

    @Override
    public Void visitVariavelExpressao(Variavel expressao) {
	escritor.concatenarNaLinha(expressao.nome.lexeme);
	return null;
    }

}

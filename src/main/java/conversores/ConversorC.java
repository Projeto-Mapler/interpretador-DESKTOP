package conversores;

import static modelos.TiposToken.MAIOR_IQUAL;
import static modelos.TiposToken.MAIOR_QUE;
import static modelos.TiposToken.MENOR_IGUAL;
import static modelos.TiposToken.MENOR_QUE;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import evento.GerenciadorEventos;
import modelos.TiposToken;
import modelos.Token;
import modelos.VariavelVetor;
import modelos.excecao.RuntimeError;
import modelos.tree.Declaracao;
import modelos.tree.Expressao;
import modelos.tree.Declaracao.Bloco;
import modelos.tree.Declaracao.ChamadaModulo;
import modelos.tree.Declaracao.Enquanto;
import modelos.tree.Declaracao.Escreva;
import modelos.tree.Declaracao.Ler;
import modelos.tree.Declaracao.Modulo;
import modelos.tree.Declaracao.Para;
import modelos.tree.Declaracao.Programa;
import modelos.tree.Declaracao.Repita;
import modelos.tree.Declaracao.Se;
import modelos.tree.Declaracao.Var;
import modelos.tree.Declaracao.VarDeclaracoes;
import modelos.tree.Declaracao.VariavelArray;
import modelos.tree.Expressao.Atribuicao;
import modelos.tree.Expressao.AtribuicaoArray;
import modelos.tree.Expressao.Binario;
import modelos.tree.Expressao.ExpParentizada;
import modelos.tree.Expressao.Grupo;
import modelos.tree.Expressao.Literal;
import modelos.tree.Expressao.Logico;
import modelos.tree.Expressao.Unario;
import modelos.tree.Expressao.Variavel;

/**
 * Converte pseudoCodigo para C
 * 
 * @author Kerlyson
 *
 */
public class ConversorC extends Conversor implements Expressao.Visitor<Void>, Declaracao.Visitor<Void> {

    private Map<String, TiposToken> variaveis = new HashMap<String, TiposToken>();// variavel, tipo

    public ConversorC(Declaracao.Programa programa, GerenciadorEventos gerenciadorEventos) {
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
	    return "char";
	case TIPO_INTEIRO:
	    return "int";
	case TIPO_REAL:
	    return "double";
	case TIPO_CARACTERE:
	    return "char";
	case TIPO_LOGICO:
	    return "bool";
	default:
	    return "";
	}
    }

    private String getEspecificadorTipo(TiposToken tipo) {
	switch (tipo) {
	case CADEIA:
	case TIPO_CADEIA:
	    return "%s";
	case INTEIRO:
	case TIPO_INTEIRO:
	    return "%d";
	case REAL:
	case TIPO_REAL:
	    return "%f";
	case CARACTERE:
	case TIPO_CARACTERE:
	    return "%c";
	case VERDADEIRO:
	case FALSO:
	case TIPO_LOGICO:
	    return "%d"; // TODO
	default:
	    return tipo.toString();
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
    public Void visitExpressaoDeclaracao(modelos.tree.Declaracao.Expressao declaracao) {
	escritor.concatenarNaLinha("");
	evaluate(declaracao.expressao);
	escritor.concatenarNaLinha(";").addQuebraLinha();
	return null;
    }

    @Override
    public Void visitEscrevaDeclaracao(Escreva declaracao) {
	escritor.concatenarNaLinha("printf(");
	List<modelos.tree.Expressao> expressoes = declaracao.expressoes;
	String especificadorLiteral = this.getEspecificadorEscreva(expressoes);
	if (especificadorLiteral != null) {
	    escritor.concatenarNaLinha("\"" + especificadorLiteral + "\", ");
	}
	for (int i = 0; i < expressoes.size(); i++) {
	    evaluate(expressoes.get(i));
	    if (i < (expressoes.size() - 1)) {
		escritor.concatenarNaLinha(", ");
	    }
	}
	escritor.concatenarNaLinha(");").addQuebraLinha();
	return null;
    }

    private String getEspecificadorEscreva(List<modelos.tree.Expressao> expressoes) {
	StringBuilder builder = new StringBuilder();
	for (int i = 0; i < expressoes.size(); i++) {
	    modelos.tree.Expressao exp = expressoes.get(i);
	    if (exp instanceof modelos.tree.Expressao.Literal) {
		Token token = ((modelos.tree.Expressao.Literal) exp).token;
		builder.append(this.getEspecificadorTipo(token.type));
	    } else if (exp instanceof modelos.tree.Expressao.Variavel) {
		Token token = ((modelos.tree.Expressao.Variavel) exp).nome;
		builder.append(this.getEspecificadorTipo(this.variaveis.get(token.lexeme)));
	    } else if (exp instanceof modelos.tree.Expressao.VariavelArray) {
		Token token = ((modelos.tree.Expressao.VariavelArray) exp).nome;
		builder.append(this.getEspecificadorTipo(this.getVariavelVetorTipo(token.lexeme)));
	    } else if (exp instanceof modelos.tree.Expressao.Logico || exp instanceof modelos.tree.Expressao.Unario) {
		builder.append("%d");
	    } else if (exp instanceof modelos.tree.Expressao.Binario) {
		Token operador = ((modelos.tree.Expressao.Binario) exp).operador;
		if (isTokenTypeIgualA(operador, MAIOR_QUE, MAIOR_IQUAL, MENOR_QUE, MENOR_IGUAL)) {
		    // Operação logica
		    builder.append("%d");
		} else {
		    // Operacao Aritimetica
		    // builder.append("%f");
		    builder.append(this.getEspecificadorBinario((modelos.tree.Expressao.Binario) exp));

		}
	    } else if (exp instanceof modelos.tree.Expressao.ExpParentizada) {
		modelos.tree.Expressao exp2 = ((modelos.tree.Expressao.ExpParentizada) exp).grupo.expressao;
		builder.append(this.getEspecificadorEscreva(Collections.singletonList(exp2)));
	    }

	}
	return builder.toString();
    }

    private String getEspecificadorBinario(modelos.tree.Expressao.Binario bin) {
	modelos.tree.Expressao expEsquerda = (modelos.tree.Expressao) bin.esquerda;
	modelos.tree.Expressao expDireita = (modelos.tree.Expressao) bin.direita;
	String esquerda = this.getEspecificadorEscreva(Collections.singletonList(expEsquerda));
	String direita = this.getEspecificadorEscreva(Collections.singletonList(expDireita));
	if (esquerda.contains("%f") || direita.contains("%f")) {
	    return "%f";
	} else {
	    return "%d";
	}
    }

    private boolean isTokenTypeIgualA(Token token, TiposToken... types) {
	for (TiposToken type : types) {
	    if (token.type == type) {
		return true;
	    }
	}

	return false;
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
	    escritor.concatenarNaLinha(getLerFuncao(lexeme) + ");").addQuebraLinha();

	}
	if (atribuicao instanceof Expressao.AtribuicaoArray) {
	    String lexeme = ((Expressao.AtribuicaoArray) atribuicao).nome.lexeme;
	    escritor.concatenarNaLinha(getLerFuncao(lexeme) + "[");
	    evaluate(((Expressao.AtribuicaoArray) atribuicao).index);

	    escritor.concatenarNaLinha("]);").addQuebraLinha();
	}
	return null;
    }

    private String getLerFuncao(String lexeme) {
	if (this.isCadeia(lexeme)) {
	    return "gets(" + lexeme;
	}
	TiposToken tipo = null;
	if (this.variaveis.get(lexeme) == TiposToken.TIPO_VETOR) {
	    tipo = this.getVariavelVetorTipo(lexeme);
	} else {
	    tipo = this.variaveis.get(lexeme);
	}
	return "scanf(\"" + this.getEspecificadorTipo(tipo) + "\", &" + lexeme;
    }

    @Override
    public Void visitVarDeclaracao(Var declaracao) {
	this.variaveis.put(declaracao.nome.lexeme, declaracao.tipo.type);
	if (declaracao.tipo.type == TiposToken.TIPO_MODULO) {
	    return null;
	}
	String tipo = this.tipoVariavel(declaracao.tipo.type);
	escritor
		.concatenarNaLinha(
				   tipo + " " + declaracao.nome.lexeme
					   + this.getTamanhoVetorTipoString(declaracao.tipo.type) + ";")
		.addQuebraLinha();
	return null;
    }

    private String getTamanhoVetorTipoString(TiposToken tipo) {
	if (tipo == TiposToken.TIPO_CADEIA) {
	    return "[100]";
	}
	return "";
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
		this.variaveis.put(varriavel.nome.lexeme, varriavel.tipo.type);
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
		//this.variaveis.put(varriavel.nome.lexeme, varriavel.tipo.type);
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
	this.variaveis.put(declaracao.nome.lexeme, declaracao.tipo.type);
	String tipo = this.tipoVariavel(declaracao.tipo.type);
	VariavelVetor vv = new VariavelVetor(
					     declaracao.tipo.type,
					     (int) ((Expressao.Literal) declaracao.intervaloI).valor,
					     (int) ((Expressao.Literal) declaracao.intervaloF).valor);

	escritor
		.concatenarNaLinha(
				   tipo + " " + declaracao.nome.lexeme + "[" + vv.getTamanho() + "]"
					   + this.isVetorCadeia(declaracao.tipo.type) + ";")
		.addQuebraLinha();
	addVariavelVetor(declaracao.nome.lexeme, vv);
	return null;
    }

    private String isVetorCadeia(TiposToken type) {
	if (type == TiposToken.TIPO_CADEIA)
	    return "[100]";
	return "";
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
		.concatenarNaLinha("#include <stdio.h>")
		.addQuebraLinha()
		.concatenarNaLinha("#include <stdbool.h>")
		.addQuebraLinha()
		.concatenarNaLinha("#include <string.h>")
		.addQuebraLinha(2);

	// converte variaveis
	for (Declaracao variaveis : declaracao.variaveis) {
	    execute(variaveis);
	}

	escritor.addQuebraLinha();

	// converte modulos-funcoes
	for (Declaracao modulo : declaracao.modulos) {
	    execute(modulo);
	}

	escritor.addQuebraLinha().concatenarNaLinha("int main() {").addQuebraLinha().indentar();

	// converte inicio-fim
	for (Declaracao corpo : declaracao.corpo) {
	    execute(corpo);
	}

	escritor.removerIdentacao().concatenarNaLinha("}"); // fim main

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
	if (this.isCadeia(expressao.nome.lexeme)) {

	    escritor.concatenarNaLinha("strcpy(" + expressao.nome.lexeme + ", ");
	    evaluate(expressao.valor);
	    escritor.concatenarNaLinha(")");

	} else {

	    escritor.concatenarNaLinha(expressao.nome.lexeme + " = ");
	    evaluate(expressao.valor);
	}
	return null;
    }

    private boolean isCadeia(String lexeme) {
	return this.variaveis.get(lexeme) == TiposToken.TIPO_CADEIA;
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
    public Void visitVariavelArrayExpressao(modelos.tree.Expressao.VariavelArray expressao) {
	String nome = expressao.nome.lexeme;
	if (expressao.index == null) {
	    escritor.concatenarNaLinha(nome);
	} else if (expressao.index instanceof modelos.tree.Expressao.Literal) {
	    escritor.concatenarNaLinha(nome + "[");
	    int i = (int) ((modelos.tree.Expressao.Literal) expressao.index).valor;
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

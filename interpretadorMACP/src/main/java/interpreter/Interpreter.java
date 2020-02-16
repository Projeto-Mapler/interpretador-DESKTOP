package interpreter;

import static model.TokenType.ASTERISCO;
import static model.TokenType.BARRA;
import static model.TokenType.MAIS;
import static model.TokenType.MENOS;
import static model.TokenType.OU;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import main.Principal;
import model.Token;
import model.TokenType;
import parser.RuntimeError;
import tree.Declaracao;
import tree.Declaracao.Bloco;
import tree.Declaracao.Enquanto;
import tree.Declaracao.Ler;
import tree.Declaracao.Para;
import tree.Declaracao.Print;
import tree.Declaracao.Programa;
import tree.Declaracao.Se;
import tree.Declaracao.Var;
import tree.Expressao;
import tree.Expressao.Atribuicao;
import tree.Expressao.Binario;
import tree.Expressao.Grupo;
import tree.Expressao.Literal;
import tree.Expressao.Logico;
import tree.Expressao.Unario;
import tree.Expressao.Variavel;

public class Interpreter implements Expressao.Visitor<Object>, Declaracao.Visitor<Void> {
    private BufferedReader reader;
    private Environment environment = new Environment();

    public Interpreter(BufferedReader reader) {
	this.reader = reader;
    }

    public void interpret(Declaracao.Programa programa) {
	try {
	    this.visitProgramaDeclaracao(programa);
	   
	} catch (RuntimeError error) {
	    Principal.runtimeError(error);
	}
    }

    // HELPERS:
    private void execute(Declaracao declaracao) {
	declaracao.accept(this);
    }

    private String stringify(Object object) {
	if (object == null)
	    return "nulo";

	// Hack. Work around Java adding ".0" to integer-valued doubles.
	if (object instanceof Double) {
	    String text = object.toString();
	    if (text.endsWith(".0")) {
		text = text.substring(0, text.length() - 2);
	    }
	    return text;
	}

	if (object instanceof Boolean) {
	    if ((boolean) object) {
		return "verdadeiro";
	    } else {
		return "falso";
	    }
	}

	return object.toString();
    }

    private Object evaluate(Expressao expressao) {
	return expressao.accept(this);
    }

    private boolean isTruthy(Object object) {
	if (object == null)
	    return false;
	if (object instanceof Boolean)
	    return (boolean) object;
	return true;
    }

    private boolean isEqual(Object a, Object b) {
	// nil is only equal to nil.
	if (a == null && b == null)
	    return true;
	if (a == null)
	    return false;

	return a.equals(b);
    }

    private void checkNumberOperand(Token operator, Object operand) {
	if (operand instanceof Double)
	    return;
	if (operand instanceof Integer)
	    return;
	throw new RuntimeError(operator, "Operador deve ser um número.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
	boolean leftDoubleInt = left instanceof Double || left instanceof Integer;
	boolean rightDoubleInt = right instanceof Double || right instanceof Integer;
	if (leftDoubleInt && rightDoubleInt)
	    return;

	throw new RuntimeError(operator, "Operadores devem ser números.");
    }

    private Object retornaValorNumericoTipoCorreto(TokenType op, Object left, Object right) {
	if (left instanceof Integer && right instanceof Integer) {
	    switch (op) {
	    case MAIS:
		return (int) left + (int) right;
	    case MENOS:

		return (int) left - (int) right;
	    case ASTERISCO:

		return (int) left * (int) right;
	    case BARRA:

		return (int) left / (int) right;

	    }
	}
	double l = (left instanceof Integer) ? ((int) left) / 1.0 : (double) left;
	double r = (right instanceof Integer) ? ((int) right) / 1.0 : (double) right;
	switch (op) {
	case MAIS:
	    return l + r;
	case MENOS:

	    return l - r;
	case ASTERISCO:

	    return l * r;
	case BARRA:

	    return l / r;
	}
	return null;

    }

    private Double toDouble(Object valor) {
	if (valor instanceof Integer) {
	    return Double.valueOf((int) valor);
	}
	return (double) valor;
    }

    void executeBlock(List<Declaracao> statements, Environment environment) {
	Environment previous = this.environment; // uselles?
	try {
	    this.environment = environment;

	    for (Declaracao statement : statements) {
		execute(statement);
	    }
	} finally {
	    this.environment = previous;
	}
    }

    // Nodes:

    @Override
    public Object visitBinarioExpressao(Binario expressao) {
	Object esquerda = evaluate(expressao.esquerda);
	Object direita = evaluate(expressao.direita);

	switch (expressao.operador.type) {
	case MENOS:
	    checkNumberOperands(expressao.operador, esquerda, direita);
	    return retornaValorNumericoTipoCorreto(MENOS, esquerda, direita);
	case BARRA:
	    checkNumberOperands(expressao.operador, esquerda, direita);
	    return retornaValorNumericoTipoCorreto(BARRA, esquerda, direita);
	case ASTERISCO:
	    checkNumberOperands(expressao.operador, esquerda, direita);
	    return retornaValorNumericoTipoCorreto(ASTERISCO, esquerda, direita);
	case MAIS:
	    if (esquerda instanceof String && direita instanceof String) {
		return (String) esquerda + (String) direita;
	    }
	    if ((esquerda instanceof Double || esquerda instanceof Integer)
		    && (direita instanceof Double || direita instanceof Integer)) {
		return retornaValorNumericoTipoCorreto(MAIS, esquerda, direita);
	    }
	    throw new RuntimeError(expressao.operador, "Operadores devem ser números ou cadeia de caracteres.");
	case MAIOR_QUE:
	    checkNumberOperands(expressao.operador, esquerda, direita);
	    return toDouble(esquerda) > toDouble(direita);
	case MAIOR_IQUAL:
	    checkNumberOperands(expressao.operador, esquerda, direita);
	    return toDouble(esquerda) >= toDouble(direita);
	case MENOR_QUE:
	    checkNumberOperands(expressao.operador, esquerda, direita);
	    return toDouble(esquerda) < toDouble(direita);
	case MENOR_IGUAL:
	    checkNumberOperands(expressao.operador, esquerda, direita);

	    return toDouble(esquerda) <= toDouble(direita);
	case DIFERENTE:
	    return !isEqual(esquerda, direita);
	case IGUAL:
	    return isEqual(esquerda, direita);
	default:
	    break;
	}
	return null;
    }

    @Override
    public Object visitGrupoExpressao(Grupo expressao) {
	return evaluate(expressao.expressao);
    }

    @Override
    public Object visitLiteralExpressao(Literal expressao) {
	return expressao.valor;
    }

    @Override
    public Object visitUnarioExpressao(Unario expressao) {
	Object direita = evaluate(expressao.direira);

	switch (expressao.operador.type) {
	case NAO:
	    return !isTruthy(direita);
	case MENOS:
	    checkNumberOperand(expressao.operador, direita);
	    if (direita instanceof Integer) {
		return -(int) direita;
	    }
	    if (direita instanceof Double) {
		return -(double) direita;
	    }
	    break;

	default:
	    break;
	}
	return null;
    }

    @Override
    public Void visitExpressaoDeclaracao(tree.Declaracao.Expressao declaracao) {
	evaluate(declaracao.expressao);
	return null;
    }

    @Override
    public Void visitPrintDeclaracao(Print declaracao) {
	Object valor = evaluate(declaracao.expressao);
	System.out.println(stringify(valor)); // imprime acoes no terminal
	return null;
    }

    @Override
    public Void visitLerDeclaracao(Ler declaracao) {
	try {
	    String v = reader.readLine();
	    System.out.println("ler: " + v);// imprime acoes no terminal

	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public Void visitVarDeclaracao(Var declaracao) {
	// TODO: atribuir tipo, iniciar com valor nulo, remover expressao da declaracao
	// e substituir por tipo
//		Object value = null;                        
//	    if (declaracao.expressao != null) {             
//	      value = evaluate(declaracao.expressao);       
//	    }

	environment.define(declaracao.nome, declaracao.tipo);
	return null;
    }

    @Override
    public Object visitVariavelExpressao(Variavel expressao) {
	return environment.get(expressao.nome);
    }

    @Override
    public Object visitAtribuicaoExpressao(Atribuicao expressao) {
	Object value = evaluate(expressao.valor);

	environment.assign(expressao.nome, value);
	return value;
    }

    @Override
    public Void visitBlocoDeclaracao(Bloco declaracao) {
	executeBlock(declaracao.declaracoes, this.environment);
	return null;
    }

    @Override
    public Void visitSeDeclaracao(Se declaracao) {
	if (isTruthy(evaluate(declaracao.condicao))) {
	    execute(declaracao.entaoBloco);
	} else if (declaracao.senaoBloco != null) {
	    execute(declaracao.senaoBloco);
	}
	return null;
    }

    @Override
    public Object visitLogicoExpressao(Logico expressao) {
	Object esquerda = evaluate(expressao.esquerda);

	if (expressao.operador.type == OU) {
	    if (isTruthy(esquerda)) {
		return esquerda;
	    }
	} else {
	    if (!isTruthy(esquerda))
		return esquerda;
	}
	return evaluate(expressao.direita);
    }

    @Override
    public Void visitEnquantoDeclaracao(Enquanto declaracao) {
	while (isTruthy(evaluate(declaracao.condicao))) {
	    execute(declaracao.corpo);
	}
	return null;
    }

    @Override
    public Void visitParaDeclaracao(Para declaracao) {
	evaluate(declaracao.atribuicao);
	while (isTruthy(evaluate(declaracao.condicao))) {
	    execute(declaracao.facaBloco);
	    evaluate(declaracao.incremento);
	}
	return null;
    }

    @Override
    public Void visitProgramaDeclaracao(Programa declaracao) {
	 for (Declaracao variaveis : declaracao.variaveis) {
		execute(variaveis);
	 }
	 for (Declaracao corpo : declaracao.corpo) {
		execute(corpo);
	 }
	return null;
    }

}

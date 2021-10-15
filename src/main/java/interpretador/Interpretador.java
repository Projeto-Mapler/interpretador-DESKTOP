package interpretador;

import static modelos.TiposToken.ASTERISCO;
import static modelos.TiposToken.BARRA;
import static modelos.TiposToken.MAIS;
import static modelos.TiposToken.MENOS;
import static modelos.TiposToken.OU;
import java.util.List;
import java.util.Map;
import evento.EventoInterpretador;
import evento.EventosService;
import modelos.TiposToken;
import modelos.Token;
import modelos.VariavelVetor;
import modelos.excecao.ExecucaoInterrompidaException;
import modelos.excecao.RuntimeError;
import modelos.tree.Declaracao;
import modelos.tree.Expressao;
import modelos.tree.Declaracao.Bloco;
import modelos.tree.Declaracao.ChamadaModulo;
import modelos.tree.Declaracao.Enquanto;
import modelos.tree.Declaracao.Escreva;
import modelos.tree.Declaracao.Fim;
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
 * Interpreta e gera a saída do pseudocodigo representado no Declaração.Programa
 * passado
 * 
 * @author Kerlyson
 *
 */
public class Interpretador implements Expressao.Visitor<Object>, Declaracao.Visitor<Void> {

	private Ambiente environment = new Ambiente();
	private EventosService gerenciadorEventos;
	private LeitorEntradaConsole entradaConsole = new LeitorEntradaConsole(this);
	private boolean terminado, pausado;
	private Thread thread;

	public Interpretador(EventosService ge) {
		this.gerenciadorEventos = ge;
		this.terminado = true;
		this.pausado = false;
	}

	public Map<String, Object> getAmbienteSnapshot() {
		return this.environment.criarSnapshot();
	}

	public void interpretar(Declaracao.Programa programa) {
		this.pausado = false;
		this.terminado = false;
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();

				try {
					visitProgramaDeclaracao(programa);
				} catch (RuntimeError error) {
					gerenciadorEventos.notificar(EventoInterpretador.ERRO, error);
					terminado = true;
				} catch (ExecucaoInterrompidaException e) {
					terminado = true;
					// e.printStackTrace();
				} catch (Exception e) {
					terminado = true;
					e.printStackTrace();
				}

				long elapsedTime = System.currentTimeMillis();
				double tempoExecucao = (double) (elapsedTime - startTime) / 1000F;

				if (terminado) {
					gerenciadorEventos.notificar(EventoInterpretador.INTERPRETACAO_INTERROMPIDA, tempoExecucao);
				} else {
					terminado = true;
					gerenciadorEventos.notificar(EventoInterpretador.INTERPRETACAO_CONCLUIDA, tempoExecucao);
				}

				environment.reset();
				entradaConsole.reset();

			}
		});
		this.thread.start();
	}

	// CONTROLE DA EXECUCAO:

	public void suspenderExecucao() {
		if (this.isExecutando()) {
			this.pausado = true;
			this.terminado = false;
			synchronized (this.thread) {
				try {
					this.thread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void continuarExecucao() {
		if (this.pausado) {
			this.pausado = false;
			this.terminado = false;
			synchronized (this.thread) {
				this.thread.notify();
			}
		}
	}

	public void terminarExecucao() {
		this.terminado = true;
		if (this.pausado) {
			this.pausado = false;
			synchronized (this.thread) {
				this.thread.notify();
			}
		}
	}

	public boolean isExecutando() {
		return !this.terminado && !this.pausado;
	}

	public boolean isPausado() {
		return this.pausado;
	}

	public boolean isTerminado() {
		return this.terminado;
	}

	private void checkTerminado() {
		if (this.terminado) {
			throw new ExecucaoInterrompidaException();
		}
	}

	// AUXILIARES:
	private void execute(Declaracao declaracao) {
		gerenciadorEventos.notificar(EventoInterpretador.VISITA_NODE_AST, declaracao);
		checkTerminado();
		declaracao.accept(this);
	}

	private Object evaluate(Expressao expressao) {
		gerenciadorEventos.notificar(EventoInterpretador.VISITA_NODE_AST, expressao);
		checkTerminado();
		return expressao.accept(this);
	}

	/**
	 * Converte objeto para string
	 * 
	 * @param object
	 * @return
	 */
	private String stringify(Object object) {
		if (object == null)
			return "nulo";

		// Java add ".0" em intergers convertidos para doubles.
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

	private boolean isLogico(Object object) {
		if (object == null)
			return false;
		if (object instanceof Boolean)
			return (boolean) object;
		return true; // TODO: jogar error
	}

	private boolean isIgual(Object a, Object b) {
		if (a == null && b == null)
			return true;
		if (a == null)
			return false;

		return a.equals(b);
	}

	private void checkarOperadorNumerico(Token operador, Object operando) {
		if (operando instanceof Double)
			return;
		if (operando instanceof Integer)
			return;
		throw new RuntimeError(operador, "Operando não numérico.");
	}

	private void checarOperadorNumericos(Token operator, Object left, Object right) {
		boolean leftDoubleInt = left instanceof Double || left instanceof Integer;
		boolean rightDoubleInt = right instanceof Double || right instanceof Integer;
		if (leftDoubleInt && rightDoubleInt)
			return;

		throw new RuntimeError(operator, "Operandos não numéricos.");
	}

	private Object retornaValorNumericoTipoCorreto(TiposToken op, Object esquerda, Object direita) {
		// se os dois forem inteiros
		if (esquerda instanceof Integer && direita instanceof Integer) {
			switch (op) {
			case MAIS:
				return (int) esquerda + (int) direita;
			case MENOS:
				return (int) esquerda - (int) direita;
			case ASTERISCO:
				return (int) esquerda * (int) direita;
			case BARRA:
				return (int) esquerda / (int) direita;
			default:
				break;
			}
		}
		// se os dois não forem inteiros, retorna um real
		double esquerdaDouble = (esquerda instanceof Integer) ? ((int) esquerda) / 1.0 : (double) esquerda;
		double direitaDouble = (direita instanceof Integer) ? ((int) direita) / 1.0 : (double) direita;
		switch (op) {
		case MAIS:
			return esquerdaDouble + direitaDouble;
		case MENOS:
			return esquerdaDouble - direitaDouble;
		case ASTERISCO:
			return esquerdaDouble * direitaDouble;
		case BARRA:
			return esquerdaDouble / direitaDouble;
		default:
			break;
		}
		return null;

	}

	private Double toDouble(Object valor) {
		if (valor instanceof Integer) {
			return Double.valueOf((int) valor);
		}
		return (double) valor;
	}

	/**
	 * Executa lista de blocos de execução
	 * 
	 * @param statements  - lista de Declaracoes que devem ser executados
	 * @param environment - escopo do bloco | não utilizado pois existe apenas um
	 *                    escopo atualmente
	 */
	public void executeBlock(List<Declaracao> statements, Ambiente environment) {
		for (Declaracao statement : statements) {
			execute(statement);
		}
	}

	// NODES:

	@Override
	public Object visitBinarioExpressao(Binario expressao) {
		Object esquerda = evaluate(expressao.esquerda);
		Object direita = evaluate(expressao.direita);
		try {
		switch (expressao.operador.type) {
		case MENOS:
			checarOperadorNumericos(expressao.operador, esquerda, direita);
			return retornaValorNumericoTipoCorreto(MENOS, esquerda, direita);
		case BARRA:
			checarOperadorNumericos(expressao.operador, esquerda, direita);
			return retornaValorNumericoTipoCorreto(BARRA, esquerda, direita);
		case ASTERISCO:
			checarOperadorNumericos(expressao.operador, esquerda, direita);
			return retornaValorNumericoTipoCorreto(ASTERISCO, esquerda, direita);
		case MAIS:
			if (esquerda instanceof String && direita instanceof String) {
				return (String) esquerda + (String) direita;
			}
			if ((esquerda instanceof Double || esquerda instanceof Integer)
					&& (direita instanceof Double || direita instanceof Integer)) {
				return retornaValorNumericoTipoCorreto(MAIS, esquerda, direita);
			}
			throw new RuntimeError(expressao.operador,
					"Operadores devem ser apenas números ou apenas cadeia de caracteres.");
		case MAIOR_QUE:
			checarOperadorNumericos(expressao.operador, esquerda, direita);
			return toDouble(esquerda) > toDouble(direita);
		case MAIOR_IQUAL:
			checarOperadorNumericos(expressao.operador, esquerda, direita);
			return toDouble(esquerda) >= toDouble(direita);
		case MENOR_QUE:
			checarOperadorNumericos(expressao.operador, esquerda, direita);
			return toDouble(esquerda) < toDouble(direita);
		case MENOR_IGUAL:
			checarOperadorNumericos(expressao.operador, esquerda, direita);

			return toDouble(esquerda) <= toDouble(direita);
		case DIFERENTE:
			return !isIgual(esquerda, direita);
		case IGUAL:
			return isIgual(esquerda, direita);
		default:
			break;
		}
		} catch (ArithmeticException ex) {			
			throw new RuntimeError(expressao.operador, "Expressão aritimética inválida!");
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
			return !isLogico(direita);
		case MENOS:
			checkarOperadorNumerico(expressao.operador, direita);
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
	public Void visitExpressaoDeclaracao(modelos.tree.Declaracao.Expressao declaracao) {
		evaluate(declaracao.expressao);
		return null;
	}

	@Override
	public Void visitEscrevaDeclaracao(Escreva declaracao) {
		StringBuilder output = new StringBuilder();

		for (modelos.tree.Expressao expressao : declaracao.expressoes) {
			Object valor = evaluate(expressao);
			if (valor instanceof VariavelVetor) {
				Object v[] = ((VariavelVetor) valor).getValores();
				output.append("[");
				for (int x = 0; x < v.length; x++) {
					output.append(stringify(v[x]));
					if (x < v.length - 1) {
						output.append(", ");
					}
				}
				output.append("]");
				return null;
			}
			output.append(stringify(valor));
		}
		gerenciadorEventos.notificar(EventoInterpretador.OUTPUT, output.toString());
		// System.out.println(output.toString());// imprime acoes no terminal
		return null;
	}

	@Override
	public Void visitLerDeclaracao(Ler declaracao) {

		this.gerenciadorEventos.notificar(EventoInterpretador.INPUT, this.entradaConsole);
		if (!this.entradaConsole.getValorSetado())
			this.suspenderExecucao();// espera o valor ser setado para continuar

		String valor = this.entradaConsole.getValor();
		this.entradaConsole.reset();
		// System.err.println("lido: " + valor);// imprime acoes no terminal

		Expressao atribuicao = declaracao.atribuicao;
		if (atribuicao instanceof Expressao.Atribuicao) {
			Token nome = ((Expressao.Atribuicao) atribuicao).nome;
			environment.setVariavelPorFuncaoLer(nome, valor, null);
		}
		if (atribuicao instanceof Expressao.AtribuicaoArray) {
			Token nome = ((Expressao.AtribuicaoArray) atribuicao).nome;
			Object index = evaluate(((Expressao.AtribuicaoArray) atribuicao).index);
			environment.setVariavelPorFuncaoLer(nome, valor, index);
		}

		return null;
	}

	@Override
	public Void visitVarDeclaracao(Var declaracao) {
		environment.definirVariavel(declaracao.nome, declaracao.tipo);
		return null;
	}

	@Override
	public Object visitVariavelExpressao(Variavel expressao) {
		return environment.getValorVariavel(expressao.nome);
	}

	@Override
	public Object visitAtribuicaoExpressao(Atribuicao expressao) {
		Object value = evaluate(expressao.valor);
		if (value instanceof modelos.Modulo) {
			throw new RuntimeError(expressao.nome, "Módulo não pode ser atribuido para variável");
		}
		environment.setValorVariavel(expressao.nome, value);
		return value;
	}

	@Override
	public Void visitBlocoDeclaracao(Bloco declaracao) {

		executeBlock(declaracao.declaracoes, null);
		return null;
	}

	@Override
	public Void visitSeDeclaracao(Se declaracao) {
		if (isLogico(evaluate(declaracao.condicao))) {
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
			if (isLogico(esquerda)) {
				return esquerda;
			}
		} else {
			if (!isLogico(esquerda))
				return esquerda;
		}
		return evaluate(expressao.direita);
	}

	@Override
	public Void visitEnquantoDeclaracao(Enquanto declaracao) {
		while (isLogico(evaluate(declaracao.condicao))) {
			execute(declaracao.corpo);
		}
		return null;
	}

	@Override
	public Void visitParaDeclaracao(Para declaracao) {

		evaluate(declaracao.atribuicao);
		Expressao.Binario condicao = (Expressao.Binario) declaracao.condicao;

		while (isLogico(evaluate(condicao))) {
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

		for (Declaracao modulo : declaracao.modulos) {
			execute(modulo);
		}

		for (Declaracao corpo : declaracao.corpo) {
			execute(corpo);
		}

		execute(declaracao.fim);

		return null;
	}

	@Override
	public Void visitVariavelArrayDeclaracao(VariavelArray declaracao) {
		int intervaloI = (int) evaluate(declaracao.intervaloI);
		int intervaloF = (int) evaluate(declaracao.intervaloF);

		if (intervaloI > intervaloF) {
			throw new RuntimeError(declaracao.nome, "Intervalo inicial não pode ser maior que o intervalo final");
		}
		if (declaracao.tipo.type == TiposToken.TIPO_MODULO) {
			throw new RuntimeError(declaracao.nome, "vetor não pode ter o tipo modulo.");
		}
		environment.definirVariavelVetor(declaracao.nome,
				new VariavelVetor(declaracao.tipo.type, intervaloI, intervaloF));
		return null;
	}

	@Override
	public Object visitAtribuicaoArrayExpressao(AtribuicaoArray expressao) {
		Object index = evaluate(expressao.index);
		Object valor = evaluate(expressao.valor);

		this.environment.setValorVariavelVetor(expressao.nome, index, valor);

		return null;
	}

	@Override
	public Object visitVariavelArrayExpressao(Expressao.VariavelArray expressao) {
		VariavelVetor variavel = (VariavelVetor) environment.getValorVariavel(expressao.nome);
		Object index = evaluate(expressao.index);
		if (index == null) {
			throw new RuntimeError(expressao.nome, "Index informado não pode ser nulo.");
		}
		if (!(index instanceof Integer)) {
			throw new RuntimeError(expressao.nome, "Index informado não pode ser resolvido.");
		}
		if ((int) index < 0 || (int) index > variavel.getIntervaloF() || (int) index < variavel.getIntervaloI()) {
			throw new RuntimeError(expressao.nome, "Index informado não encontrado");
		}
		return variavel.getValorNoIndex((int) index);
	}

	@Override
	public Object visitExpParentizadaExpressao(ExpParentizada expressao) {
		return this.evaluate(expressao.grupo);
	}

	@Override
	public Void visitRepitaDeclaracao(Repita declaracao) {
		do {
			execute(declaracao.corpo);
		} while (isLogico(evaluate(declaracao.condicao)));
		return null;
	}

	@Override
	public Void visitModuloDeclaracao(Modulo declaracao) {
		modelos.Modulo modulo = new modelos.Modulo(declaracao);
		this.environment.setValorVariavel(declaracao.nome, modulo);
		return null;
	}

	@Override
	public Void visitChamadaModuloDeclaracao(ChamadaModulo declaracao) {
		modelos.Modulo modulo = (modelos.Modulo) environment.getValorVariavel(declaracao.identificador);
		modulo.chamar(this, null);
		return null;
	}

	@Override
	public Void visitVarDeclaracoesDeclaracao(VarDeclaracoes declaracao) {
		for (Declaracao variavel : declaracao.variaveis) {
			execute(variavel);
		}
		return null;
	}

	@Override
	public Void visitFimDeclaracao(Fim declaracao) {
		// faz nada - fim serve para emitir o ultimo valor para o debug
		return null;
	}

}

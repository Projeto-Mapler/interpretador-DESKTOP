package interpretador;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import analisador.AnalisadorLexico;
import analisador.AnalisadorSintatico;
import conversores.Conversor;
import conversores.ConversorFactory;
import conversores.ConversorStrategy;
import debug.DebugSnapshot;
import debug.DebugStrategy;
import debug.Debugador;
import debug.EstadoDebug;
import evento.EventoInterpretador;
import evento.EventoListener;
import evento.EventosService;
import log.LogService;
import modelos.Token;
import modelos.tree.Declaracao;
import modelos.tree.Declaracao.Programa;

/**
 * Responsável por executar as etapas do interpretador na ordem correta,
 * disparar os eventos de erros;
 * 
 * @author Kerlyson
 *
 */
public class InterpretadorService implements EventoListener {

	private boolean temErro = false;
	private Interpretador interpreter;
	private AnalisadorLexico analisadorLexico;
	private AnalisadorSintatico analisadorSintatico;
	private EventosService eventos;
	private Debugador debugador;
	private final AcaoInterpretador acoes;
	private LogService log;

	/**
	 * Gerencia o fluxo de interpretacao, tradução e debug
	 * 
	 * @param acoes      - Implementacao com as acoes que devem ser executadas nos
	 *                   eventos
	 * @param debugAtivo
	 */
	public InterpretadorService(AcaoInterpretador acoes) {
		this.acoes = acoes;

		this.eventos = new EventosService();

		this.analisadorLexico = new AnalisadorLexico(this.eventos);
		this.analisadorSintatico = new AnalisadorSintatico(this.eventos);
		this.interpreter = new Interpretador(this.eventos);
		this.debugador = new Debugador(this.eventos, this.interpreter, false);
		this.log = new LogService();
	}

	// EXECUTAR
	public void executarViaArquivo(String path) throws IOException {
		String source = this.getTextoCodigoDoArquivo(path);
		executarViaTexto(source);
	}

	public void executarViaTexto(String source) {
		// resets:
		interpreter.terminarExecucao();
		this.temErro = false;
		this.eventos.inscreverTodos(EventoInterpretador.values(), this);
		Programa programa = this.gerarPrograma(source);
		if (programa == null)
			return;
		interpreter.interpretar(programa);
	}

	// TRADUÇÕES
	public String traduzirDoArquivo(String path, ConversorStrategy conversorStrategy) throws IOException {
		String source = this.getTextoCodigoDoArquivo(path);
		return traduzirDoTexto(source, conversorStrategy);
	}

	public String traduzirDoTexto(String texto, ConversorStrategy conversorStrategy) throws IOException {
		Declaracao.Programa programa = this.gerarPrograma(texto);
		if (programa == null)
			return null;
		Conversor conversor = ConversorFactory.getConversor(this.eventos, programa, conversorStrategy);
		return conversor.converter();
	}

	// GET PROGRAMA AST
	public Declaracao.Programa getProgramaASTViaArquivo(String path) throws IOException {
		String source = this.getTextoCodigoDoArquivo(path);
		return getProgramaASTViaTexto(source);
	}

	public Declaracao.Programa getProgramaASTViaTexto(String texto) throws IOException {
		return this.gerarPrograma(texto);
	}

	// GET TOKENS
	public List<Token> getTokensViaTexto(String source) {
		return this.analisadorLexico.scanTokens(source);
	}

	public List<Token> getTokensViaArquivo(String path) throws IOException {
		String source = this.getTextoCodigoDoArquivo(path);
		return getTokensViaTexto(source);
	}

	private String getTextoCodigoDoArquivo(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		return new String(bytes, Charset.defaultCharset()).trim();
	}

	private Programa gerarPrograma(String source) {
		List<Token> tokens = this.getTokensViaTexto(source);
		Programa p = this.analisadorSintatico.parse(tokens);
		if (this.temErro)
			return null;
		return p;
	}

	// DEBUGADOR
	public boolean isDebugAtivo() {
		return this.debugador.isAtivo();
	}

	public void setDebugAtivo(boolean ativo) {
		this.debugador.setAtivo(ativo);
	}

	public void setDebugStrategy(DebugStrategy s) {
		this.debugador.setDebugStrategy(s);
	}

	public void debugProxPasso() {
		this.debugador.proximoPasso();
	}

	public void debugContinuar() {
		this.debugador.continuarExecucao();
	}

	public void debugParar() {
		this.debugador.terminarExecucao();
	}

	// LOG
	public void setLogAtivo(boolean ativar) {
		this.log.setAtivo(ativar);
	}

	public void setLogColorido(boolean colorido) {
		this.log.setColorido(colorido);
	}

	public boolean isLogColorido() {
		return this.log.isColorido();
	}

	public boolean isLogAtivo() {
		return this.log.isAtivo();
	}

	public void setEventoLog(EventoInterpretador eventos) {
		this.log.addEvento(eventos);
	}

	public void setEventosLog(List<EventoInterpretador> eventos) {
		this.log.addEventos(eventos);
	}

	public void removerEventosLog(List<EventoInterpretador> eventos) {
		this.log.removerEventos(eventos);
	}

	public void removerEventosLog() {
		this.log.removerEventos();
	}

	// EVENTOS
	@Override
	public void update(EventoInterpretador evento, Object payload) {
		if (this.isLogAtivo()) {
			String logMsg = this.log.printLog(evento, payload);
			if (logMsg != null)
				this.acoes.onLog(logMsg);
		}

		switch (evento) {
		case ERRO:
			temErro = true;
			this.acoes.onErro((RuntimeException) payload);
			this.eventos.marcarParaDesinscrever(this); // para a execucao.. habilita o garbageCollector
			return;
		case INPUT:
			this.acoes.onInput((LeitorEntradaConsole) payload);
			return;
		case OUTPUT:
			this.acoes.onOutput((String) payload);
			return;
		case INTERPRETACAO_CONCLUIDA:
			this.acoes.onInterpretacaoConcluida((double) payload);
			this.eventos.marcarParaDesinscrever(this);// para a execucao.. habilita o garbageCollector
			return;
		case INTERPRETACAO_INTERROMPIDA:
			this.acoes.onInterpretacaoInterrompida((double) payload);
			this.eventos.marcarParaDesinscrever(this);// para a execucao.. habilita o garbageCollector
			return;
		case DEBUG_MUDANCA_ESTADO:
			this.acoes.onDebugMudancaEstado((EstadoDebug) payload);
			return;
		case DEBUG_PASSO_EXECUTADO:
			this.acoes.onDebugPassoExecutado((DebugSnapshot) payload);
			return;
		case VISITA_NODE_AST:
			return;
		}
	}

}

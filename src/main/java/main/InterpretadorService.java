package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import analisador.AnalisadorSintatico;
import analisador.AnalisadorLexico;
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
import interpretador.Interpretador;
import interpretador.LeitorEntradaConsole;
import modelos.Token;
import modelos.tree.Declaracao;
import modelos.tree.Declaracao.Programa;

/**
 * Responsável por executar as etapas do interpretador na ordem correta, disparar os eventos de
 * erros;
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
  
  /**
   * Gerencia o fluxo de interpretacao, tradução e debug
   * @param acoes - Implementacao com as acoes que devem ser executadas nos eventos
   * @param debugAtivo 
   */
  public InterpretadorService(AcaoInterpretador acoes, boolean debugAtivo) {
    this.acoes = acoes;
    
    this.eventos = new EventosService();   
    this.eventos.inscreverTodos(EventoInterpretador.values(), this);

    this.analisadorLexico = new AnalisadorLexico(this.eventos);
    this.analisadorSintatico = new AnalisadorSintatico(this.eventos);
    this.interpreter = new Interpretador(this.eventos);
    this.debugador = new Debugador(this.eventos, this.interpreter, debugAtivo);
  }
  // EXECUTAR
  public void executarViaArquivo(String path) throws IOException {
    String source = this.getTextoCodigoDoArquivo(path);
    executarViaTexto(source);
  }
  public void executarViaTexto(String source) {
    if (!interpreter.isExecutando()) {
      interpreter.terminarExecucao();
    }
    Programa programa = this.gerarPrograma(source);
    if (programa == null) return;
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
  public List<Token> getTokensViaTexto(String source){
    return this.analisadorLexico.scanTokens(source);
  }
  public List<Token> getTokensViaArquivo(String path) throws IOException{
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
    if(this.temErro) return null;
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
  
  // EVENTOS
  @Override
  public void update(EventoInterpretador tipoEvento, Object payload) {
    //System.out.println("[EVENTO]= "+ tipoEvento.toString());
    switch (tipoEvento) {
      case ERRO:
        temErro = true;
        this.acoes.onErro((RuntimeException) payload);
        return;
      case INPUT:
        this.acoes.onInput((LeitorEntradaConsole) payload);
        return;        
      case OUTPUT: 
        this.acoes.onOutput((String) payload);
        return;
      case INTERPRETACAO_CONCLUIDA: 
        this.acoes.onInterpretacaoConcluida((double) payload);
        return;
      case INTERPRETACAO_INTERROMPIDA: 
        this.acoes.onInterpretacaoInterrompida((double) payload);
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

  public void fechar() {
    this.eventos.desiscreverTodos(this);
  }
  
}

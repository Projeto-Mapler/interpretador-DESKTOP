package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import conversores.Conversor;
import conversores.ConversorFactory;
import conversores.ConversorStrategy;
import debug.Debugador;
import evento.EventoInterpretador;
import evento.EventoListener;
import evento.GerenciadorEventos;
import interpretador.Interpretador;
import modelos.Token;
import modelos.tree.Declaracao;
import modelos.tree.Declaracao.Programa;
import parser.Parser;
import scanner.Scanner;

/**
 * Responsável por executar as etapas do interpretador na ordem correta, disparar os eventos de
 * erros;
 * 
 * @author Kerlyson
 *
 */
public class Principal implements EventoListener {

  private boolean temErro = false;
  private boolean temRunTimeErro = false;

  private Interpretador interpreter;
  private GerenciadorEventos eventos;

  public Principal(GerenciadorEventos ge, Debugador debug) {
    eventos = ge;
    ge.inscreverTodos(new EventoInterpretador[] {
        EventoInterpretador.ERRO_PARSE, 
        EventoInterpretador.ERRO_RUNTIME,
        EventoInterpretador.INTERPRETACAO_CONCLUIDA}, this);
    interpreter = new Interpretador(ge);
    if (debug != null) {
      debug.setInterpretador(interpreter);
    }
  }

  public void executarViaArquivo(String path) throws IOException {
    String source = this.getTextoCodigoDoArquivo(path);
    executarViaTexto(source);
  }  
  
  public void executarViaTexto(String source) {
    if(!interpreter.isExecutando()) {
      interpreter.terminarExecucao();
    }
    Programa programa = this.gerarPrograma(source);
    if (temErro || temRunTimeErro)
      return;
    interpreter.interpretar(programa);
  }
 
  public Declaracao.Programa getProgramaAST(String path) throws IOException{
    String source = this.getTextoCodigoDoArquivo(path);
    return this.gerarPrograma(source);
  }

  public String traduzirDoArquivo(String path, ConversorStrategy conversorStrategy) throws IOException {
    String source = this.getTextoCodigoDoArquivo(path);
    return traduzirDoTexto(source, conversorStrategy);
  }
  
  public String traduzirDoTexto(String texto, ConversorStrategy conversorStrategy) throws IOException {
    Declaracao.Programa programa = this.gerarPrograma(texto);
    if (temErro || temRunTimeErro)
      return null;
    Conversor conversor = ConversorFactory.getConversor(this.eventos, programa, conversorStrategy);
    return conversor.converter();
  }

  private String getTextoCodigoDoArquivo(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    return new String(bytes, Charset.defaultCharset()).trim();
  }

  private Programa gerarPrograma(String source) {
    Scanner scanner = new Scanner(source, this.eventos);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens, this.eventos);
    return parser.parse();
  }

  @Override
  public void update(EventoInterpretador tipoEvento, Object payload) {
    switch (tipoEvento) {
      case ERRO_PARSE:
        temErro = true;
        break;
      case ERRO_RUNTIME:
        temRunTimeErro = true;
        break;
//      case INTERPRETACAO_CONCLUIDA:
//       this.interpreter.terminarExecucao();// garante que a thread do interpretador seja terminada;
      default:
        break;
    }
    return;
  }
}

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
import debug.EventoListener;
import debug.GerenciadorEventos;
import debug.TiposEvento;
import interpretador.Interpretador;
import modelos.Token;
import parser.Parser;
import scanner.Scanner;
import tree.Declaracao;
import tree.Declaracao.Programa;

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
    ge.inscreverTodos(new TiposEvento[] {TiposEvento.ERRO_PARSE, TiposEvento.ERRO_RUNTIME}, this);
    interpreter = new Interpretador(ge);
    if (debug != null) {
      debug.setInterpretador(interpreter);
    }
  }

  public void runFile(String path) throws IOException {
    String source = this.getSource(path);
    Programa programa = this.gerarPrograma(source);
    if (temErro || temRunTimeErro)
      return;
    interpreter.interpretar(programa);
  }  
 
  public Declaracao.Programa getProgramaAST(String path) throws IOException{
    String source = this.getSource(path);
    return this.gerarPrograma(source);
  }

  public String getConversao(String path, ConversorStrategy conversorStrategy) throws IOException {
    String source = this.getSource(path);
    Declaracao.Programa programa = this.gerarPrograma(source);
    if (temErro || temRunTimeErro)
      return null;
    Conversor conversor = ConversorFactory.getConversor(this.eventos, programa, conversorStrategy);
    return conversor.converter();
  }

  private String getSource(String path) throws IOException {
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
  public void update(TiposEvento tipoEvento, Object payload) {
    switch (tipoEvento) {
      case ERRO_PARSE:
        temErro = true;
        break;
      case ERRO_RUNTIME:
        temRunTimeErro = true;
        break;
      default:
        break;
    }
    return;
  }
}

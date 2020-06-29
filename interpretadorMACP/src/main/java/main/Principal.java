package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import conversores.Conversor;
import conversores.ConversorFactory;
import conversores.ConversorStrategy;
import debug.DebugSnapshot;
import debug.Debugador;
import debug.EventoListener;
import debug.GerenciadorEventos;
import debug.TiposEvento;
import interpretador.Interpretador;
import modelos.ParserError;
import modelos.RuntimeError;
import modelos.TiposToken;
import modelos.Token;
import parser.Parser;
import scanner.Scanner;
import tree.Declaracao;

/**
 * Responsável por executar as etapas do interpretador na ordem correta,
 * disparar os eventos de erros;
 * 
 * @author Kerlyson
 *
 */
public class Principal implements EventoListener {
    private boolean temErro = false;
    private boolean temRunTimeErro = false;

    private Interpretador interpreter;
    private GerenciadorEventos eventos;

    public Principal(GerenciadorEventos ge, Debugador d) {

	eventos = ge;
	ge.inscrever(TiposEvento.ACAO_DEBUG, this);
	interpreter = new Interpretador(this, ge);

	d.setInterpretador(interpreter);
//		BreakpointsDebugStrategy breakpointsDebugStrategy = new BreakpointsDebugStrategy();
//		breakpointsDebugStrategy.addBreakPoint(13);
//		debugador.setDebugStrategy(breakpointsDebugStrategy);
//		debugador.setDebugStrategy(new PassoAPassoDebugStrategy());

    }

    public void runFile(String path) throws IOException {
	byte[] bytes = Files.readAllBytes(Paths.get(path));
	run(new String(bytes, Charset.defaultCharset()).trim());

	if (temErro)
	    System.exit(65);
	if (temRunTimeErro)
	    System.exit(70);
    }

    private void run(String source) {
	Scanner scanner = new Scanner(this, source);
	List<Token> tokens = scanner.scanTokens();
	Parser parser = new Parser(this, tokens);
	Declaracao.Programa programa = parser.parse();

	if (temErro)
	    return;
//		interpreter.interpret(programa);
	Conversor cc = ConversorFactory.getConversor(this, programa, ConversorStrategy.PYTHON);
	System.out.println(cc.converter());

    }

    private void report(int line, String onde, String msg) {
	System.err.println("[Parser Erro | linha " + line + "] Erro " + onde + ": " + msg);
	temErro = true;
    }

    public void error(ParserError erro) {
	this.eventos.notificar(TiposEvento.ERRO_PARSE, erro);

	if (erro.token != null) {
	    if (erro.token.type == TiposToken.EOF) {
		report(erro.linha, " no fim", erro.mensagem);
	    } else {
		report(erro.linha, " em '" + erro.token.lexeme + "'", erro.mensagem);
	    }
	} else {
	    report(erro.linha, "", erro.mensagem);
	}
    }

    public void runtimeError(RuntimeError error) {
	this.eventos.notificar(TiposEvento.ERRO_RUNTIME, error);
	System.err
		  .println(
			   "[Runtime Erro | linha " + error.token.line + "] Erro em '" + error.token.lexeme + "': "
				   + error.getMessage());
	temRunTimeErro = true;
    }

    @Override
    public void update(TiposEvento tipoEvento, Object payload) {
	if (tipoEvento == TiposEvento.ACAO_DEBUG) {
	    DebugSnapshot s = (DebugSnapshot) payload;
	    System.out.println("=================");
	    System.out.println("linha: " + s.getNode().getLinha() + " .. " + s.getNode().getClass().getName());
	    System.out.println("Ambiente:");
	    System.out.println("Nome\tValor");
	    for (String n : s.getAmbienteSnapshot().keySet()) {
		System.out.print(n + "\t");
		Object valor = s.getAmbienteSnapshot().get(n);
		if (valor != null) {
		    System.out.print(valor.toString());
		}
		System.out.print("\n");

	    }
	    System.out.println("=================");
	}
    }

}

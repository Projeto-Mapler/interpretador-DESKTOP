package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import conversores.ConversorJava;
import debug.BreakpointsDebugStrategy;
import debug.Debugador;
import debug.GerenciadorEventos;
import debug.PassoAPassoDebugStrategy;
import debug.TiposEvento;
import interpretador.Interpretador;
import modelos.ParserError;
import modelos.RuntimeError;
import modelos.TiposToken;
import modelos.Token;
import parser.Parser;
import scanner.Scanner;
import tree.Declaracao;

public class Principal {
	private  boolean temErro = false;
	private  boolean temRunTimeErro = false;
	
	private  Interpretador interpreter;
	private  Debugador debugador;	
	private GerenciadorEventos eventos;
	
	public Principal(GerenciadorEventos ge, boolean debugAtivo) {
		
		eventos = ge;
		interpreter = new Interpretador(this, ge);
		
		debugador = new Debugador(interpreter, ge, debugAtivo);
		BreakpointsDebugStrategy breakpointsDebugStrategy = new BreakpointsDebugStrategy();
		breakpointsDebugStrategy.addBreakPoint(13);
//		debugador.setDebugStrategy(breakpointsDebugStrategy);
		debugador.setDebugStrategy(new PassoAPassoDebugStrategy());
	
	}

	public void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		
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
		interpreter.interpret(programa);
		
		ConversorJava cj = new ConversorJava(this, programa);
//		System.out.println("\n\n===>>Conversor Java:\n");
//		System.out.println(cj.converter());

	}


	private void report(int line, String onde, String msg) {
				System.err.println("[Parser Erro | linha " + line + "] Erro em '" + onde + "': " + msg);
		temErro = true;
	}

	public void error(ParserError erro) {
		this.eventos.notificar(TiposEvento.ERRO_PARSE, erro);

		if(erro.token != null) {			
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
		System.err.println("[Runtime Erro | linha " + error.token.line + "] Erro em '" + error.token.lexeme + "': " + error.getMessage());
		temRunTimeErro = true;
	}

}

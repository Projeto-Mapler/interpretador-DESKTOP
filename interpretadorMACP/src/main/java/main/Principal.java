package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import debug.BreakpointsDebugStrategy;
import debug.Debugador;
import debug.GerenciadorEventos;
import debug.PassoAPassoDebugStrategy;
import interpreter.Interpretador;
import model.ParserError;
import model.RuntimeError;
import model.Token;
import model.TokenType;
import parser.Parser;
import scanner.Scanner;
import tree.Declaracao;

public class Principal {
	private  boolean temErro = false;
	private  boolean temRunTimeErro = false;
	private  InputStreamReader input;
	private  BufferedReader reader;
	private  GerenciadorEventos ge;
	private  Interpretador interpreter;
	private  Debugador debugador;	
	
	public Principal(GerenciadorEventos ge) {
		input = new InputStreamReader(System.in);
		reader = new BufferedReader(input);
		this.ge = ge;
		
		interpreter = new Interpretador(this, reader, ge);
		debugador = new Debugador(interpreter, ge, true);
		BreakpointsDebugStrategy breakpointsDebugStrategy = new BreakpointsDebugStrategy();
		breakpointsDebugStrategy.addBreakPoint(13);
		debugador.setDebugStrategy(breakpointsDebugStrategy);
//		debugador.setDebugStrategy(new PassoAPassoDebugStrategy());
	
	}

	/*
	 * public static void main(String[] args) throws IOException { String
	 * caminhoExemplos = "..\\exemplos\\"; String exemplos[] = { "condicionais.txt",
	 * "laços.txt", "modulo.txt", "variaveis.txt", "io.txt", "operações.txt" };
	 * String arquivo = "C:\\Users\\Kerlyson\\Desktop\\12.txt";
	 * runFile(caminhoExemplos+exemplos[5]); }
	 */
	public void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		// Indicate an error in the exit code.
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
//		System.out.println(declaracoes.size());
//		new ImpressoraAST().print(declaracoes);// imprime arvore
//		JavaConversorTeste t = new JavaConversorTeste();
//		interpreter.interpret(programa);
		System.out.println("\n\n===>>Conversor Java:\n");
//		System.out.println(t.converter(programa));
		
		ConversorJava cj = new ConversorJava(programa);
		System.out.println(cj.converter());

	}


	private void report(int line, String onde, String msg) {
		System.err.println("[Parser Erro | linha " + line + "] Erro" + onde + ": " + msg);
		temErro = true;
	}

	public void error(ParserError erro) {
		if(erro.token != null) {			
			if (erro.token.type == TokenType.EOF) {
				report(erro.linha, " no fim", erro.mensagem);
			} else {
				report(erro.linha, " em '" + erro.token.lexeme + "'", erro.mensagem);
			}
		} else {
			report(erro.linha, "", erro.mensagem);
		}
	}

	public void runtimeError(RuntimeError error) {
		System.err.println("[Runtime Erro | linha " + error.token.line + "] " + error.getMessage());
		temRunTimeErro = true;
	}

}

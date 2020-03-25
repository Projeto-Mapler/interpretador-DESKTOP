package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import debug.Debugador;
import debug.GerenciadorEventos;
import debug.TipoEvento;
import interpreter.Interpretador;
import interpreter.JavaConversorTeste;
import model.ParserError;
import model.RuntimeError;
import model.Token;
import model.TokenType;
import parser.Parser;
import scanner.Scanner;
import tree.Declaracao;

public class Principal {
	private static boolean temErro = false;
	private static boolean temRunTimeErro = false;
	private static InputStreamReader input;
	private static BufferedReader reader;
	private static GerenciadorEventos ge;
	private static Interpretador interpreter;
	private static Debugador debugador;	
	
	static {
		input = new InputStreamReader(System.in);
		reader = new BufferedReader(input);
		ge = new GerenciadorEventos();
		debugador = new Debugador();
		ge.inscrever(TipoEvento.DEBUG, debugador);
		
		interpreter = new Interpretador(reader, ge);
	}

	/*
	 * public static void main(String[] args) throws IOException { String
	 * caminhoExemplos = "..\\exemplos\\"; String exemplos[] = { "condicionais.txt",
	 * "laços.txt", "modulo.txt", "variaveis.txt", "io.txt", "operações.txt" };
	 * String arquivo = "C:\\Users\\Kerlyson\\Desktop\\12.txt";
	 * runFile(caminhoExemplos+exemplos[5]); }
	 */
	public static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		// Indicate an error in the exit code.
		if (temErro)
			System.exit(65);
		if (temRunTimeErro)
			System.exit(70);
	}

	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser = new Parser(tokens);
		Declaracao.Programa programa = parser.parse();

		if (temErro)
			return;
//		System.out.println(declaracoes.size());
//		new ImpressoraAST().print(declaracoes);// imprime arvore
		JavaConversorTeste t = new JavaConversorTeste();
		interpreter.interpret(programa);
		System.out.println("\n\n===>>Conversor Java:\n");
		System.out.println(t.converter(programa));

	}


	private static void report(int line, String onde, String msg) {
		System.err.println("[Parser Erro | linha " + line + "] Erro" + onde + ": " + msg);
		temErro = true;
	}

	public static void error(ParserError erro) {
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

	public static void runtimeError(RuntimeError error) {
		System.err.println("[Runtime Erro | linha " + error.token.line + "] " + error.getMessage());
		temRunTimeErro = true;
	}
}

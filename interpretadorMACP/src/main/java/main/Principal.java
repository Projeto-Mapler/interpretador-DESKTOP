package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import interpreter.Interpretador;
import interpreter.JavaConversorTeste;
import model.Token;
import model.TokenType;
import parser.Parser;
import parser.RuntimeError;
import scanner.Scanner;
import tree.Declaracao;

public class Principal {
	static boolean temErro = false;
	static boolean temRunTimeErro = false;
	static final InputStreamReader input = new InputStreamReader(System.in);
	static final BufferedReader reader = new BufferedReader(input);
	private static final Interpretador interpreter = new Interpretador(reader);

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
	public static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		System.err
				.println("[linha " + line + "] Erro" + where + ": " + message);
		temErro = true;
	}
	public static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, " no fim", message);
		} else {
			report(token.line, " em '" + token.lexeme + "'", message);
		}
	}
	public static void runtimeError(RuntimeError error) {
		System.err.println(	"[linha " + error.token.line + "] " + error.getMessage() );
		temRunTimeErro = true;
	}
}

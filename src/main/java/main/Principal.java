package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import debug.Debugador;
import debug.EventoListener;
import debug.GerenciadorEventos;
import debug.TiposEvento;
import interpretador.Interpretador;
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

    public Principal(GerenciadorEventos ge, Debugador debug) {

	eventos = ge;
	ge.inscrever(TiposEvento.ACAO_DEBUG, this);
	interpreter = new Interpretador(ge);
	debug.setInterpretador(interpreter);

    }

    public void runFile(String path) throws IOException {
	byte[] bytes = Files.readAllBytes(Paths.get(path));
	run(new String(bytes, Charset.defaultCharset()).trim());

	if (temErro || temRunTimeErro)
	    return;
    }

    private void run(String source) {
	Scanner scanner = new Scanner(source, this.eventos);
	List<Token> tokens = scanner.scanTokens();
	
	Parser parser = new Parser(tokens, this.eventos);
	Declaracao.Programa programa = parser.parse();

	if (temErro)
	    return;
	interpreter.interpretar(programa);

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

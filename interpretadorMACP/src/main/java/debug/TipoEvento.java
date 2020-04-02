package debug;

public enum TipoEvento {
	NODE_DEBUG, // quando um node executa o metodo visit
	ERRO_RUNTIME, // quando um runtime erro eh lancado
	ERRO_PARSE, // quando um parse erro eh lancado
	TOGGLE_DEBUG, // informa se o debug deve ser executado
	MUDANCA_ESTADO_DEBUG, // quando o debugador muda de estado 
	CONTINUAR_DEBUG, // informa o debugador a continuar a execucao
	FINALIZAR_DEBUG, // informa o debugador a terminar a execucao
	;
}

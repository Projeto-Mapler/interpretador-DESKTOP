package debug;

public enum TiposEvento {
	/**
	 * quando o node 'ler' eh processado
	 * 
	 * @payload - LeitorEntradaConsole
	 */
	LER_EVENTO,
	
	/**
	 * quando o node 'escrever' eh processado
	 * 
	 * @payload - String
	 */
	ESCREVER_EVENTO,
	
	/**
	 * quando um node executa o metodo visit
	 * 
	 * @payload - AstDebugNode
	 */
	NODE_DEBUG,
	
	/**
	 * quando um runtime erro eh lancado
	 * 
	 * @payload - RuntimeError
	 */
	ERRO_RUNTIME,
	
	/**
	 * quando um parse erro eh lancado
	 * 
	 * @payload - ParserError
	 */
	ERRO_PARSE,
	
	/**
	 * informa se o debug deve ser executado
	 * 
	 * @payload - Boolean
	 */
	TOGGLE_DEBUG,
	
	/**
	 * Quando o debugador muda de estado
	 * 
	 * @payload - EstadosDebug
	 */
	MUDANCA_ESTADO_DEBUG,
	
	/**
	 * informa o debugador a continuar a execucao
	 * 
	 * @payload - null
	 */
	CONTINUAR_DEBUG_ATIVO,
	
	/**
	 * informa o debugador a continuar a execucao sem o processo de debug
	 * 
	 * @payload - null
	 */
	CONTINUAR_DEBUG_DESATIVADO,
	
	/**
	 * informa o debugador a terminar a execucao
	 * 
	 * @payload - null
	 */
	FINALIZAR_DEBUG,
	
	/**
	 * emitido apos uma estrategia de debug ser executada
	 * 
	 * @payload - DebugSnapshot
	 */
	ACAO_DEBUG,
	
	/**
	 * A thread do processo de interpretação foi concluida
	 * @payload - (double) tempo de execução
	 */
	INTERPRETACAO_CONCLUIDA,
	;
}

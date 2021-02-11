package evento;

/**
 * Eventos do interpretador
 * @author Kerlyson
 *
 */
public enum EventoInterpretador {
    /**
     * quando o node 'ler' eh processado
     * 
     * @payload - LeitorEntradaConsole
     */
    INPUT,

    /**
     * quando o node 'escrever' eh processado
     * 
     * @payload - String
     */
    OUTPUT,

    /**
     * quando um node executa o metodo visit
     * 
     * @payload - AstDebugNode
     */
    VISITA_NODE_AST,

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
    CONTINUAR_DEBUG_ON,

    /**
     * informa o debugador a continuar a execucao sem o processo de debug
     * 
     * @payload - null
     */
    CONTINUAR_DEBUG_OFF,

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
     * 
     * @payload - (double) tempo de execução
     */
    INTERPRETACAO_CONCLUIDA,
  
  /**
   * A thread do processo de interpretação foi terminada
   * 
   * @payload - null
   */
  INTERPRETACAO_INTERROMPIDA,;
}

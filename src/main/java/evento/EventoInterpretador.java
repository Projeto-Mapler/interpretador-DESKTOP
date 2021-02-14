package evento;

/**
 * Eventos do interpretador
 * 
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
   * @payload - objeto com o erro
   */
  ERRO,

  /**
   * Quando o debugador muda de estado
   * 
   * @payload - EstadosDebug
   */
  DEBUG_MUDANCA_ESTADO,

  /**
   * emitido apos uma estrategia de debug ser executada
   * 
   * @payload - DebugSnapshot
   */
  DEBUG_PASSO_EXECUTADO,

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

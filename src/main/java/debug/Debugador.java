package debug;

import evento.EventoInterpretador;
import evento.EventoListener;
import evento.EventosService;
import interpretador.Interpretador;
import modelos.tree.AstDebugNode;

/**
 * Realiza o processo de Debug do pseudoCodigo
 * 
 * @author Kerlyson
 *
 */
public class Debugador implements EventoListener {

  private final Interpretador interpretador;
  private EstadoDebug estado;// estado em que se encontra o Debugador
  private boolean ativo;
  private Integer linhaAnterior = 0; // linha do node verificado
  private final EventosService eventos;
  private DebugStrategy strategy;
  private AstDebugNode ultimoNode;
  private boolean resumir = false;

  /**
   * 
   * @param e - Gerenciador de Eventos compartilhado
   * @param i - Interpretador
   * @param ativo - SE o debugador deve ser executado quando o codigo for interpretado
   */
  public Debugador(EventosService e,  Interpretador i, boolean ativo) {
    this.interpretador = i;
    this.eventos = e;
    this.eventos.inscrever(EventoInterpretador.VISITA_NODE_AST, this);
    this.eventos.inscrever(EventoInterpretador.INTERPRETACAO_INTERROMPIDA, this);
    this.eventos.inscrever(EventoInterpretador.INTERPRETACAO_CONCLUIDA, this);
    this.setAtivo(ativo);
  }

  @Override
  public void update(EventoInterpretador tipoEvento, Object payload) {
    if (!this.ativo)
      return;
    switch (tipoEvento) {
      case VISITA_NODE_AST:
        this.updateNodeDebug(payload);
        return;
      case INTERPRETACAO_CONCLUIDA:
      case INTERPRETACAO_INTERROMPIDA:
        this.resumir = false;
        this.setEstado(EstadoDebug.INICIAL);
        return;
      default:
        return;
    }
  }

  /**
   * Chamado quando o evento Node_Debug é disparado
   * 
   * @param payload - NodeAstDebug
   */
  private void updateNodeDebug(Object payload) {
    if (!this.ativo || this.resumir) return;
    this.setEstado(EstadoDebug.EXECUTANDO);
    if (payload instanceof AstDebugNode) {
      AstDebugNode node = (AstDebugNode) payload;
      if (node.getLinha() < 1)
        return;

      ultimoNode = node;
      this.linhaAnterior = this.strategy.executar(node, this);
    }
  }

  /**
   * @param strategy - algoritimo de debug que deve ser utilizado
   */
  public void setDebugStrategy(DebugStrategy strategy) {
    this.strategy = strategy;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  public boolean isAtivo() {
    return this.ativo;
  }

  // CONTROLADORES DA EXECUÇÃO

  /**
   * Pausa a execução do interpretador
   */
  public void pausarExecucao() {
    this.setEstado(EstadoDebug.PAUSADO);
    this.eventos.notificar(EventoInterpretador.DEBUG_PASSO_EXECUTADO, new DebugSnapshot(ultimoNode, this.interpretador.getAmbienteSnapshot()));
    interpretador.suspenderExecucao();
  }

  /**
   * Para a execução do interpretador
   */
  public void terminarExecucao() {
    if(this.ativo && this.estado != EstadoDebug.INICIAL)
    this.interpretador.terminarExecucao();
  }

  /**
   * Continua a execução do interpretador, se estiver pausado.
   */
  public void proximoPasso() {
    if (this.ativo && !this.resumir && this.estado == EstadoDebug.PAUSADO) {
      this.setEstado(EstadoDebug.EXECUTANDO);
      this.interpretador.continuarExecucao();
    }
  }

  /**
   * Continua a execução do interpretador, se estiver pausado, sem o debug.
   */
  public void continuarExecucao() {
    if (this.ativo && !this.resumir && this.estado == EstadoDebug.PAUSADO) {
      this.resumir = true; // desativa temporariamente para não pausar no prox node
      this.interpretador.continuarExecucao();
    }
  }

  private void setEstado(EstadoDebug estado) {
    this.estado = estado;
    this.eventos.notificar(EventoInterpretador.DEBUG_MUDANCA_ESTADO, this.estado);
  }

  public EstadoDebug getEstado() {
    return this.estado;
  }
  
  public int getLinha() {
    return this.linhaAnterior;
  }

}

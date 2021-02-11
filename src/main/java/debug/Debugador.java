package debug;

import evento.EventoInterpretador;
import evento.EventoListener;
import evento.GerenciadorEventos;
import interpretador.Interpretador;
import modelos.tree.AstDebugNode;

/**
 * Realiza o processo de Debug do pseudoCodigo
 * 
 * @author Kerlyson
 *
 */
public class Debugador implements EventoListener {

  private Interpretador interpretador;

  private EstadoDebug estado;// estado em que se encontra o Debugador
  private Integer linhaAnterior = 0; // linha do node verificado
  private GerenciadorEventos ge;
  private DebugStrategy strategy;
  private AstDebugNode ultimoNode;

  /**
   * 
   * @param ge - Gerenciador de Eventos compartilhado
   * @param ativo - SE o debugador deve ser executado quando o codigo for interpretado
   */
  public Debugador(GerenciadorEventos ge, boolean ativo) {
    this.ge = ge;
    this.ge.inscrever(EventoInterpretador.VISITA_NODE_AST, this);
    this.ge.inscrever(EventoInterpretador.CONTINUAR_DEBUG_ON, this);
    this.ge.inscrever(EventoInterpretador.CONTINUAR_DEBUG_OFF, this);
    this.ge.inscrever(EventoInterpretador.FINALIZAR_DEBUG, this);
    this.ge.inscrever(EventoInterpretador.TOGGLE_DEBUG, this); 
    this.ge.inscrever(EventoInterpretador.INTERPRETACAO_INTERROMPIDA, this);

    this.setDebugadorAtivo(ativo);
  }

  @Override
  public void update(EventoInterpretador tipoEvento, Object payload) {

    switch (tipoEvento) {

      case TOGGLE_DEBUG: 
        this.setDebugadorAtivo((Boolean) payload);
        break;
      case VISITA_NODE_AST:
        if (estado == EstadoDebug.OFF)
          return;
        this.updateNodeDebug(payload);

        break;
      case CONTINUAR_DEBUG_ON:
        this.continuarExecucao();
        break;
      case CONTINUAR_DEBUG_OFF:
        this.continuarExecucaoSemDebug();
        break;
      case FINALIZAR_DEBUG:
        this.terminarExecucao();
        break;
      case INTERPRETACAO_INTERROMPIDA:
        this.setEstado(EstadoDebug.FINALIZADO);
        break;
      default:
        break;
    }

  }

  /**
   * Chamado quando o evento Node_Debug é disparado
   * 
   * @param payload - NodeAstDebug
   */
  private void updateNodeDebug(Object payload) {

    if (estado == EstadoDebug.ON)
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
   * algoritimo de debug que deve ser utilizado
   * 
   * @param strategy
   */
  public void setDebugStrategy(DebugStrategy strategy) {
    this.strategy = strategy;
  }

  /**
   * SE o debugador deve ser executado quando o codigo for interpretado
   * 
   * @param ativo
   */
  public void setDebugadorAtivo(boolean ativo) {
    this.setEstado(ativo ? EstadoDebug.ON : EstadoDebug.OFF);
  }

  // CONTROLADORES DA EXECUÇÃO

  /**
   * Pausa a execução do interpretador
   */
  protected void pausarExecucao() {

    this.setEstado(EstadoDebug.PAUSADO);
    this.ge.notificar(EventoInterpretador.ACAO_DEBUG, new DebugSnapshot(ultimoNode, this.interpretador.getAmbienteSnapshot()));
    interpretador.suspenderExecucao();

  }

  /**
   * Para a execução do interpretador
   */
  protected void terminarExecucao() {
    this.setEstado(EstadoDebug.OFF);
    this.interpretador.terminarExecucao();
  }

  /**
   * Continua a execução do interpretador, se estiver pausado.
   */
  protected void continuarExecucao() {

    if (this.estado == EstadoDebug.PAUSADO) {
      this.setEstado(EstadoDebug.EXECUTANDO);
      this.interpretador.continuarExecucao();
    }

  }

  /**
   * Continua a execução do interpretador, se estiver pausado, sem o debug.
   */
  protected void continuarExecucaoSemDebug() {

    if (this.estado == EstadoDebug.PAUSADO) {

      this.ge.notificar(EventoInterpretador.TOGGLE_DEBUG, false);
      // this.setEstado(EstadosDebug.EXECUTANDO);
      this.interpretador.continuarExecucao();

    }

  }

  /**
   * Set para o estado do Debugador. Dispara MUDANCA_ESTADO_DEBUG no Gerenciador de Eventos
   * 
   * @param estado
   */
  protected void setEstado(EstadoDebug estado) {
    this.estado = estado;
    this.ge.notificar(EventoInterpretador.MUDANCA_ESTADO_DEBUG, this.estado);
  }

  /**
   * 
   * @return nummero da linha do ultimo node analisado
   */
  protected int getLinha() {
    return this.linhaAnterior;
  }

  public void setInterpretador(Interpretador i) {
    this.interpretador = i;
  }

}

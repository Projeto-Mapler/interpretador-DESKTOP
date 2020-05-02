package debug;

import interpretador.Interpretador;
import tree.AstDebugNode;

/**
 * Realiza o processo de Debug do pseudoCodigo
 * @author Kerlyson
 *
 */
public class Debugador implements EventoListener {

	private Interpretador interpretador;
	
	private EstadosDebug estado;// estado em que se encontra o Debugador
	private Integer linhaAnterior = 0; // linha do node verificado
	private GerenciadorEventos ge;
	private DebugStrategy strategy; 

	/**
	 * 
	 * @param i -  instancia do interpretador que está executando o codigo
	 * @param ge - Gerenciador de Eventos compartilhado
	 * @param ativo - SE o debugador deve ser executado quando o codigo for interpretado
	 */
	public Debugador(Interpretador i, GerenciadorEventos ge, boolean ativo) {
		this.interpretador = i;
		this.ge = ge;
		this.ge.inscrever(TiposEvento.NODE_DEBUG, this);
		this.ge.inscrever(TiposEvento.CONTINUAR_DEBUG_ATIVO, this);
		this.ge.inscrever(TiposEvento.CONTINUAR_DEBUG_DESATIVADO, this);
		this.ge.inscrever(TiposEvento.FINALIZAR_DEBUG, this);
		this.ge.inscrever(TiposEvento.TOGGLE_DEBUG, this); // nao usado
		
		this.setDebugadorAtivo(ativo);
	}

	@Override
	public void update(TiposEvento tipoEvento, Object payload) {
	
		switch (tipoEvento) {
		
		case TOGGLE_DEBUG: // nao usado
			this.setDebugadorAtivo((Boolean) payload);
			break;
		case NODE_DEBUG:
			if (estado == EstadosDebug.DESATIVO)
				return;
			this.updateNodeDebug(payload);

			break;
		case CONTINUAR_DEBUG_ATIVO:
			this.continuarExecucao();
			break;
		case CONTINUAR_DEBUG_DESATIVADO:
			this.continuarExecucaoSemDebug();
			break;
		case FINALIZAR_DEBUG:
			this.terminarExecucao();
			break;

		default:
			break;
		}

	}

	/**
	 * Chamado quando o evento Node_Debug é disparado
	 * @param payload - NodeAstDebug
	 */
	private void updateNodeDebug(Object payload) {

		if (estado == EstadosDebug.ATIVO)
			this.setEstado(EstadosDebug.EXECUTANDO);
		if (payload instanceof AstDebugNode) {
			AstDebugNode node = (AstDebugNode) payload;
			if (node.getLinha() < 1)
				return;
		
			this.linhaAnterior = this.strategy.executar(node, this);
		}
	}
	
	
	/**
	 * algoritimo de debug que deve ser utilizado
	 * @param strategy
	 */
	public void setDebugStrategy(DebugStrategy strategy) {
		this.strategy = strategy;
	}

	
	/**
	 * SE o debugador deve ser executado quando o codigo for interpretado
	 * @param ativo
	 */
	public void setDebugadorAtivo(boolean ativo) {
		this.setEstado(ativo ? EstadosDebug.ATIVO : EstadosDebug.DESATIVO);
	}

	// CONTROLADORES DA EXECUÇÃO

	/**
	 * Pausa a execução do interpretador
	 */
	protected void pausarExecucao() {

		this.setEstado(EstadosDebug.PAUSADO);

		interpretador.suspender();

	}
	/**
	 * Para a execução do interpretador
	 */
	protected void terminarExecucao() {
		this.setEstado(EstadosDebug.DESATIVO);
		this.interpretador.terminar();
	}
	
	/**
	 * Continua a execução do interpretador, se estiver pausado.
	 */
	protected void continuarExecucao() {

		if (this.estado == EstadosDebug.PAUSADO) {

			this.setEstado(EstadosDebug.EXECUTANDO);
		this.interpretador.resumir();

		}

	}
	
	/**
	 * Continua a execução do interpretador, se estiver pausado, sem o debug.
	 */
	protected void continuarExecucaoSemDebug() {

		if (this.estado == EstadosDebug.PAUSADO) {

			this.ge.notificar(TiposEvento.TOGGLE_DEBUG, false);
//			this.setEstado(EstadosDebug.EXECUTANDO);
			this.interpretador.resumir();

		}

	}
	
	/**
	 * Set para o estado do Debugador.
	 * Dispara MUDANCA_ESTADO_DEBUG no Gerenciador de Eventos
	 * @param estado
	 */
	protected void setEstado(EstadosDebug estado) {
		this.estado = estado;
		
		this.ge.notificar(TiposEvento.MUDANCA_ESTADO_DEBUG, this.estado);
	}
	
	/**
	 * 
	 * @return nummero da linha do ultimo node analisado
	 */
	protected int getLinha() {
		return this.linhaAnterior;
	}
	

}

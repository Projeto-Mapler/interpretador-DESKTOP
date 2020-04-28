package debug;

import interpreter.Interpretador;
import tree.AstDebugNode;

public class Debugador implements EventoListener {

	private Interpretador interpretador;
	
	private EstadosDebug estado;
	private Integer linhaAnterior = 0;
	private GerenciadorEventos ge;
	private DebugStrategy strategy;

	public Debugador(Interpretador i, GerenciadorEventos ge, boolean ativo) {
		this.interpretador = i;
		this.ge = ge;
		this.ge.inscrever(TipoEvento.NODE_DEBUG, this);
		this.ge.inscrever(TipoEvento.CONTINUAR_DEBUG, this);
		this.ge.inscrever(TipoEvento.FINALIZAR_DEBUG, this);
//		this.ge.inscrever(TipoEvento.TOGGLE_DEBUG, this); // nao usado
		
		this.setDebugadorAtivo(ativo);
	}

	@Override
	public void update(TipoEvento tipoEvento, Object payload) {
	
		switch (tipoEvento) {
// nao usado
//		case TOGGLE_DEBUG:
//			this.setDebugadorAtivo((Boolean) payload);
//			break;
		case NODE_DEBUG:
			if (estado == EstadosDebug.DESATIVO)
				return;
			this.updateNodeDebug(payload);

			break;
		case CONTINUAR_DEBUG:
			this.continuarExecucao();
			break;
		case FINALIZAR_DEBUG:
			this.terminarExecucao();
			break;

		default:
			break;
		}

	}

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

	public void setDebugStrategy(DebugStrategy strategy) {
		this.strategy = strategy;
	}

	

	public void setDebugadorAtivo(boolean ativo) {
		this.setEstado(ativo ? EstadosDebug.ATIVO : EstadosDebug.DESATIVO);
	}

	

	protected void pausaExecucao() {

		this.setEstado(EstadosDebug.PAUSADO);

		interpretador.suspender();

	}

	protected void terminarExecucao() {
		this.setEstado(EstadosDebug.ATIVO);
		this.interpretador.terminar();
	}

	protected void continuarExecucao() {

		if (this.estado == EstadosDebug.PAUSADO) {

			this.setEstado(EstadosDebug.EXECUTANDO);
		this.interpretador.resumir();

		}

	}

	protected void setEstado(EstadosDebug estado) {
		this.estado = estado;
		this.ge.notificar(TipoEvento.MUDANCA_ESTADO_DEBUG, this.estado);
	}
	
	protected int getLinha() {
		return this.linhaAnterior;
	}
	

}

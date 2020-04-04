package debug;

import java.util.HashSet;
import java.util.Set;

import interpreter.Interpretador;
import tree.AstDebugNode;

public class Debugador implements EventoListener {

	private Interpretador interpretador;
	private Set<Integer> breakpoints;
	private EstadosDebug estado;
	private Integer breakpoint = 0, linhaAnterior = 0;
	private GerenciadorEventos ge;

	public Debugador(Interpretador i, GerenciadorEventos ge, boolean ativo) {
		this.interpretador = i;
		this.ge = ge;
		this.ge.inscrever(TipoEvento.NODE_DEBUG, this);
		this.ge.inscrever(TipoEvento.CONTINUAR_DEBUG, this);
		this.ge.inscrever(TipoEvento.FINALIZAR_DEBUG, this);
		this.ge.inscrever(TipoEvento.TOGGLE_DEBUG, this);
		this.breakpoints = new HashSet<Integer>();
		this.setDebugadorAtivo(ativo);
	}

	@Override
	public void update(TipoEvento tipoEvento, Object payload) {
		if (estado == EstadosDebug.DESATIVO)
			return;
		switch (tipoEvento) {
		case TOGGLE_DEBUG:
			this.setDebugadorAtivo((Boolean) payload);
		case NODE_DEBUG:
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
			this.checkBreakPoint(node.getLinha());
			System.out.println("[debug] linha: " + node.getLinha() + " .. " + payload.getClass().getName());
			this.linhaAnterior = node.getLinha();
		}
	}

//	public void setInterpretadorThread(Thread thread) {
//		this.interpretadorThread = thread;
//	}

	public void addBreakPoint(int linha) {
		this.breakpoints.add(linha);
	}

	public void removeBreakPoint(int linha) {
		this.breakpoints.remove(linha);
	}

	public void removeTodosBreakPoins() {
		this.breakpoints.clear();
	}

	public void setDebugadorAtivo(boolean ativo) {
		this.setEstado(ativo ? EstadosDebug.ATIVO : EstadosDebug.DESATIVO);
	}

	private void checkBreakPoint(int linha) {
		if (this.breakpoints.contains(linha)) {
			if (this.breakpoint == linha && this.linhaAnterior == this.breakpoint)
				return;
			this.breakpoint = linha;
			this.pausaExecucao();

		}
	}

	private void pausaExecucao() {

		this.setEstado(EstadosDebug.PAUSADO);

		interpretador.suspender();

	}

	private void terminarExecucao() {
		this.setEstado(EstadosDebug.ATIVO);
		this.interpretador.terminar();
	}

	private void continuarExecucao() {

		if (this.estado == EstadosDebug.PAUSADO) {

			this.setEstado(EstadosDebug.EXECUTANDO);
		this.interpretador.resumir();

		}

	}

	private void setEstado(EstadosDebug estado) {
		this.estado = estado;
		this.ge.notificar(TipoEvento.MUDANCA_ESTADO_DEBUG, this.estado);
	}

}

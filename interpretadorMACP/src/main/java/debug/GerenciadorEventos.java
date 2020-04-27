package debug;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GerenciadorEventos {
	Map<TipoEvento, Set<EventoListener>> inscritos;

	public GerenciadorEventos() {
		inscritos = new HashMap<TipoEvento, Set<EventoListener>>();
		for (TipoEvento t : TipoEvento.values()) {
			inscritos.put(t, new HashSet<EventoListener>());
		}
	}

	public void inscrever(TipoEvento te, EventoListener ev) {
		this.inscritos.get(te).add(ev);
	}

	public void desinscrever(TipoEvento te, EventoListener ev) {
		this.inscritos.get(te).remove(ev);
	}

	public void notificar(TipoEvento te, Object payload) {

		for (EventoListener ev : this.inscritos.get(te)) {
			ev.update(te, payload);
		}
	}
}

package debug;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GerenciadorEventos {
	Map<TiposEvento, Set<EventoListener>> inscritos;

	public GerenciadorEventos() {
		inscritos = new HashMap<TiposEvento, Set<EventoListener>>();
		for (TiposEvento t : TiposEvento.values()) {
			inscritos.put(t, new HashSet<EventoListener>());
		}
	}

	public void inscrever(TiposEvento te, EventoListener ev) {
		this.inscritos.get(te).add(ev);
	}

	public void desinscrever(TiposEvento te, EventoListener ev) {
		this.inscritos.get(te).remove(ev);
	}

	public void notificar(TiposEvento te, Object payload) {

		for (EventoListener ev : this.inscritos.get(te)) {
			ev.update(te, payload);
		}
	}
}

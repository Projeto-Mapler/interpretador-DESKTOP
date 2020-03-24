package debug;

import java.util.HashSet;
import java.util.Set;

public class GerenciadorEventos {
	Set<EventoListener> inscritos;
	
	public GerenciadorEventos() {
		inscritos = new HashSet<EventoListener>();
	}
	
	public void inscrever(EventoListener ev) {
		this.inscritos.add(ev);
	}
	public void desinscrever(EventoListener ev) {
		this.inscritos.remove(ev);
	}
	
	public void notificar(int linha) {
		if(linha < 1 ) return;
		System.err.println("[debug] linha: " + linha);
		for(EventoListener ev : inscritos) {
			ev.update(linha);
		}
	}
}

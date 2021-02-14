package evento;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Padrão Observer. Gerencia e notifica classes interessadas nos eventos que
 * acontecem no interpretador.
 * 
 * @author Kerlyson
 *
 */
public class EventosService {
    Map<EventoInterpretador, Set<EventoListener>> inscritos;

    public EventosService() {
	inscritos = new HashMap<EventoInterpretador, Set<EventoListener>>();
	for (EventoInterpretador t : EventoInterpretador.values()) {
	    inscritos.put(t, new HashSet<EventoListener>());
	}
    }

    /**
     * Inscreve uma classe para ser notificada na ocorrencia de um evento
     * 
     * @param te - evento que a classe deseja 'ouvir'
     * @param ev - classe que deseja se inscrever
     */
    public void inscrever(EventoInterpretador te, EventoListener ev) {
	this.inscritos.get(te).add(ev);
    }

    /**
     * Inscreve o listener nos eventos passados no array
     * 
     * @param tes - array de eventos para inscrever
     * @param ev  - listener para increver nos eventos
     */
    public void inscreverTodos(EventoInterpretador[] tes, EventoListener ev) {
	for (EventoInterpretador te : tes) {
	    this.inscrever(te, ev);
	}
    }

    /**
     * Desiscreve o eventoListener de todos os eventos que estiver inscrito
     * 
     * @param ev - listener que deseja desiscrever
     */
    public void desiscreverTodos(EventoListener ev) {
	Set<EventoInterpretador> keys = this.inscritos.keySet();
	for (EventoInterpretador key : keys) {
	    this.desinscrever(key, ev);
	}
    }

    /**
     * Remove uma classe inscrita em determinado evento
     * 
     * @param te - evento que a classe se inscreveu
     * @param ev - classe inscrita no evento
     */
    public void desinscrever(EventoInterpretador te, EventoListener ev) {
	this.inscritos.get(te).remove(ev);
    }

    /**
     * Chamado quando se deseja disparar um evento
     * 
     * @param te      - tipo do evento
     * @param payload - carga do evento
     */
    public void notificar(EventoInterpretador te, Object payload) {
//		Object x = payload == null ? "" : payload;
//		System.err.println(te.toString() + "---"+x.toString());
	for (EventoListener ev : this.inscritos.get(te)) {
	    ev.update(te, payload);
//			System.err.println(ev.getClass().getName());
	}
    }
}

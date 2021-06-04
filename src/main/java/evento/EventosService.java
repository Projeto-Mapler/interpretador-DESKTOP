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
	Set<EventoListener> marcadosParaDesinscrever;

	public EventosService() {
		marcadosParaDesinscrever = new HashSet<EventoListener>();
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
	public void desinscreverTodos(EventoListener ev) {
		Set<EventoInterpretador> keys = this.inscritos.keySet();
		for (EventoInterpretador key : keys) {
			this.desinscrever(key, ev);
		}
	}
	
	/**
	 * Marca um EventoListener para ser desinscrito de todos os eventos que esta inscrito
	 * Desiscricao é executado apos o ultimo update (notificar()) feito pelo Evento Service
	 * @param ev
	 */
	public void marcarParaDesinscrever(EventoListener ev) {
		this.marcadosParaDesinscrever.add(ev);
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
//		System.err.println("Evento: " + te.toString() + " - Payload: " + x.toString());
		for (EventoListener ev : this.inscritos.get(te)) {
//			System.err.println("Update do Listener: " + ev.getClass().getName());
			ev.update(te, payload);
		}
	
		if(this.marcadosParaDesinscrever.size() > 0) {
			for(EventoListener ev : this.marcadosParaDesinscrever) {
				this.desinscreverTodos(ev);
			}
			this.marcadosParaDesinscrever.clear();
		}
	
	}
}

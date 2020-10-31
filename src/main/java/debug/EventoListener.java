package debug;

/**
 * Padrão Observer. Toda classe que deseja 'ouvir' os eventos do Gerenciador de
 * Eventos deve implementar esta interface
 * 
 * @author Kerlyson
 *
 */
public interface EventoListener {
    /**
     * Metódo chamado quando um evento é disparado no Gerenciador de Eventos
     * 
     * @param tipoEvento - tipo do evento
     * @param payload    - carga do evento
     */
    void update(TiposEvento tipoEvento, Object payload);
}

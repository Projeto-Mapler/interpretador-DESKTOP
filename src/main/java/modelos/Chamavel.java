package modelos;

import java.util.List;

import interpretador.Interpretador;

/**
 * Objetos 'chamaveis' da pseudoLinguagem devem implementar esta interface.
 * Objetos chamaveis são funções ou modulos na linguagem.
 * @author Kerlyson
 *
 */
public interface Chamavel {
	Object chamar(Interpretador interpretador, List<Object> argumentos);
}

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
    /**
     * Executa o bloco de instruções da função (modulo)
     * @param interpretador - deve chamar a função executeBlock
     * @param argumentos - caso modulos aceitem argumentos | não usado atualmente
     * @return - case modulos aceitem retorno | não usado atualmente 
     */
	Object chamar(Interpretador interpretador, List<Object> argumentos);
}

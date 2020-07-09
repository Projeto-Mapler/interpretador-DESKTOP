package modelos;

import java.util.List;

import interpretador.Interpretador;
import tree.Declaracao;

/**
 * Representa objeto chamavel módulo da pseudolinguagem
 * @author Kerlyson
 *
 */
public class Modulo implements Chamavel{

	private final Declaracao.Modulo modulo;
	public Modulo(Declaracao.Modulo modulo) {
		this.modulo = modulo;
	}
	
	@Override
	public Object chamar(Interpretador interpretador, List<Object> argumentos) {
		interpretador.executeBlock(modulo.corpo.declaracoes, null);
		return null;
	}

}

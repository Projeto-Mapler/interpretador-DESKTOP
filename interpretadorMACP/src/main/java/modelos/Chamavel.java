package modelos;

import java.util.List;

import interpretador.Interpretador;

public interface Chamavel {
	Object chamar(Interpretador interpretador, List<Object> argumentos);
}

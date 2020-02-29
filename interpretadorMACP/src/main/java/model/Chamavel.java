package model;

import java.util.List;

import interpreter.Interpretador;

public interface Chamavel {
	Object chamar(Interpretador interpretador, List<Object> argumentos);
}

package conversores;

import debug.GerenciadorEventos;
import tree.Declaracao.Programa;

public class ConversorFactory {
    
    private ConversorFactory() {}

    
    public static Conversor getConversor(GerenciadorEventos gerenciadorEventos, Programa programa, ConversorStrategy conversor) {
	switch (conversor) {
	case C:
	    return new ConversorC(programa, gerenciadorEventos);
	case Cpp:
	    return new ConversorCpp(programa, gerenciadorEventos);
	case JAVA:
	    return new ConversorJava(programa, gerenciadorEventos);
	case PASCAL:
	    return new ConversorPascal(programa, gerenciadorEventos);
	case PYTHON:
	    return new ConversorPython(programa, gerenciadorEventos);
	default:
	  return null;
	}
	
    }
}

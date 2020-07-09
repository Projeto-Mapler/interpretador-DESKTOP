package conversores;

import main.Principal;
import tree.Declaracao.Programa;

public class ConversorFactory {
    
    private ConversorFactory() {}

    
    public static Conversor getConversor(Principal principal, Programa programa, ConversorStrategy conversor) {
	switch (conversor) {
	case C:
	    return new ConversorC(principal, programa);
	case Cpp:
	    return new ConversorCpp(principal, programa);
	case JAVA:
	    return new ConversorJava(principal, programa);
	case PASCAL:
	    return new ConversorPascal(principal, programa);
	case PYTHON:
	    return new ConversorPython(principal, programa);
	default:
	  return null;
	}
	
    }
}

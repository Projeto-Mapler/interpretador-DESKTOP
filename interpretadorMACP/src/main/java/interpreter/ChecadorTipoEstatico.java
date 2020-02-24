package interpreter;

import model.TokenType;
import parser.RuntimeError;


public class ChecadorTipoEstatico {
    
    public TokenType tipoDoValor(Object valor) {
	if(valor instanceof Integer) {
	    return TokenType.TIPO_INTEIRO;
	}
	if(valor instanceof Double) {
	    return TokenType.TIPO_REAL;
	}
	if(valor instanceof Boolean) {
	    return TokenType.TIPO_LOGICO;
	}
	if(valor instanceof Character) {
	    return TokenType.TIPO_CARACTERE;
	}
	if(valor instanceof String) {
	    return TokenType.TIPO_CADEIA;
	}
//	if(valor instanceof Arrays) {
//	    return TokenType.TIPO_VETOR;
//	}
	// TODO: throw error ?
	return null;
    }
    
    public Object castLerValor(String valor, TokenType tipo)  throws Exception{ 

	switch(tipo) {
	case TIPO_INTEIRO:
	    return Integer.parseInt(valor);
	case TIPO_CARACTERE:
	    return valor.charAt(0);
	case TIPO_LOGICO:
	    if(valor.equals("falso")) {
		return false;
	    }
	    if(valor.equals("verdadeiro")) {
		return true;
	    }
	    return null;
	case TIPO_REAL:
	    return Double.parseDouble(valor);
	case TIPO_CADEIA:
	    return valor;
	}
 
	return null;
	// throw error?
    }
    
    
   public boolean isTipoValorValido(TokenType tipo, Object valor) {
       return tipo == tipoDoValor(valor);
   }
    
}

package interpreter;

import model.TokenType;


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
    
    
   public boolean isTipoValorValido(TokenType tipo, Object valor) {
       return tipo == tipoDoValor(valor);
   }
    
}

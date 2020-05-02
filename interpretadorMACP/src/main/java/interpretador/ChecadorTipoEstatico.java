package interpretador;

import modelos.TiposToken;

public class ChecadorTipoEstatico {

	public TiposToken tipoDoValor(Object valor) {
		if (valor instanceof Integer) {
			return TiposToken.TIPO_INTEIRO;
		}
		if (valor instanceof Double) {
			return TiposToken.TIPO_REAL;
		}
		if (valor instanceof Boolean) {
			return TiposToken.TIPO_LOGICO;
		}
		if (valor instanceof Character) {
			return TiposToken.TIPO_CARACTERE;
		}
		if (valor instanceof String) {
			return TiposToken.TIPO_CADEIA;
		}
		if (valor instanceof modelos.Modulo) {
			return TiposToken.TIPO_MODULO;
		}
		// if(valor instanceof Arrays) {
		// return TokenType.TIPO_VETOR;
		// }
		// TODO: throw error ?
		return null;
	}

	public Object castLerValor(String valor, TiposToken tipo) throws Exception {

		switch (tipo) {
			case TIPO_INTEIRO :
				return Integer.parseInt(valor);
			case TIPO_CARACTERE :
				return valor.charAt(0);
			case TIPO_LOGICO :
				if (valor.equals("falso")) {
					return false;
				}
				if (valor.equals("verdadeiro")) {
					return true;
				}
				return null;
			case TIPO_REAL :
				return Double.parseDouble(valor);
			case TIPO_CADEIA :
				return valor;
		}

		return null;
		// throw error?
	}

	public boolean isTipoValorValido(TiposToken tipo, Object valor) {
		return tipo == tipoDoValor(valor);
	}

}

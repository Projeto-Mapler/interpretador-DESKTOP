package interpretador;

import modelos.TiposToken;

/**
 * Verifica os tipos de variaveis
 * 
 * @author Kerlyson
 *
 */
public class ChecadorTipoEstatico {

	public TiposToken getTipoDoValor(Object valor) {
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
		return null;
	}

	/**
	 * Converte o valor para o tipo desejado
	 * 
	 * @param valor - String que foi lida no input
	 * @param tipo  - tipo alvo
	 * @return - valor com cast
	 * @throws Exception
	 */
	public Object castLerValor(String valor, TiposToken tipo) throws Exception {

		switch (tipo) {
		case TIPO_INTEIRO:
			return Integer.parseInt(valor);
		case TIPO_CARACTERE:
			return valor.charAt(0);
		case TIPO_LOGICO:
			if (valor.equals("falso")) {
				return false;
			}
			if (valor.equals("verdadeiro")) {
				return true;
			}
			return null;
		case TIPO_REAL:
			return Double.parseDouble(valor);
		case TIPO_CADEIA:
			return valor;
		default:
			return null;
		}
	}

	public Object castValorObject(Object valor) {
	
		if (valor instanceof Boolean) {
			if((Boolean)valor) return "verdadeiro";
			if(!(Boolean)valor) return "falso";
		}
		if(valor == null) return "nulo";
		return valor;
	}

	public boolean isTipoValorValido(TiposToken tipo, Object valor) {
		return tipo == getTipoDoValor(valor);
	}

}

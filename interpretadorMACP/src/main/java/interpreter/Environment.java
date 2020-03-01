package interpreter;

import java.util.HashMap;
import java.util.Map;

import model.Modulo;
import model.Token;
import model.TokenType;
import parser.RuntimeError;

public class Environment {
	private final Map<String, Object> valores = new HashMap<>();
	private final Map<String, TokenType> definicoes = new HashMap<>();
	private ChecadorTipoEstatico checadorTipo = new ChecadorTipoEstatico();

	public void define(Token nome, Token tipo) {
//		System.err.println(nome.lexeme + " - " + tipo.lexeme + " definido");
//		if(!isNomeVariavelValido(nome.lexeme)) {
//			throw new RuntimeError(nome,
//					"variável '" + nome.lexeme + "' possue um nome inválido.");
//		}
		definicoes.put(nome.lexeme, tipo.type);
		valores.put(nome.lexeme, null);
	}

	public void defineArray(Token nome, VariavelVetor vetor) {
//		if(!isNomeVariavelValido(nome.lexeme)) {
//			throw new RuntimeError(nome,
//					"variável '" + nome.lexeme + "' possue um nome inválido.");
//		}
		definicoes.put(nome.lexeme, TokenType.TIPO_VETOR);
		valores.put(nome.lexeme, vetor);
	}

	public Object get(Token nome) {
		if (valores.containsKey(nome.lexeme)) {
			if (getVarTipo(nome) == TokenType.TIPO_MODULO
					&& valores.get(nome.lexeme) == null) {
				throw new RuntimeError(nome,
						"modulo não declarado '" + nome.lexeme + "'.");
			}
			return valores.get(nome.lexeme);
		}

		throw new RuntimeError(nome,
				"variavel indefinida '" + nome.lexeme + "'.");
	}

	public void assign(Token nome, Object valor) {
		if (valores.containsKey(nome.lexeme)) {
			if (checadorTipo.isTipoValorValido(getVarTipo(nome), valor)) {
				valores.put(nome.lexeme, valor);
				return;
			} else {

				throw new RuntimeError(nome,
						"atribuição inválida para '" + nome.lexeme + "'.");
			}
		}
		if (checadorTipo.isTipoValorValido(TokenType.TIPO_MODULO, valor)) {
			throw new RuntimeError(nome,
					"modulo não declarado '" + nome.lexeme + "'.");
		}
		throw new RuntimeError(nome,
				"variavel indefinida '" + nome.lexeme + "'.");
	}

	public void assignVetor(Token nome, Object index, Object valor) {
		if (valores.containsKey(nome.lexeme)) {
			VariavelVetor variavel = (VariavelVetor) this.get(nome);

			if (!(index instanceof Integer)) {
				throw new RuntimeError(nome,
						"Index informado não pode ser resolvido.");
			}
			if ((int) index < 0 || (int) index > variavel.getIntervaloF()
					|| (int) index < variavel.getIntervaloI()) {
				throw new RuntimeError(nome, "Index informado não encontrado");
			}

			if (checadorTipo.isTipoValorValido(variavel.getTipo(), valor)) {
				variavel.getValores()[(int) index
						- variavel.getIntervaloI()] = valor;
				return;
			} else {

				throw new RuntimeError(nome,
						"atribuição inválida para '" + nome.lexeme + "'.");
			}
		}

		throw new RuntimeError(nome,
				"variavel indefinida '" + nome.lexeme + "'.");
	}

	/**
	 * Executa cast e atribuicao do valor lido pela funcao ler
	 * 
	 * @param nome
	 *            - token identificador
	 * @param valorLido
	 *            - string que a funcao ler capturou do usuario
	 * @param index
	 *            - nulo se a variavel a ser atribuida nao for um vetor
	 */
	public void assignLer(Token nome, String valorLido, Object index) {
		TokenType tipo = getVarTipo(nome);

		if (tipo == TokenType.TIPO_VETOR) {
			tipo = ((VariavelVetor) this.get(nome)).getTipo();
		}

		Object valor;
		try {
			valor = checadorTipo.castLerValor(valorLido, tipo);
		} catch (Exception e) {
			throw new RuntimeError(nome,
					"atribuição inválida '" + nome.lexeme + "'.");
		}
		if (index != null) {
			this.assignVetor(nome, index, valor);
		} else {
			this.assign(nome, valor);
		}
	}

	public TokenType getVarTipo(Token nome) {
		return definicoes.get(nome.lexeme);
	}
	
//	private boolean isNomeVariavelValido(String nome) {
//		char inicial =  nome.charAt(0);
//		if(Character.isAlphabetic(inicial) || inicial == '_') {
//			return true;
//		}
//		return false;
//	}

}

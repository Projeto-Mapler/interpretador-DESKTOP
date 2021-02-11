package interpretador;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import modelos.TiposToken;
import modelos.Token;
import modelos.VariavelVetor;
import modelos.excecao.RuntimeError;

/**
 * Gerencia variaveis e seus valores durante a interpretação do programa
 * 
 * @author Kerlyson
 *
 */
public class Ambiente {
    private final Map<String, Object> valores = new HashMap<>(); // valores das variaveis <Nome da variavel, valor>
    private final Map<String, TiposToken> definicoes = new HashMap<>(); // variaveis declaradas <nome, tipo>
    private ChecadorTipoEstatico checadorTipo = new ChecadorTipoEstatico();

    public void definirVariavel(Token nome, Token tipo) {
	definicoes.put(nome.lexeme, tipo.type);
	valores.put(nome.lexeme, null);
    }

    public void definirVariavelVetor(Token nome, VariavelVetor vetor) {
	definicoes.put(nome.lexeme, TiposToken.TIPO_VETOR);
	valores.put(nome.lexeme, vetor);
    }

    public Object getValorVariavel(Token nome) {
	if (valores.containsKey(nome.lexeme)) {
	    if (getTipoVariavel(nome) == TiposToken.TIPO_MODULO && valores.get(nome.lexeme) == null) {
		throw new RuntimeError(nome, "modulo não declarado '" + nome.lexeme + "'.");
	    }
	    return valores.get(nome.lexeme);
	}

	throw new RuntimeError(nome, "variavel indefinida '" + nome.lexeme + "'.");
    }

    public void setValorVariavel(Token nome, Object valor) {
	if (valores.containsKey(nome.lexeme)) {
	    if (checadorTipo.isTipoValorValido(getTipoVariavel(nome), valor)) {
		valores.put(nome.lexeme, valor);
		return;
	    } else {
		throw new RuntimeError(nome, "atribuição inválida para '" + nome.lexeme + "'.");
	    }
	}
	if (checadorTipo.isTipoValorValido(TiposToken.TIPO_MODULO, valor)) {
	    throw new RuntimeError(nome, "modulo não declarado '" + nome.lexeme + "'.");
	}
	throw new RuntimeError(nome, "variavel indefinida '" + nome.lexeme + "'.");
    }

    public void setValorVariavelVetor(Token nome, Object index, Object valor) {
	if (valores.containsKey(nome.lexeme)) {
	    VariavelVetor variavel = (VariavelVetor) this.getValorVariavel(nome);

	    if (!(index instanceof Integer)) {
		throw new RuntimeError(nome, "Index informado não pode ser resolvido.");
	    }
	    if ((int) index < 0 || (int) index > variavel.getIntervaloF() || (int) index < variavel.getIntervaloI()) {
		throw new RuntimeError(nome, "Index informado não encontrado");
	    }

	    if (checadorTipo.isTipoValorValido(variavel.getTipo(), valor)) {
		variavel.setValor((int) index, valor);
		return;
	    } else {
		throw new RuntimeError(nome, "atribuição inválida para '" + nome.lexeme + "'.");
	    }
	}

	throw new RuntimeError(nome, "variavel indefinida '" + nome.lexeme + "'.");
    }

    /**
     * Executa cast e atribuicao do valor lido pela funcao ler
     * 
     * @param nome      - token identificador
     * @param valorLido - string que a funcao ler capturou do usuario
     * @param index     - nulo se a variavel a ser atribuida nao for um vetor
     */
    public void setVariavelPorFuncaoLer(Token nome, String valorLido, Object index) {
	TiposToken tipo = getTipoVariavel(nome);

	if (tipo == TiposToken.TIPO_VETOR) {
	    tipo = ((VariavelVetor) this.getValorVariavel(nome)).getTipo();
	}

	Object valor;
	try {
	    valor = checadorTipo.castLerValor(valorLido, tipo);
	} catch (Exception e) {
	    throw new RuntimeError(nome, "atribuição inválida '" + nome.lexeme + "'.");
	}
	if (index != null) {
	    this.setValorVariavelVetor(nome, index, valor);
	} else {
	    this.setValorVariavel(nome, valor);
	}
    }

    public TiposToken getTipoVariavel(Token nome) {
	return definicoes.get(nome.lexeme);
    }

    /**
     * Cria uma 'imagem' do estado atual das variaveis
     * 
     * @return Map<string, Object> = <'nome da variavel', 'valor da variavel'>
     */
    protected Map<String, Object> criarSnapshot() {
	Map<String, Object> retorno = new HashMap<String, Object>();

	Set<String> nomeVariaveis = this.definicoes.keySet();

	for (String nome : nomeVariaveis) {
	    retorno.put(nome, this.valores.get(nome));
	}

	return retorno;
    }

}

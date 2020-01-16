package interpreter;

import java.util.HashMap;
import java.util.Map;

import model.Token;
import model.TokenType;
import parser.RuntimeError;

public class Environment {
    private final Map<String, Object> valores = new HashMap<>();
    private final Map<String, TokenType> definicoes = new HashMap<>();
    private ChecadorTipoEstatico checadorTipo = new ChecadorTipoEstatico();

    public void define(Token nome, Token tipo) {
	definicoes.put(nome.lexeme, tipo.type);
	valores.put(nome.lexeme, null);
    }

    public Object get(Token name) {
	if (valores.containsKey(name.lexeme)) {
	    return valores.get(name.lexeme);
	}

	throw new RuntimeError(name, "variavel indefinida '" + name.lexeme + "'.");
    }

    public void assign(Token nome, Object valor) {
	if (valores.containsKey(nome.lexeme)) {
	    if(checadorTipo.isTipoValorValido(getVarTipo(nome), valor)) {		
		valores.put(nome.lexeme, valor);
		return;
	    } else {
		
		throw new RuntimeError(nome, "atribuição inválida '" + nome.lexeme + "'.");
	    }
	}

	throw new RuntimeError(nome, "variavel indefinida '" + nome.lexeme + "'.");
    }
    
    public TokenType getVarTipo(Token nome) {
	return definicoes.get(nome.lexeme);
    }

}

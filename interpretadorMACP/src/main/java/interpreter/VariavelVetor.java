package interpreter;

import java.util.Arrays;

import model.TokenType;

public class VariavelVetor {
    private Object[] valores;
    private TokenType tipo;
    private int tamanho, intervaloI, intervaloF;
    
    
    public VariavelVetor(TokenType tipo, int intervaloI, int intervaloF) {
	this.tipo = tipo;
	this.intervaloI = intervaloI;
	this.intervaloF = intervaloF;
	
	// TODO: check intervalo I < intervaloF
	
	// intervalos inclusivos: [4..6] == [4,5,6]
	this.tamanho = intervaloF - intervaloI + 1;
	this.valores = new Object[tamanho];
    }
    
    public int resolverIndex(int valor) {
    	return (valor - this.intervaloI);
    }
    
    public Object getValorNoIndex(int pseudoIndex) {
    	return this.valores[this.resolverIndex(pseudoIndex)];
    }
    
    public int getIntervaloI() {
        return intervaloI;
    }

    public void setIntervaloI(int intervaloI) {
        this.intervaloI = intervaloI;
    }

    public int getIntervaloF() {
        return intervaloF;
    }

    public void setIntervaloF(int intervaloF) {
        this.intervaloF = intervaloF;
    }

    public Object[] getValores() {
        return valores;
    }
    public void setValores(Object[] valores) {
        this.valores = valores;
    }
    public TokenType getTipo() {
        return tipo;
    }
    public void setTipo(TokenType tipo) {
        this.tipo = tipo;
    }
    public int getTamanho() {
        return tamanho;
    }
    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    
    
    
}

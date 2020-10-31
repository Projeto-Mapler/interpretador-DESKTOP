package conversores;

/**
 * Responsável por construir a String do psudocodigo na linguagem alvo. Segue o
 * padrão Builder, permitindo concatenar chamadas de métodos:
 * indentar().addQuebraLinha()...
 * 
 * @author Kerlyson
 *
 */
public class Escritor {
    private int indexIndentacao = 0;
    private StringBuilder stringBuilder;

    public Escritor() {
	this.stringBuilder = new StringBuilder();
    }

    /**
     * Retorna a String construida
     * 
     * @return - String resultado
     */
    protected String getResultado() {
	return this.stringBuilder.toString();
    }

    /**
     * Incrementa a quantidade de '\t' concatenado em cada nova linha
     * 
     * @return
     */
    protected Escritor indentar() {
	this.indexIndentacao++;
	return this;
    }

    /**
     * Decrementa a quantidade de '\t' concatenado em cada nova linha
     * 
     * @return
     */
    protected Escritor removerIdentacao() {
	if (this.indexIndentacao > 0)
	    this.indexIndentacao--;
	return this;
    }

    /**
     * Concatena um '/n' na String resultado
     * 
     * @param quantidade - quantas 'n' devem ser acrescentadas
     * @return
     */
    protected Escritor addQuebraLinha(int quantidade) {
	if (quantidade < 0)
	    quantidade = 0;
	for (int x = 0; x < quantidade; x++)
	    this.stringBuilder.append("\n");
	return this;
    }

    /**
     * Concatena apenas um '/n' na String resultado
     * 
     * @return
     */
    protected Escritor addQuebraLinha() {
	this.stringBuilder.append("\n");
	return this;
    }

    /**
     * Concatena a 'linha' no String resultado
     * 
     * @param linha - String a ser concatenada no resultado
     * @return
     */
    protected Escritor concatenarNaLinha(String linha) {
	if (linha.length() == 0)
	    return this;

	if (stringBuilder.length() > 0 && indexIndentacao > 0) {
	    char ultimoChar = stringBuilder.charAt(stringBuilder.length() - 1);
	    if (ultimoChar == '\n') {
		for (int x = 0; x < indexIndentacao; x++)
		    stringBuilder.append("\t");
	    }
	}

	this.stringBuilder.append(linha);
	return this;
    }

    /**
     * Reseta a identação e a String resultado
     * 
     * @return
     */
    protected void reset() {
	this.indexIndentacao = 0;
	this.stringBuilder = new StringBuilder();
    }

}

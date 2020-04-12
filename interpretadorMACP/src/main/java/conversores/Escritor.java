package conversores;

public class Escritor {
	private int indexIndentacao = 0;
	private StringBuilder stringBuilder;

	public Escritor() {
		this.stringBuilder = new StringBuilder();
	}

	protected String getResultado() {
		return this.stringBuilder.toString();
	}

	protected Escritor indentar() {
		this.indexIndentacao++;
		return this;
	}

	protected Escritor removerIdentacao() {
		if (this.indexIndentacao > 0)
			this.indexIndentacao--;
		return this;
	}

	protected Escritor addQuebraLinha(int quantidade) {
		if (quantidade < 0)
			quantidade = 0;
		for (int x = 0; x < quantidade; x++)
			this.stringBuilder.append("\n");
		return this;
	}

	protected Escritor addQuebraLinha() {
		this.stringBuilder.append("\n");
		return this;
	}

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

	protected void reset() {
		this.indexIndentacao = 0;
		this.stringBuilder = new StringBuilder();
	}

}

package modelos;

/**
 * Esse objeto é usado para passar valores de entrada para o interprador quando um evento de TiposEventos.LER_EVENTO é disparado
 * @author Kerlyson
 *
 */
public class LeitorEntradaConsole {
	private String valor;
	private boolean valorSetado;
	
	public LeitorEntradaConsole() {
		valorSetado = false;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
		this.valorSetado = true;
	}
	
	public boolean getValorSetado() {
		return this.valorSetado;
	}
	
	public void reset() {
		this.valorSetado = false;
		this.valor = null;
	}
	
	
}

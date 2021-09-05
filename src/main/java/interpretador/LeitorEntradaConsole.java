package interpretador;

/**
 * Esse objeto é usado para passar valores de entrada para o interprador quando um evento de
 * TiposEventos.LER_EVENTO é disparado
 * 
 * @author Kerlyson
 *
 */
public class LeitorEntradaConsole {

  private String valor;
  private Interpretador interpretador;
  /**
   * se o valor foi setado, interpretador espera esse valor ser verdadeiro para continuar a execução
   * após lançar o evento LER
   */
  private boolean valorSetado;

  public LeitorEntradaConsole(Interpretador interpretador) {
    valorSetado = false;
    this.interpretador = interpretador;
  }

  public void setValor(String valor) {
    this.valor = valor;
    this.valorSetado = true;
    this.interpretador.continuarExecucao();
  }

  public boolean getValorSetado() {
    return this.valorSetado;
  }

  public void reset() {
    this.valorSetado = false;
    this.valor = null;
  }

  public String getValor() {
    return this.valor;
  }
  
}

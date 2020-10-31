package modelos;

/**
 * Representa uma variavel vetor da pseudoLinguagem
 * 
 * @author Kerlyson
 *
 */
public class VariavelVetor {
  
  private Object[] valores;
  private TiposToken tipo;
  private int tamanho, intervaloI, intervaloF;
  
  public VariavelVetor(TiposToken tipo, int intervaloI, int intervaloF) {
    this.tipo = tipo;
    this.intervaloI = intervaloI;
    this.intervaloF = intervaloF;
    // intervalos inclusivos: [4..6] == [4,5,6]
    this.tamanho = intervaloF - intervaloI + 1;
    this.valores = new Object[tamanho];
  }

  /**
   * Retorna o index valido em java
   * 
   * @param valor
   * @return - index valido em java
   */
  public int resolverIndex(int valor) {
    return (valor - this.intervaloI);
  }

  /**
   * Retorna o valor no index desejado
   * 
   * @param pseudoIndex - index passado na pseudoLinguagem
   * @return - valor no index desejado
   */
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

  public void setValor(int pseudoIndex, Object valor) {
    // System.err.println(this.resolverIndex(pseudoIndex) + " -- "+ valor);
    this.valores[this.resolverIndex(pseudoIndex)] = valor;
  }

  public TiposToken getTipo() {
    return tipo;
  }

  public void setTipo(TiposToken tipo) {
    this.tipo = tipo;
  }

  public int getTamanho() {
    return tamanho;
  }

  public void setTamanho(int tamanho) {
    this.tamanho = tamanho;
  }

  @Override
  public String toString() {
    String retorno = "[";
    for (int i = 0; i < valores.length; i++) {
      // System.err.println(valores[i]);
      if (valores[i] == null)
        continue;
      retorno = retorno + valores[i].toString();
      if (i + 1 < valores.length) {
        retorno += ", ";
      }
    }
    return retorno + "]";
  }
}

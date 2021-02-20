package interpretador;

import debug.DebugSnapshot;
import debug.EstadoDebug;

public interface AcaoInterpretador {
  public void onInput(LeitorEntradaConsole leitor);
  public void onOutput(String output);
  public void onInterpretacaoConcluida(double tempoExecucao);
  public void onInterpretacaoInterrompida(double tempoExecucao);
  public void onDebugMudancaEstado(EstadoDebug novoEstado);
  public void onDebugPassoExecutado(DebugSnapshot snapshot);
  /**
   *  
   * @param erro - pode ser qualquer classe do pacote modelos.excecao;
   * necessita ser tratado com instanceof por exemplo
   */
  public void onErro(RuntimeException erro);
  public void onLog(String msgLog);
}

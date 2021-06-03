package tool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import conversores.ConversorStrategy;
import debug.DebugSnapshot;
import debug.EstadoDebug;
import debug.PassoAPassoDebugStrategy;
import evento.EventoInterpretador;
import interpretador.AcaoInterpretador;
import interpretador.InterpretadorService;
import interpretador.LeitorEntradaConsole;
import modelos.TiposToken;
import modelos.excecao.ParserError;
import modelos.excecao.RuntimeError;
import scala.actors.threadpool.Arrays;

/**
 * Classe de teste de implementação da biblioteca
 * 
 * @author Kerlyson
 *
 */
public class MainUI extends JFrame implements AcaoInterpretador {

  private static final long serialVersionUID = 1L;
  private final String PATH_EXEMPLOS = "exemplos\\";
  private JPanel panel;
  private JButton botaoArquivo, botaoIniciarExemplo, botaoDebugParar, botaoDebugContinuar, botaoDebugContinuarSem;
  private JLabel labelOu;
  private JComboBox<String> comboBoxExemplos, comboBoxTraducoes;
  private JFileChooser fileChooser;
  private JCheckBox checkBoxDebugAtivo, checkBoxImprimirTraducao, checkBoxJGraphT, checkBoxLogAtivo;
  private Map<JCheckBox, EventoInterpretador> logTipos;

  private InterpretadorService interpretador;

  public MainUI() {

    this.interpretador = new InterpretadorService(this);
    this.interpretador.setLogAtivo(true);
    this.interpretador.setLogColorido(true);
    this.interpretador.setEventosLog(Arrays.asList(EventoInterpretador.values()));

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    this.logTipos = new HashMap<>();
    for (EventoInterpretador e : EventoInterpretador.values()) {
      JCheckBox cb = new JCheckBox(e.toString());
      cb.setEnabled(false);
      this.logTipos.put(cb, e);
    }

    this.setup();
    this.setVisible(true);
  }

  public static void main(String[] args) {
    new MainUI();
  }

  private void setup() {
    this.setTitle("Interpretador");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    this.panel = new JPanel();
    this.panel.setLayout(new GridBagLayout());
    GridBagConstraints cons = new GridBagConstraints();
    cons.fill = GridBagConstraints.HORIZONTAL;
    cons.insets = new Insets(0, 0, 5, 0);

    this.panel.setBorder(new EmptyBorder(new Insets(20, 50, 20, 50)));
    // BOTOES
    this.setupBotoes();
    // LABELS
    this.labelOu = new JLabel("OU");
    this.labelOu.setHorizontalAlignment(JLabel.CENTER);
    // SELETOR EXEMPLOS
    this.comboBoxExemplos = new JComboBox<String>(this.getNomeArquivosExemplo());
    // SELETOR TRADUCAO
    this.comboBoxTraducoes = new JComboBox<String>(this.getNomesTraducoes());
    this.comboBoxTraducoes.setEnabled(false);
    // LAYOUT
    cons.gridx = 0;
    cons.weightx = 1;
    cons.gridy = 0;
    this.panel.add(this.botaoArquivo, cons);

    JPanel panelBotoesDebug = new JPanel();
    panelBotoesDebug.setLayout(new GridBagLayout());
    GridBagConstraints cons2 = new GridBagConstraints();
    cons2.fill = GridBagConstraints.HORIZONTAL;
    cons2.insets = new Insets(0, 5, 5, 5);
    cons2.gridx = 0;
    cons2.gridy = 0;
    List<Component> botoesDebug = Arrays.asList(new Component[] {this.botaoDebugContinuar, this.botaoDebugParar, this.botaoDebugContinuarSem});
    for (Component c : botoesDebug) {
      cons2.gridx++;
      panelBotoesDebug.add(c, cons2);
    }

    List<Component> componentes = new ArrayList<Component>();
    componentes.addAll(Arrays.asList(new Component[] {this.labelOu, this.comboBoxExemplos, this.botaoIniciarExemplo, new JSeparator(JSeparator.HORIZONTAL), this.checkBoxImprimirTraducao,
        this.comboBoxTraducoes, new JSeparator(JSeparator.HORIZONTAL), this.checkBoxDebugAtivo, panelBotoesDebug, new JSeparator(JSeparator.HORIZONTAL), this.checkBoxJGraphT,
        new JSeparator(JSeparator.HORIZONTAL), this.checkBoxLogAtivo,}));
    componentes.addAll(this.logTipos.keySet());

    for (Component c : componentes) {
      cons.gridy++;
      this.panel.add(c, cons);
    }


    this.add(panel);
    this.pack();
    this.centralizaJanela();
  }

  private void centralizaJanela() {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
    this.setLocation(x, y);
  }

  private void setupBotoes() {
    this.fileChooser = new JFileChooser();
    JFrame frame = this;
    // RODA ARQUIVO DO PC
    this.botaoArquivo = new JButton("Executar Arquivo");
    this.botaoArquivo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int resultado = fileChooser.showOpenDialog(frame);
        if (resultado == JFileChooser.APPROVE_OPTION) {
          File arquivoSelecionado = fileChooser.getSelectedFile();
          String caminho = arquivoSelecionado.getPath();
          rodarArquivo(caminho);
        }
      }
    });
    // RODA EXEMPLO
    this.botaoIniciarExemplo = new JButton("Executar Exemplo");
    this.botaoIniciarExemplo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String caminho = getCamihoArquivo();
        rodarArquivo(caminho);
      }
    });
    // TRADUCAO
    this.checkBoxImprimirTraducao = new JCheckBox("Imprimir Tradução", false);
    this.checkBoxImprimirTraducao.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JCheckBox x = (JCheckBox) e.getSource();
        if (x.isSelected()) {
          comboBoxTraducoes.setEnabled(true);
        } else {
          comboBoxTraducoes.setEnabled(false);
        }
      }
    });
    // LOG CHECKBOX
    this.checkBoxLogAtivo = new JCheckBox("Log ativo", false);
    this.checkBoxLogAtivo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JCheckBox x = (JCheckBox) e.getSource();
        if (x.isSelected()) {
          logTipos.keySet().forEach(c -> c.setEnabled(true));
        } else {
          logTipos.keySet().forEach(c -> c.setEnabled(false));
        }
      }
    });
    // DEBUG CHECKBOX
    this.checkBoxDebugAtivo = new JCheckBox("Debug ativo", false);
    // GRAFO
    this.checkBoxJGraphT = new JCheckBox("Exibir Grafo", false);
    // ->
    this.botaoDebugContinuar = new JButton("->");
    this.botaoDebugContinuar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        interpretador.debugProxPasso();
      }
    });
    this.botaoDebugContinuar.setEnabled(false);
    // |>
    this.botaoDebugContinuarSem = new JButton("|>");
    this.botaoDebugContinuarSem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        interpretador.debugContinuar();
      }
    });
    this.botaoDebugContinuarSem.setEnabled(false);
    // X
    this.botaoDebugParar = new JButton("X");
    this.botaoDebugParar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        interpretador.debugParar();
      }
    });
    this.botaoDebugParar.setEnabled(false);
  }

  private String[] getNomesTraducoes() {
    List<String> retorno = new ArrayList<String>();
    for (ConversorStrategy s : ConversorStrategy.values()) {
      retorno.add(s.name());
    }

    String[] conversores = new String[retorno.size()];
    conversores = retorno.toArray(conversores);

    return conversores;
  }

  private String[] getNomeArquivosExemplo() {
    File folder = new File(PATH_EXEMPLOS);
    File[] listOfFiles = folder.listFiles();

    List<String> retorno = new ArrayList<String>();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        retorno.add(listOfFiles[i].getName());
      }
    }

    String[] arquivos = new String[retorno.size()];
    arquivos = retorno.toArray(arquivos);

    return arquivos;
  }

  // EXECUCAO
  private void rodarArquivo(String caminho) {
    try {
      // DEBUG
      this.interpretador.setDebugStrategy(new PassoAPassoDebugStrategy());
      this.interpretador.setDebugAtivo(this.checkBoxDebugAtivo.isSelected());
    
      // LOG
      this.interpretador.setLogAtivo(this.checkBoxLogAtivo.isSelected());
      if (this.checkBoxLogAtivo.isSelected()) {
        this.interpretador.removerEventosLog();
        for (JCheckBox c : this.logTipos.keySet()) {
          if (c.isSelected())
            this.interpretador.setEventoLog(this.logTipos.get(c));
        }
      }
      
      this.interpretador.executarViaArquivo(caminho);
      // GRAFO
      if (this.checkBoxJGraphT.isSelected()) {
        new JGraphTBuilder().print(this.interpretador.getProgramaASTViaArquivo(caminho));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getCamihoArquivo() {
    String arquivo = (String) comboBoxExemplos.getSelectedItem();
    String caminho = PATH_EXEMPLOS + arquivo;
    return caminho;
  }

  // PRINT DOS ERROS
  private void report(int line, String onde, String msg) {
    System.err.println("[Parser Erro | linha " + line + "] Erro " + onde + ": " + msg);
  }

  private void error(ParserError erro) {
    if (erro.token != null) {
      if (erro.token.type == TiposToken.EOF) {
        report(erro.linha, " no fim", erro.mensagem);
      } else {
        report(erro.linha, " em '" + erro.token.lexeme + "'", erro.mensagem);
      }
    } else {
      report(erro.linha, "", erro.mensagem);
    }
  }

  private void runtimeError(RuntimeError error) {
    System.err.println("[Runtime Erro | linha " + error.token.line + "] Erro em '" + error.token.lexeme + "': " + error.getMessage());
  }

  // ACOES PARA O INTERPRETADOR
  private void interpretacaoConcluida(double payload, boolean temErro) {
    System.out.println("Tempo de execução: " + payload + "s");

    this.botaoDebugContinuar.setEnabled(false);
    this.botaoDebugParar.setEnabled(false);
    this.botaoDebugContinuarSem.setEnabled(false);

    if (temErro)
      return;

    if (this.checkBoxImprimirTraducao.isSelected()) {
      String cs = (String) this.comboBoxTraducoes.getSelectedItem();
      ConversorStrategy conversor = ConversorStrategy.valueOf(cs);
      String result;
      try {
        result = this.interpretador.traduzirDoArquivo(this.getCamihoArquivo(), conversor);
        System.out.println("Conversao " + cs);
        System.out.println(result);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onInput(LeitorEntradaConsole leitor) {
    System.out.println(">");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      leitor.setValor(reader.readLine());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onOutput(String output) {
   // System.out.println(output);
  }

  @Override
  public void onInterpretacaoConcluida(double tempoExecucao) {
    this.interpretacaoConcluida(tempoExecucao, false);
  }

  @Override
  public void onInterpretacaoInterrompida(double tempoExecucao) {
    this.interpretacaoConcluida(tempoExecucao, true);
  }

  @Override
  public void onDebugMudancaEstado(EstadoDebug novoEstado) {
    // System.out.println("[Estado] = " + novoEstado.toString());
    switch (novoEstado) {
      case INICIAL:// reset
        this.checkBoxDebugAtivo.setEnabled(true);
        this.botaoDebugContinuar.setEnabled(false);
        this.botaoDebugParar.setEnabled(false);
        this.botaoDebugContinuarSem.setEnabled(false);
        return;
      case PAUSADO:
        this.checkBoxDebugAtivo.setEnabled(false);
        this.botaoDebugContinuar.setEnabled(true);
        this.botaoDebugParar.setEnabled(true);
        this.botaoDebugContinuarSem.setEnabled(true);
        return;
      case EXECUTANDO:
        this.checkBoxDebugAtivo.setEnabled(false);
        this.botaoDebugContinuar.setEnabled(false);
        this.botaoDebugParar.setEnabled(true);
        this.botaoDebugContinuarSem.setEnabled(false);
        return;
      default:
        return;
    }

  }

  @Override
  public void onDebugPassoExecutado(DebugSnapshot snapshot) {
     System.out.println("=================");
     System.out.println("linha: " + snapshot.getNode().getLinha() + " .. " +
     snapshot.getNode().getClass().getName());
     System.out.println("Ambiente:");
     System.out.println("Nome\tValor");
     for (String n : snapshot.getAmbienteSnapshot().keySet()) {
     System.out.print(n + "\t");
     Object valor = snapshot.getAmbienteSnapshot().get(n);
     if (valor != null) {
     System.out.print(valor.toString());
     }
     System.out.print("\n");
    
     }
     System.out.println("=================");
  }

  @Override
  public void onErro(RuntimeException erro) {
    if (erro instanceof ParserError) {
      this.error((ParserError) erro);
    } else if (erro instanceof RuntimeError) {
      this.runtimeError((RuntimeError) erro);
    }
  }

  @Override
  public void onLog(String msgLog) {
    System.out.println(msgLog);
  }

}

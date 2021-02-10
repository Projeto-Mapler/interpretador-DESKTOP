package main;

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
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import conversores.ConversorStrategy;
import debug.DebugSnapshot;
import debug.Debugador;
import debug.EstadosDebug;
import debug.EventoListener;
import debug.GerenciadorEventos;
import debug.PassoAPassoDebugStrategy;
import debug.TiposEvento;
import modelos.LeitorEntradaConsole;
import modelos.ParserError;
import modelos.RuntimeError;
import modelos.TiposToken;
import util.JGraphTBuilder;

/**
 * Classe de teste de implementação da biblioteca
 * 
 * @author Kerlyson
 *
 */
public class MainUI extends JFrame implements EventoListener {
  private static final long serialVersionUID = 1L;

  private final String PATH_EXEMPLOS = "exemplos\\";

  private JPanel panel;
  private JButton botaoArquivo, botaoIniciarExemplo, botaoDebugParar, botaoDebugContinuar,
      botaoDebugContinuarSem;
  private JLabel labelOu;
  private JComboBox<String> comboBoxExemplos, comboBoxTraducoes;
  private JFileChooser fileChooser;
  private JCheckBox checkBoxDebugAtivo, checkBoxImprimirTraducao, checkBoxJGraphT;

  private GerenciadorEventos ge = new GerenciadorEventos();

  public MainUI() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.setup();
    this.setVisible(true);

    this.ge.inscreverTodos(new TiposEvento[] {TiposEvento.MUDANCA_ESTADO_DEBUG,
        TiposEvento.ESCREVER_EVENTO, TiposEvento.LER_EVENTO, TiposEvento.INTERPRETACAO_CONCLUIDA,
        TiposEvento.ERRO_PARSE, TiposEvento.ERRO_RUNTIME, TiposEvento.INTERPRETACAO_CONCLUIDA},
        this);

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

    this.panel.setBorder(new EmptyBorder(new Insets(100, 50, 100, 50)));

    this.setupBotoes();

    this.labelOu = new JLabel("OU");
    this.labelOu.setHorizontalAlignment(JLabel.CENTER);

    this.comboBoxExemplos = new JComboBox<String>(this.getNomeArquivosExemplo());

    cons.gridx = 0;
    cons.weightx = 1;
    cons.gridy = 0;
    this.panel.add(this.botaoArquivo, cons);

    cons.gridy++;
    this.panel.add(this.labelOu, cons);

    cons.gridy++;
    this.panel.add(this.comboBoxExemplos, cons);

    cons.gridy++;
    this.panel.add(this.botaoIniciarExemplo, cons);

    cons.gridy++;
    this.panel.add(new JSeparator(JSeparator.HORIZONTAL), cons);

    this.comboBoxTraducoes = new JComboBox<String>(this.getNomesTraducoes());
    this.comboBoxTraducoes.setEnabled(false);

    cons.gridy++;
    this.panel.add(this.checkBoxImprimirTraducao, cons);

    cons.gridy++;
    this.panel.add(this.comboBoxTraducoes, cons);

    cons.gridy++;
    this.panel.add(new JSeparator(JSeparator.HORIZONTAL), cons);

    cons.gridy++;
    this.panel.add(this.checkBoxDebugAtivo, cons);

    JPanel p = new JPanel();
    p.setLayout(new GridBagLayout());
    GridBagConstraints cons2 = new GridBagConstraints();
    cons2.fill = GridBagConstraints.HORIZONTAL;
    cons2.insets = new Insets(0, 5, 5, 5);
    cons2.gridx = 0;
    cons2.gridy = 0;
    p.add(this.botaoDebugContinuar, cons2);
    cons2.gridx = 1;
    p.add(this.botaoDebugParar, cons2);
    cons2.gridx = 2;
    p.add(this.botaoDebugContinuarSem, cons2);

    cons.gridy++;
    this.panel.add(p, cons);

    cons.gridy++;
    this.panel.add(new JSeparator(JSeparator.HORIZONTAL), cons);

    cons.gridy++;
    this.panel.add(this.checkBoxJGraphT, cons);

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

    this.botaoIniciarExemplo = new JButton("Executar Exemplo");
    this.botaoIniciarExemplo.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        String caminho = getCamihoArquivo();
        rodarArquivo(caminho);

      }
    });

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
    this.checkBoxDebugAtivo = new JCheckBox("Debug ativo", false);
    // n usado: degug eh instanciado apenas apos o botao de executar eh clicado
    // this.checkBoxDebugAtivo.addActionListener(new ActionListener() {
    //
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // System.out.println("jeje");
    // JCheckBox cbLog = (JCheckBox) e.getSource();
    // ge.notificar(TipoEvento.TOGGLE_DEBUG, cbLog.isSelected());
    //
    // }
    // });

    this.checkBoxJGraphT = new JCheckBox("Exibir Grafo", false);

    this.botaoDebugContinuar = new JButton("->");

    this.botaoDebugParar = new JButton("X");

    this.botaoDebugContinuarSem = new JButton("|>");

    this.botaoDebugContinuar.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ge.notificar(TiposEvento.CONTINUAR_DEBUG_ATIVO, null);

      }
    });

    this.botaoDebugContinuarSem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ge.notificar(TiposEvento.CONTINUAR_DEBUG_DESATIVADO, null);

      }
    });

    this.botaoDebugParar.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ge.notificar(TiposEvento.FINALIZAR_DEBUG, null);
      }
    });

    this.botaoDebugContinuar.setEnabled(false);
    this.botaoDebugParar.setEnabled(false);
    this.botaoDebugContinuarSem.setEnabled(false);
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

  private void rodarArquivo(String caminho) {
    try {
      Debugador debugador = new Debugador(this.ge, this.checkBoxDebugAtivo.isSelected());
      // BreakpointsDebugStrategy breakpointsDebugStrategy = new BreakpointsDebugStrategy();
      // breakpointsDebugStrategy.addBreakPoint(13);
      // debugador.setDebugStrategy(breakpointsDebugStrategy);
      debugador.setDebugStrategy(new PassoAPassoDebugStrategy());

      Principal principal = new Principal(ge, debugador);
      principal.executarViaArquivo(caminho);
      if (this.checkBoxJGraphT.isSelected()) {
        new JGraphTBuilder().print(principal.getProgramaAST(caminho));
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

  @Override
  public void update(TiposEvento tipoEvento, Object payload) {

    if (tipoEvento == TiposEvento.INTERPRETACAO_CONCLUIDA) {
      System.out.println("Tempo de execução: " + (double) payload + "s");

      this.botaoDebugContinuar.setEnabled(false);
      this.botaoDebugParar.setEnabled(false);
      this.botaoDebugContinuarSem.setEnabled(false);
      
      
      if (this.checkBoxImprimirTraducao.isSelected()) {
        String cs = (String) this.comboBoxTraducoes.getSelectedItem();
        ConversorStrategy conversor = ConversorStrategy.valueOf(cs);

        String result;
        try {
          result = new Principal(ge, null).traduzirDoArquivo(this.getCamihoArquivo(), conversor);
          System.out.println("Conversao " + cs);
          System.out.println(result);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
      return;
    }
    if (tipoEvento == TiposEvento.ESCREVER_EVENTO) {
      String msg = (String) payload;
      System.out.println(msg);
      return;
    }
    if (tipoEvento == TiposEvento.LER_EVENTO) {
      LeitorEntradaConsole leitor = (LeitorEntradaConsole) payload;
      System.out.println(">");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      try {
        leitor.setValor(reader.readLine());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    }

    if (tipoEvento == TiposEvento.ERRO_PARSE) {
      this.error((ParserError) payload);
      return;
    }

    if (tipoEvento == TiposEvento.ERRO_RUNTIME) {
      this.runtimeError((RuntimeError) payload);
      return;
    }
    if (tipoEvento == TiposEvento.ACAO_DEBUG) {
      DebugSnapshot s = (DebugSnapshot) payload;
      System.out.println("=================");
      System.out
          .println("linha: " + s.getNode().getLinha() + " .. " + s.getNode().getClass().getName());
      System.out.println("Ambiente:");
      System.out.println("Nome\tValor");
      for (String n : s.getAmbienteSnapshot().keySet()) {
        System.out.print(n + "\t");
        Object valor = s.getAmbienteSnapshot().get(n);
        if (valor != null) {
          System.out.print(valor.toString());
        }
        System.out.print("\n");

      }
      System.out.println("=================");
    }

    if (payload instanceof EstadosDebug) {
      EstadosDebug estado = (EstadosDebug) payload;

      switch (estado) {
        case DESATIVO:
          this.checkBoxDebugAtivo.setSelected(false);
        case ATIVO:
          this.checkBoxDebugAtivo.setEnabled(true);
          this.botaoDebugContinuar.setEnabled(false);
          this.botaoDebugParar.setEnabled(false);
          this.botaoDebugContinuarSem.setEnabled(false);
          break;
        case PAUSADO:
          this.checkBoxDebugAtivo.setEnabled(false);
          this.botaoDebugContinuar.setEnabled(true);
          this.botaoDebugParar.setEnabled(true);
          this.botaoDebugContinuarSem.setEnabled(true);
          break;
        case EXECUTANDO:
          this.checkBoxDebugAtivo.setEnabled(false);
          this.botaoDebugContinuar.setEnabled(false);
          this.botaoDebugParar.setEnabled(true);
          this.botaoDebugContinuarSem.setEnabled(false);
          break;

        default:
          break;
      }
    }

  }

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
    System.err.println("[Runtime Erro | linha " + error.token.line + "] Erro em '"
        + error.token.lexeme + "': " + error.getMessage());

  }
}

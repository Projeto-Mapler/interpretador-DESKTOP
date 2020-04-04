package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
import javax.swing.border.EmptyBorder;

import debug.EstadosDebug;
import debug.EventoListener;
import debug.GerenciadorEventos;
import debug.TipoEvento;

public class MainUI extends JFrame implements EventoListener{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String PATH_EXEMPLOS = "..\\exemplos\\";

	private JPanel panel;
	private JButton botaoArquivo, botaoIniciarExemplo, botaoDebugParar,  botaoDebugContinuar;
	private JLabel labelOu;
	private JComboBox<String> comboBoxExemplos;
	private JFileChooser fileChooser;
	private JCheckBox checkBoxDebugAtivo;
	
	private GerenciadorEventos ge = new GerenciadorEventos();
	
	public MainUI() {
		this.setup();
		this.setVisible(true);
		
		this.ge.inscrever(TipoEvento.MUDANCA_ESTADO_DEBUG, this);
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
		
		
		this.panel.setBorder(new EmptyBorder(new Insets(100, 50, 100,50)));
		
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
		
		cons.gridy++;
		this.panel.add(p, cons);
		
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
				if(resultado == JFileChooser.APPROVE_OPTION) {
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
				String arquivo = (String)comboBoxExemplos.getSelectedItem();
				String caminho = PATH_EXEMPLOS + arquivo;
				rodarArquivo(caminho);
				
			}
		});
		
		this.checkBoxDebugAtivo = new JCheckBox("Debug ativo", true);
		this.checkBoxDebugAtivo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 JCheckBox cbLog = (JCheckBox) e.getSource();
			        ge.notificar(TipoEvento.TOGGLE_DEBUG, cbLog.isSelected());
				
			}
		});
		
		this.botaoDebugContinuar = new JButton("|>");
	
		this.botaoDebugParar = new JButton("X");
	
		this.botaoDebugContinuar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ge.notificar(TipoEvento.CONTINUAR_DEBUG, null);
				
			}
		});
		
		this.botaoDebugParar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ge.notificar(TipoEvento.FINALIZAR_DEBUG, null);
				
			}
		});
		
		this.botaoDebugContinuar.setEnabled(false);
		this.botaoDebugParar.setEnabled(false);
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
			new Principal(ge).runFile(caminho);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(TipoEvento tipoEvento, Object payload) {
		if(payload instanceof EstadosDebug ) {
			EstadosDebug estado = (EstadosDebug)payload;
			
			switch (estado) {
			case DESATIVO:
			case ATIVO:
				this.checkBoxDebugAtivo.setEnabled(true);
				this.botaoDebugContinuar.setEnabled(false);
				this.botaoDebugParar.setEnabled(false);
				break;
			case PAUSADO:
				this.checkBoxDebugAtivo.setEnabled(false);
				this.botaoDebugContinuar.setEnabled(true);
				this.botaoDebugParar.setEnabled(true);
				break;
			case EXECUTANDO:
				this.checkBoxDebugAtivo.setEnabled(false);
				this.botaoDebugContinuar.setEnabled(false);
				this.botaoDebugParar.setEnabled(true);
				break;

			default:
				break;
			}
		}
		
	}
}

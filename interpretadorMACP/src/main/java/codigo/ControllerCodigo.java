package codigo;


import com.jfoenix.controls.JFXButton;

import Recursos.Alertas;
import Recursos.ControllerConsole;
import Recursos.Propriedades;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import inicial.Inicial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.undo.UndoManager;
import org.reactfx.EventStream;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ControllerCodigo implements Initializable {
	
	@FXML
	StyleClassedTextArea codigo, traducao_c, traducao_cplus, traducao_pascal, traducao_java, traducao_python, traducao_c2, traducao_cplus2, traducao_pascal2, traducao_java2, traducao_python2;
	
	@FXML
	SplitPane split, splitt;
	
	@FXML
	Tab tab_c, tab_cplus, tab_pascal, tab_java, tab_python, tab_config, tab_c2, tab_cplus2, tab_pascal2, tab_java2, tab_python2, tab_config2;
	
	@FXML
	TabPane tab_traducao, tab_traducao2;
	
	@FXML
	MenuItem mi_abrir, mi_salvar, mi_salvarc, mi_fechar, mi_inter_portugol, mi_sobre, mi_compilar;
	
	@FXML
	JFXButton arquiv1, arquiv2, arquiv3, arquiv4, arquiv5, nov, btn_sair_teste, btn_executar;
	
	@FXML
	AnchorPane area_codigo, area_extra, area_teste, area_traducao, area_traducao2;
	
	@FXML
	CheckMenuItem mi_inter_traducao, mi_inter_mesa;
	
	@FXML
	CheckBox lg_c, lg_cplus, lg_pascal, lg_java, lg_python, lg_duplicar, lg_c2, lg_cplus2, lg_pascal2, lg_java2, lg_python2;
	
	@FXML
	MenuBar menu;
	
	//private 
		private UndoManager tbt;
		private EventStream<String> changes;
		private int numeracao, high = 1;
		private static File arquivo;
		private int numarquivo, atual, tabAtual = 1, tabAtual2 = 1, exec = 0;
		private ArrayList<String> links;
		private ControllerConsole controle;
	
	//staticos
		public static Stage sobre, console;
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	try {
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		split.getItems().add(area_extra);
    		numarquivo = 0;
    		atual = 0;
    		links = new ArrayList<String>();
    		setCss();
    		setInterface(1);
    		
    		tab_traducao.getTabs().clear();
    		tab_traducao.getTabs().add(tab_config);
    		
    		tab_traducao2.getTabs().clear();
    		tab_traducao2.getTabs().add(tab_config2);
    		
    		mi_compilar.setOnAction(e -> {
    			if(exec == 0) {
    				exec = 1;
    				FontAwesomeIcon icon = new FontAwesomeIcon();
    				icon.setIcon(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons.EXCLAMATION_TRIANGLE);
    				icon.setFill(Color.RED);
    				icon.setSize("1.5em");
    				btn_executar.setGraphic(icon);
    				console();
    			}else {
    				exec = 0;
    				controle.fechar();
    				FontAwesomeIcon icon = new FontAwesomeIcon();
    				icon.setIcon(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons.PLAY_CIRCLE);
    				icon.setFill(Color.web("#02ff13"));
    				icon.setSize("1.5em");
    				btn_executar.setGraphic(icon);
    			}
    			
    		});
    		
    		btn_executar.setOnAction(e -> {
    			if(exec == 0) {
    				exec = 1;
    				FontAwesomeIcon icon = new FontAwesomeIcon();
    				icon.setIcon(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons.EXCLAMATION_TRIANGLE);
    				icon.setFill(Color.RED);
    				icon.setSize("1.5em");
    				btn_executar.setGraphic(icon);
    				console();
    			}else {
    				exec = 0;
    				controle.fechar();
    				FontAwesomeIcon icon = new FontAwesomeIcon();
    				icon.setIcon(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons.PLAY_CIRCLE);
    				icon.setFill(Color.web("#02ff13"));
    				icon.setSize("1.5em");
    				btn_executar.setGraphic(icon);
    			}
    			
    		});
    		
    		btn_sair_teste.setOnAction(e -> {
    			mi_inter_mesa.setSelected(false);
    			if(mi_inter_traducao.isSelected()) {
					if(lg_duplicar.isSelected()) {
						setInterface(6);
					}else {
						setInterface(2);
					}
    			}else {
    				setInterface(1);
    			}
    		});
    		
    		lg_c.setOnAction(e -> {
    			if(lg_c.isSelected()) {
    				tab_traducao.getTabs().remove(tab_config);
    				tab_traducao.getTabs().add(tab_c);
    				tab_traducao.getTabs().add(tab_config);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}else {
    				tab_traducao.getTabs().remove(tab_c);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}
    		});
    		
    		lg_cplus.setOnAction(e -> {
    			if(lg_cplus.isSelected()) {
    				tab_traducao.getTabs().remove(tab_config);
    				tab_traducao.getTabs().add(tab_cplus);
    				tab_traducao.getTabs().add(tab_config);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}else {
    				tab_traducao.getTabs().remove(tab_cplus);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}
    		});
    		
    		lg_pascal.setOnAction(e -> {
    			if(lg_pascal.isSelected()) {
    				tab_traducao.getTabs().remove(tab_config);
    				tab_traducao.getTabs().add(tab_pascal);
    				tab_traducao.getTabs().add(tab_config);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}else {
    				tab_traducao.getTabs().remove(tab_pascal);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}
    		});
    		
    		lg_java.setOnAction(e -> {
    			if(lg_java.isSelected()) {
    				tab_traducao.getTabs().remove(tab_config);
    				tab_traducao.getTabs().add(tab_java);
    				tab_traducao.getTabs().add(tab_config);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}else {
    				tab_traducao.getTabs().remove(tab_java);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}
    		});
    		
    		lg_python.setOnAction(e -> {
    			if(lg_python.isSelected()) {
    				tab_traducao.getTabs().remove(tab_config);
    				tab_traducao.getTabs().add(tab_python);
    				tab_traducao.getTabs().add(tab_config);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}else {
    				tab_traducao.getTabs().remove(tab_python);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}
    		});
    		
    		lg_c2.setOnAction(e -> {
    			if(lg_c2.isSelected()) {
    				tab_traducao2.getTabs().remove(tab_config2);
    				tab_traducao2.getTabs().add(tab_c2);
    				tab_traducao2.getTabs().add(tab_config2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}else {
    				tab_traducao2.getTabs().remove(tab_c2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}
    		});
    		
    		lg_cplus2.setOnAction(e -> {
    			if(lg_cplus2.isSelected()) {
    				tab_traducao2.getTabs().remove(tab_config2);
    				tab_traducao2.getTabs().add(tab_cplus2);
    				tab_traducao2.getTabs().add(tab_config2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}else {
    				tab_traducao2.getTabs().remove(tab_cplus2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}
    		});
    		
    		lg_pascal2.setOnAction(e -> {
    			if(lg_pascal2.isSelected()) {
    				tab_traducao2.getTabs().remove(tab_config2);
    				tab_traducao2.getTabs().add(tab_pascal2);
    				tab_traducao2.getTabs().add(tab_config2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}else {
    				tab_traducao2.getTabs().remove(tab_pascal2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}
    		});
    		
    		lg_java2.setOnAction(e -> {
    			if(lg_java2.isSelected()) {
    				tab_traducao2.getTabs().remove(tab_config2);
    				tab_traducao2.getTabs().add(tab_java2);
    				tab_traducao2.getTabs().add(tab_config2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}else {
    				tab_traducao2.getTabs().remove(tab_java2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}
    		});
    		
    		lg_python2.setOnAction(e -> {
    			if(lg_python2.isSelected()) {
    				tab_traducao2.getTabs().remove(tab_config2);
    				tab_traducao2.getTabs().add(tab_python2);
    				tab_traducao2.getTabs().add(tab_config2);
    				tab_traducao2.getSelectionModel().select(tab_config2);
    			}else {
    				tab_traducao.getTabs().remove(tab_python);
    				tab_traducao.getSelectionModel().select(tab_config);
    			}
    		});
    		
    		lg_duplicar.setOnAction(e -> {
    			if(lg_duplicar.isSelected()) {
    				if(mi_inter_mesa.isSelected()){
    					setInterface(7);
    				}else {
    					setInterface(6);
    				}
    				
    			}else{
    				if(mi_inter_traducao.isSelected()) {
        				if(mi_inter_mesa.isSelected()) {
            				setInterface(4);
            			}else {
            				setInterface(2);
            			}
        			}else {
        				if(mi_inter_mesa.isSelected()) {
            				setInterface(3);
            			}else {
            				setInterface(1);
            			}
        			}
    			}
    		});
    		
    		mi_inter_portugol.setOnAction(e -> {
    			this.setInterface(1);
    			mi_inter_traducao.setSelected(false);
    			mi_inter_mesa.setSelected(false);
    		});
    		
    		mi_inter_traducao.setOnAction(e -> {
    			if(mi_inter_traducao.isSelected()) {
    				if(mi_inter_mesa.isSelected()) {
        				setInterface(4);
        			}else {
        				setInterface(2);
        			}
    			}else {
    				if(mi_inter_mesa.isSelected()) {
        				setInterface(3);
        			}else {
        				setInterface(1);
        			}
    			}
    		});
    		
    		mi_inter_mesa.setOnAction(e -> {
    			if(mi_inter_mesa.isSelected()) {
    				if(mi_inter_traducao.isSelected()) {
    					if(lg_duplicar.isSelected()) {
    						setInterface(7);
    					}else {
    						setInterface(5);
    					}
        			}else {
        				setInterface(3);
        			}
    			}else {
    				if(mi_inter_traducao.isSelected()) {
    					if(lg_duplicar.isSelected()) {
    						setInterface(6);
    					}else {
    						setInterface(2);
    					}
        			}else {
        				setInterface(1);
        			}
    			}
    		});
    		
    		codigo.setOnKeyPressed(e -> {
    	    	codigo = GerenciadorDeTexto.setCores(codigo);
    	    	Propriedades.setPropriedade("autosave", codigo.getText().replace("\n", "</line>").toString().replace(" ", "</space>"));
    		});
	    
    	    codigo.setOnKeyReleased(e -> {
    	    	codigo = GerenciadorDeTexto.setCores(codigo);
    	    	Propriedades.setPropriedade("autosave", codigo.getText().replace("\n", "</line>").toString().replace(" ", "</space>"));
		    });
    		
    		
    		arquiv1.setOnAction(e -> {
    			arquivo = new File(links.get(0));
    			try {
    				String txt = "";
    	    		BufferedReader br = new BufferedReader(new FileReader(links.get(0)));
    				while(br.ready()){
    					txt += br.readLine();
    				}
    				br.close();
    				txt = txt.replace("</line>", "\n");
        			txt = txt.replace("</space>", " ");
        			txt = txt.replace("</tab>", "\t");
    				setTextCodigo(txt);
    				atual = 1;
    			} catch (Exception es) {
    				es.printStackTrace();
    			}
    		});
    		
    		arquiv2.setOnAction(e -> {
    			arquivo = new File(links.get(1));
    			try {
    				String txt = "";
    	    		BufferedReader br = new BufferedReader(new FileReader(links.get(1)));
    				while(br.ready()){
    					txt += br.readLine();
    				}
    				br.close();
    				txt = txt.replace("</line>", "\n");
        			txt = txt.replace("</space>", " ");
        			txt = txt.replace("</tab>", "\t");
    				setTextCodigo(txt);
    				atual = 2;
    			} catch (Exception es) {
    				es.printStackTrace();
    			}
    		});
    		
    		arquiv3.setOnAction(e -> {
    			arquivo = new File(links.get(2));
    			try {
    				String txt = "";
    	    		BufferedReader br = new BufferedReader(new FileReader(links.get(2)));
    				while(br.ready()){
    					txt += br.readLine();
    				}
    				br.close();
    				txt = txt.replace("</line>", "\n");
        			txt = txt.replace("</space>", " ");
        			txt = txt.replace("</tab>", "\t");
    				setTextCodigo(txt);
    				atual = 3;
    			} catch (Exception es) {
    				es.printStackTrace();
    			}
    		});
    		
    		arquiv4.setOnAction(e -> {
    			arquivo = new File(links.get(3));
    			try {
    				String txt = "";
    	    		BufferedReader br = new BufferedReader(new FileReader(links.get(3)));
    				while(br.ready()){
    					txt += br.readLine();
    				}
    				br.close();
    				txt = txt.replace("</line>", "\n");
        			txt = txt.replace("</space>", " ");
        			txt = txt.replace("</tab>", "\t");
    				setTextCodigo(txt);
    				atual = 4;
    			} catch (Exception es) {
    				es.printStackTrace();
    			}
    		});
    		
    		arquiv5.setOnAction(e -> {
    			arquivo = new File(links.get(4));
    			try {
    				String txt = "";
    	    		BufferedReader br = new BufferedReader(new FileReader(links.get(4)));
    				while(br.ready()){
    					txt += br.readLine();
    				}
    				br.close();
    				txt = txt.replace("</line>", "\n");
        			txt = txt.replace("</space>", " ");
        			txt = txt.replace("</tab>", "\t");
    				setTextCodigo(txt);
    				atual = 5;
    			} catch (Exception es) {
    				es.printStackTrace();
    			}
    		});
    		

    		
    		mi_abrir.setOnAction(e -> {
    			escolherArquivo();
    			
    			if(arquivo != null) {
    				if(numarquivo == 0) {
    					arquiv1.setText(arquivo.getName());
    					links.add(0,arquivo.getPath().toString());
    					arquiv1.setVisible(true);
    					Propriedades.prepararBackup();
    					Propriedades.setFile(links.get(0));
    					Propriedades.setBackup();
    					atual = 1;
    					numarquivo++;
    				}
    				
    				else if(numarquivo == 1) {
    					if(arquivo.getName() != arquiv1.getText()) {
    						arquiv2.setText(arquivo.getName());
    						links.add(1,arquivo.getPath().toString());
    						arquiv2.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(1));
        					Propriedades.setBackup();
    						atual = 2;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 2) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText()) {
    						arquiv3.setText(arquivo.getName());
    						links.add(2,arquivo.getPath().toString());
    						arquiv3.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(2));
        					Propriedades.setBackup();
    						atual = 3;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 3) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText()) {
    						arquiv4.setText(arquivo.getName());
    						links.add(3,arquivo.getPath().toString());
    						arquiv4.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(3));
        					Propriedades.setBackup();
    						atual = 4;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 4) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText()) {
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						arquiv5.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 5) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText() && arquivo.getName() != arquiv5.getText()) {
    						arquiv1.setText(arquiv2.getText());
    						links.add(0,links.get(1));
    						arquiv2.setText(arquiv3.getText());
    						links.add(1,links.get(2));
    						arquiv3.setText(arquiv4.getText());
    						links.add(2,links.get(3));
    						arquiv4.setText(arquiv5.getText());
    						links.add(3,links.get(4));
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    					}
    				}
    			}
    		});
    		
    		mi_salvar.setOnAction(e -> {
    			salvar(true);
    			
    			if(arquivo != null) {
    				if(numarquivo == 0) {
    					arquiv1.setText(arquivo.getName());
    					links.add(0,arquivo.getPath().toString());
    					arquiv1.setVisible(true);
    					Propriedades.prepararBackup();
    					Propriedades.setFile(links.get(0));
    					Propriedades.setBackup();
    					atual = 1;
    					numarquivo++;
    				}
    				
    				else if(numarquivo == 1) {
    					if(!arquivo.getName().equals(arquiv1.getText())) {
    						arquiv2.setText(arquivo.getName());
    						links.add(1,arquivo.getPath().toString());
    						arquiv2.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(1));
        					Propriedades.setBackup();
    						atual = 2;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 2) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText()) {
    						arquiv3.setText(arquivo.getName());
    						links.add(2,arquivo.getPath().toString());
    						arquiv3.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(2));
        					Propriedades.setBackup();
    						atual = 3;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 3) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText()) {
    						arquiv4.setText(arquivo.getName());
    						links.add(3,arquivo.getPath().toString());
    						arquiv4.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(3));
        					Propriedades.setBackup();
    						atual = 4;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 4) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText()) {
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						arquiv5.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 5) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText() && arquivo.getName() != arquiv5.getText()) {
    						arquiv1.setText(arquiv2.getText());
    						links.add(0,links.get(1));
    						arquiv2.setText(arquiv3.getText());
    						links.add(1,links.get(2));
    						arquiv3.setText(arquiv4.getText());
    						links.add(2,links.get(3));
    						arquiv4.setText(arquiv5.getText());
    						links.add(3,links.get(4));
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    					}
    				}
    			}
    		});
    		
    		mi_salvarc.setOnAction(e -> {
    			salvarComo();
    			
    			if(arquivo != null) {
    				if(numarquivo == 0) {
    					arquiv1.setText(arquivo.getName());
    					links.add(0,arquivo.getPath().toString());
    					arquiv1.setVisible(true);
    					Propriedades.prepararBackup();
    					Propriedades.setFile(links.get(0));
    					Propriedades.setBackup();
    					atual = 1;
    					numarquivo++;
    				}
    				
    				else if(numarquivo == 1) {
    					if(arquivo.getName() != arquiv1.getText()) {
    						arquiv2.setText(arquivo.getName());
    						links.add(1,arquivo.getPath().toString());
    						arquiv2.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(1));
        					Propriedades.setBackup();
    						atual = 2;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 2) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText()) {
    						arquiv3.setText(arquivo.getName());
    						links.add(2,arquivo.getPath().toString());
    						arquiv3.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(2));
        					Propriedades.setBackup();
    						atual = 3;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 3) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText()) {
    						arquiv4.setText(arquivo.getName());
    						links.add(3,arquivo.getPath().toString());
    						arquiv4.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(3));
        					Propriedades.setBackup();
    						atual = 4;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 4) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText()) {
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						arquiv5.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 5) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText() && arquivo.getName() != arquiv5.getText()) {
    						arquiv1.setText(arquiv2.getText());
    						links.add(0,links.get(1));
    						arquiv2.setText(arquiv3.getText());
    						links.add(1,links.get(2));
    						arquiv3.setText(arquiv4.getText());
    						links.add(2,links.get(3));
    						arquiv4.setText(arquiv5.getText());
    						links.add(3,links.get(4));
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    					}
    				}
    			}
    		});
    		
    		mi_fechar.setOnAction(e -> {
    			Inicial.iniciar();
    		});
    		
    		mi_sobre.setOnAction(e -> {
    			sobre();
    		});
    		
    		nov.setOnAction(e -> {
    			if(codigo.getText() != "variaveis\\n\\ninicio\\n\\nfim.") {
    				int i = 0;
    				if(atual > 0) {
    					String cdg = codigo.getText().replace("\n", " ");
    					String atl = getTextAtual().replace("\n", " ");
    					if(!cdg.equals(atl)) {
    						i = Alertas.showConfirm("Deseja salvar o texto atual?");
    	    				if(i==1)
    	    					salvar(true);
    					}
    				}else {
    					i = Alertas.showConfirm("Deseja salvar o texto atual?");
        				if(i==1)
        					salvar(true);
    				}
    				if(i!=-1) {
    					setTextCodigo("");
    					atual = 0;
    					arquivo = null;
    				}
    					
    			}
    			
    		});
    		
    		arquiv1.setVisible(false);
    		arquiv2.setVisible(false);
    		arquiv3.setVisible(false);
    		arquiv4.setVisible(false);
    		arquiv5.setVisible(false);
    		
    		String continuar = Propriedades.getPropriedade("autosave");
    	    if(continuar != null && !continuar.equals("") && Inicial.setAutoSave == 1) {
    	    	setTextCodigo(continuar.replace("</line>", "\n").toString().replace("</space>", " ").toString().replace("</tab>", "\t"));
    	    }
    	    
    	    if(Inicial.setAutoSave == 2) {
    	    	setTextCodigo(Inicial.openFile.replace("</line>", "\n").toString().replace("</space>", " ").toString().replace("</tab>", "\t"));
    	    	atual++;
    	    	
    	    	if(arquivo != null) {
    				if(numarquivo == 0) {
    					arquiv1.setText(arquivo.getName());
    					links.add(0,arquivo.getPath().toString());
    					arquiv1.setVisible(true);
    					Propriedades.prepararBackup();
    					Propriedades.setFile(links.get(0));
    					Propriedades.setBackup();
    					atual = 1;
    					numarquivo++;
    					
    				}
    				
    				else if(numarquivo == 1) {
    					if(arquivo.getName() != arquiv1.getText()) {
    						arquiv2.setText(arquivo.getName());
    						links.add(1,arquivo.getPath().toString());
    						arquiv2.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(1));
        					Propriedades.setBackup();
    						atual = 2;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 2) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText()) {
    						arquiv3.setText(arquivo.getName());
    						links.add(2,arquivo.getPath().toString());
    						arquiv3.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(2));
        					Propriedades.setBackup();
    						atual = 3;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 3) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText()) {
    						arquiv4.setText(arquivo.getName());
    						links.add(3,arquivo.getPath().toString());
    						arquiv4.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(3));
        					Propriedades.setBackup();
    						atual = 4;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 4) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText()) {
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						arquiv5.setVisible(true);
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    						numarquivo++;
    					}
    				}
    				
    				else if(numarquivo == 5) {
    					if(arquivo.getName() != arquiv1.getText() && arquivo.getName() != arquiv2.getText() && arquivo.getName() != arquiv3.getText() && arquivo.getName() != arquiv4.getText() && arquivo.getName() != arquiv5.getText()) {
    						arquiv1.setText(arquiv2.getText());
    						links.add(0,links.get(1));
    						arquiv2.setText(arquiv3.getText());
    						links.add(1,links.get(2));
    						arquiv3.setText(arquiv4.getText());
    						links.add(2,links.get(3));
    						arquiv4.setText(arquiv5.getText());
    						links.add(3,links.get(4));
    						arquiv5.setText(arquivo.getName());
    						links.add(4,arquivo.getPath().toString());
    						Propriedades.prepararBackup();
        					Propriedades.setFile(links.get(4));
        					Propriedades.setBackup();
    						atual = 5;
    					}
    				}
    			}
    	    }
    	    
    	    Inicial.setAutoSave = 0;
    		
    	}catch (Exception ex) {
            Logger.getLogger(Inicial.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
    }
    
    private void setInterface(int numElementos) {
    	if(numElementos == 1) { //somente portugol
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		mi_inter_traducao.setSelected(false);
			mi_inter_mesa.setSelected(false);
    		
    	}else if(numElementos == 2) { //portugol + traducao
    		splitt.setDividerPositions(0.5);
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		split.getItems().add(area_extra);
    		splitt.getItems().clear();
    		splitt.getItems().add(area_traducao);
    		mi_inter_traducao.setSelected(true);
			mi_inter_mesa.setSelected(false);
    	}else if(numElementos == 3) { //portugol + teste
    		splitt.setDividerPositions(0.5);
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		split.getItems().add(area_extra);
    		splitt.getItems().clear();
    		splitt.getItems().add(area_teste);
    		mi_inter_traducao.setSelected(false);
			mi_inter_mesa.setSelected(true);
    	}else if(numElementos == 4) { //portugol + teste + traducao
    		splitt.setDividerPositions(0.5);
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		split.getItems().add(area_extra);
    		splitt.getItems().clear();
    		splitt.getItems().add(area_teste);
    		splitt.getItems().add(area_traducao);
    		mi_inter_traducao.setSelected(true);
			mi_inter_mesa.setSelected(true);
    	}else if(numElementos == 5) { //portugol + traducao + teste 
    		splitt.setDividerPositions(0.5);
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		split.getItems().add(area_extra);
    		splitt.getItems().clear();
    		splitt.getItems().add(area_traducao);
    		splitt.getItems().add(area_teste);
    		mi_inter_traducao.setSelected(true);
			mi_inter_mesa.setSelected(true);
    	}else if(numElementos == 6) { //traducao dupla 
    		splitt.setDividerPositions(0.5);
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		split.getItems().add(area_extra);
    		splitt.getItems().clear();
    		splitt.getItems().add(area_traducao);
    		splitt.getItems().add(area_traducao2);
    		mi_inter_traducao.setSelected(true);
			mi_inter_mesa.setSelected(false);
    	}else if(numElementos == 7) { //traducao dupla + teste
    		split.getItems().clear();
    		split.getItems().add(area_codigo);
    		split.getItems().add(area_extra);
    		splitt.getItems().clear();
    		splitt.getItems().add(area_traducao);
    		splitt.getItems().add(area_traducao2);
    		splitt.getItems().add(area_teste);
    		splitt.setDividerPositions(0.3, 0.67);
    		mi_inter_traducao.setSelected(true);
			mi_inter_mesa.setSelected(true);
    	}
    }
    
    public void setCss() {
    	split.getStylesheets().add(this.getClass().getResource("/css/splitpane.css").toExternalForm());
		splitt.getStylesheets().add(this.getClass().getResource("/css/splitpane.css").toExternalForm());
		tab_traducao.getStylesheets().add(this.getClass().getResource("/css/tabpane.css").toExternalForm());
		tab_traducao2.getStylesheets().add(this.getClass().getResource("/css/tabpane.css").toExternalForm());
		menu.getStylesheets().add(this.getClass().getResource("/css/menubar.css").toExternalForm());
		
		split.setStyle("-fx-box-border: #383838;");
		splitt.setStyle("-fx-box-border: #383838;");
		splitt.setDividerPositions(0.5);
		
	    codigo.getStylesheets().add(this.getClass().getResource("/css/txt.css").toExternalForm());
	    codigo.setParagraphGraphicFactory(LineNumberFactory.get(codigo));
	    codigo.setWrapText(true);
		this.resize(codigo.getPrefHeight(), codigo.getPrefWidth());
		codigo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
		codigo.appendText("variaveis\n\ninicio\n\tescrever 'Ola Mundo!';\nfim."); //texto inicial
		//area.setLineHighlighterOn(true);
		codigo = GerenciadorDeTexto.setCores(codigo);
		tbt = codigo.getUndoManager(); //vai ser usado para tratar o historico de alterações, atualmente o colorir está entrando no historico (o que nao deve acontecer ja que com o ctrl + z ao inves de desfazer ele vai descolorir)
	    tbt.forgetHistory();
		
		traducao_c.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_c.setWrapText(true);
	    traducao_c.setParagraphGraphicFactory(LineNumberFactory.get(traducao_c));
	    traducao_c.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		this.setTextTraducao("#include <stdio.h>\n\nint main(){\r\n" + "    printf(\"Ola Mundo!\");\r\n" + "    return 0;\r\n" + "}", 1);
		traducao_c = GerenciadorDeTexto.setCoresCodigo(traducao_c, 1);
		
		traducao_cplus.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_cplus.setWrapText(true);
	    traducao_cplus.setParagraphGraphicFactory(LineNumberFactory.get(traducao_cplus));
	    traducao_cplus.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
	    this.setTextTraducao("#include <iostream>\r\n" + "using namespace std;\r\n" + "\r\n" + "int main() \r\n" + "{\r\n" + "    cout << \"Ola Mundo!\";\r\n" + "    return 0;\r\n" + "}", 2);
		traducao_cplus = GerenciadorDeTexto.setCoresCodigo(traducao_cplus, 2);
		
		traducao_pascal.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_pascal.setWrapText(true);
	    traducao_pascal.setParagraphGraphicFactory(LineNumberFactory.get(traducao_pascal));
	    traducao_pascal.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
	    this.setTextTraducao("program Hello;\r\n" + "begin\r\n" + "  writeln ('Ola Mundo!');\r\n" + "end.", 3);
		traducao_pascal = GerenciadorDeTexto.setCoresCodigo(traducao_pascal, 3);
		
		traducao_java.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_java.setWrapText(true);
		traducao_java.setParagraphGraphicFactory(LineNumberFactory.get(traducao_java));
		traducao_java.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		this.setTextTraducao("public class hello {\r\n" + "\r\n\t" + "public static void main (String arg []){\r\n\t\t" + "	System.out.println(\"Ola Mundo!\");\r\n\t" + "}\r\n" + "}", 4);
		traducao_java = GerenciadorDeTexto.setCoresCodigo(traducao_java, 4);
		
		traducao_python.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_python.setWrapText(true);
		traducao_python.setParagraphGraphicFactory(LineNumberFactory.get(traducao_python));
		traducao_python.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		this.setTextTraducao("print(\"Ola Mundo!\")", 5);
		traducao_python = GerenciadorDeTexto.setCoresCodigo(traducao_python, 5);
		
		traducao_c2.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_c2.setWrapText(true);
	    traducao_c2.setParagraphGraphicFactory(LineNumberFactory.get(traducao_c2));
	    traducao_c2.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
	    this.setTextTraducaoDuplicada("#include <stdio.h>\n\nint main(){\r\n" + "    printf(\"Ola Mundo!\");\r\n" + "    return 0;\r\n" + "}", 1);
		traducao_c2 = GerenciadorDeTexto.setCoresCodigo(traducao_c2, 1);
		
		traducao_cplus2.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_cplus2.setWrapText(true);
	    traducao_cplus2.setParagraphGraphicFactory(LineNumberFactory.get(traducao_cplus2));
	    traducao_cplus2.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
	    this.setTextTraducaoDuplicada("#include <iostream>\r\n" + "using namespace std;\r\n" + "\r\n" + "int main() \r\n" + "{\r\n" + "    cout << \"Ola Mundo!\";\r\n" + "    return 0;\r\n" + "}", 2);
		traducao_cplus2 = GerenciadorDeTexto.setCoresCodigo(traducao_cplus2, 2);
		
		traducao_pascal2.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_pascal2.setWrapText(true);
	    traducao_pascal2.setParagraphGraphicFactory(LineNumberFactory.get(traducao_pascal2));
	    traducao_pascal2.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
	    this.setTextTraducaoDuplicada("program Hello;\r\n" + "begin\r\n" + "  writeln ('Ola Mundo!');\r\n" + "end.", 3);
		traducao_pascal2 = GerenciadorDeTexto.setCoresCodigo(traducao_pascal2, 3);
		
		traducao_java2.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_java2.setWrapText(true);
		traducao_java2.setParagraphGraphicFactory(LineNumberFactory.get(traducao_java2));
		traducao_java2.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		this.setTextTraducaoDuplicada("public class hello {\r\n" + "\r\n\t" + "public static void main (String arg []){\r\n\t\t" + "	System.out.println(\"Ola Mundo!\");\r\n\t" + "}\r\n" + "}", 4);
		traducao_java2 = GerenciadorDeTexto.setCoresCodigo(traducao_java2, 4);
		
		traducao_python2.getStylesheets().add(this.getClass().getResource("/css/txtTraducao.css").toExternalForm());
		traducao_python2.setWrapText(true);
		traducao_python2.setParagraphGraphicFactory(LineNumberFactory.get(traducao_python2));
		traducao_python2.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		this.setTextTraducaoDuplicada("print(\"Ola Mundo!\")", 5);
		traducao_python2 = GerenciadorDeTexto.setCoresCodigo(traducao_python2, 5);
    }
    
    private String getTextAtual() { //pegando o texto do arquivo
		try {
			String txt = "";
    		BufferedReader br = new BufferedReader(new FileReader(links.get(atual-1)));
			while(br.ready()){
				txt += br.readLine();
			}
			br.close();
			txt = txt.replace("</line>", "\n");
			txt = txt.replace("</space>", " ");
			txt = txt.replace("</tab>", "\t");
			return txt;
		} catch (Exception es) {
			es.printStackTrace();
		}
		return null;
    }
    
    private void setTextTraducao(String tex, int linguagem) {
    	
    	if(linguagem == 1) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_c.deleteText(0,traducao_c.getText().length());
            traducao_c.appendText(tex);
            traducao_c = GerenciadorDeTexto.setCores(traducao_c);
    	}else if(linguagem == 2) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_cplus.deleteText(0,traducao_cplus.getText().length());
            traducao_cplus.appendText(tex);
            traducao_cplus = GerenciadorDeTexto.setCores(traducao_cplus);
    	}else if(linguagem == 3) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_pascal.deleteText(0,traducao_pascal.getText().length());
            traducao_pascal.appendText(tex);
            traducao_pascal = GerenciadorDeTexto.setCores(traducao_pascal);
    	}else if(linguagem == 4) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_java.deleteText(0,traducao_java.getText().length());
            traducao_java.appendText(tex);
            traducao_java = GerenciadorDeTexto.setCores(traducao_java);
    	}else if(linguagem == 5) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_python.deleteText(0,traducao_python.getText().length());
            traducao_python.appendText(tex);
            traducao_python = GerenciadorDeTexto.setCores(traducao_python);
    	}
    	
    	
    }
    
    private void setTextTraducaoDuplicada(String tex, int linguagem) {
    	
    	if(linguagem == 1) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_c2.deleteText(0,traducao_c2.getText().length());
            traducao_c2.appendText(tex);
            traducao_c2 = GerenciadorDeTexto.setCores(traducao_c2);
    	}else if(linguagem == 2) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_cplus2.deleteText(0,traducao_cplus2.getText().length());
            traducao_cplus2.appendText(tex);
            traducao_cplus2 = GerenciadorDeTexto.setCores(traducao_cplus2);
    	}else if(linguagem == 3) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_pascal2.deleteText(0,traducao_pascal2.getText().length());
            traducao_pascal2.appendText(tex);
            traducao_pascal2 = GerenciadorDeTexto.setCores(traducao_pascal2);
    	}else if(linguagem == 4) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_java2.deleteText(0,traducao_java2.getText().length());
            traducao_java2.appendText(tex);
            traducao_java2 = GerenciadorDeTexto.setCores(traducao_java2);
    	}else if(linguagem == 5) {
    		if(tex == "") {
            	//this.createNewArea();
                return;
            }
            traducao_python2.deleteText(0,traducao_python2.getText().length());
            traducao_python2.appendText(tex);
            traducao_python2 = GerenciadorDeTexto.setCores(traducao_python2);
    	}
    	
    	
    }
    
    private void setTextCodigo(String tex) {
        if(tex == "") {
        	//this.createNewArea();
        	this.codigo.deleteText(0,codigo.getText().length());
        	codigo.appendText("variaveis\n\ninicio\n\nfim.");
        	this.codigo = GerenciadorDeTexto.setCores(codigo);
            return;
        }
        this.codigo.deleteText(0,codigo.getText().length());
        this.codigo.appendText(tex);
        this.codigo = GerenciadorDeTexto.setCores(codigo);
       
    }
    
    public static void setArquivoStatic(File file) {
    	ControllerCodigo.arquivo = file;
    }
    
    private void escolherArquivo() {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.getExtensionFilters().addAll( new ExtensionFilter("Portugol Files", "*.miptg"));
		arquivo = fileChooser.showOpenDialog(null);
		 
		if (arquivo != null) {
			setArquivo();
			//MainController.tx.setText("File selected: " + selectedFile.getName());
		}
		else {
			//Alertas("File selection cancelled.");
		}
    }
    
    public void setArquivo() {
    	if(arquivo != null)
    		try {
    			Scanner scanner = new Scanner(arquivo);
    			codigo.deleteText(0, codigo.getText().length());
    			while (scanner.hasNext())
    				codigo.appendText(scanner.next());
    			String txt = codigo.getText().replace("</line>", "\n");
    			txt = txt.replace("</space>", " ");
    			txt = txt.replace("</tab>", "\t");
    			codigo.deleteText(0, codigo.getText().length());
    			codigo.appendText(txt);
    			codigo = GerenciadorDeTexto.setCores(codigo);
    		} catch (Exception e) {
    			arquivo = null;
    			this.codigo.deleteText(0,codigo.getText().length());
            	codigo.appendText("variaveis\n\ninicio\n\tescrever 'Falha ao carregar o arquivo';\nfim.");
            	this.codigo = GerenciadorDeTexto.setCores(codigo);
    		}
    	
    	else
    	    //setText("");
    		return;
     }
    
    public boolean salvar(boolean i) {
       if(this.arquivo != null) {
    	   try {
    	   String txt = codigo.getText();
    	   txt = txt.replace(" ", "</space>");
    	   txt = txt.replace("\n", "</line>");
    	   txt = txt.replace("\t", "</tab>");
		   FileWriter writer = new FileWriter(this.arquivo);
		   writer.write(txt);
		   writer.close();
		   if(i)
			  Alertas.showAviso("O arquivo foi salvo!");
		   return true;
    	   }catch(IOException e) {
    		   arquivo = null;
    		   return false;
    	   }
       }else {
    	   return salvarComo();
       }
    }
    
    public boolean salvarComo() {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Save file");
    	fileChooser.setInitialFileName("codigo");
    	fileChooser.getExtensionFilters().addAll( new ExtensionFilter("Portugol Files", "*.miptg"));
    	arquivo = fileChooser.showSaveDialog(null);
    	 
    	if (arquivo != null) {
    	    return salvar(false);
    	}
    	else {
    		return false;
    	}
    }
    
    public void sobre() {
    	try {
    		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sobre/sobre.fxml"));
			Parent root1;
			root1 = (Parent) fxmlLoader.load();
			ControllerCodigo.sobre = new Stage();
			ControllerCodigo.sobre.setScene(new Scene(root1));
			ControllerCodigo.sobre.setHeight(450);
			ControllerCodigo.sobre.setWidth(600);
			ControllerCodigo.sobre.initStyle(StageStyle.UTILITY);
			ControllerCodigo.sobre.setTitle("MACP - Sobre");
			ControllerCodigo.sobre.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void console() {
    	try {
    		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Recursos/console.fxml"));
    		Parent root1;
			root1 = (Parent) fxmlLoader.load();
			controle = fxmlLoader.getController();
    		ControllerCodigo.console = new Stage();
			ControllerCodigo.console.setScene(new Scene(root1));
			ControllerCodigo.console.initStyle(StageStyle.UTILITY);
			ControllerCodigo.console.setTitle("MACP - Console");
			ControllerCodigo.console.setResizable(false);
			ArrayList<String> codigo = new ArrayList<String>();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			Date startDate = new Date();
			codigo.add("Processo iniciado em " + formatter.format(startDate));
			codigo.add("Ola Mundo!");
			Date endDate = new Date();
			codigo.add("Processo finalizado. " + (int)((endDate.getTime() - startDate.getTime()) / 1000) + " segundos de duracao");
			controle.setCodigo(codigo);
			ControllerCodigo.console.show();
			
			console.setOnCloseRequest(e -> {
    			exec = 0;
				FontAwesomeIcon icon = new FontAwesomeIcon();
				icon.setIcon(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons.PLAY_CIRCLE);
				icon.setFill(Color.web("#02ff13"));
				icon.setSize("1.5em");
				btn_executar.setGraphic(icon);
    		});
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setClose() {
    	exec = 0;
		FontAwesomeIcon icon = new FontAwesomeIcon();
		icon.setIcon(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons.PLAY_CIRCLE);
		icon.setFill(Color.web("#02ff13"));
		icon.setSize("1.5em");
		btn_executar.setGraphic(icon);
    }
    
    public void resize(double height, double width) {
    	codigo.setPrefSize(width, height);
    }


    
}

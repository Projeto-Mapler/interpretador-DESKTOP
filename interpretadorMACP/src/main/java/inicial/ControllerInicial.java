package inicial;


import com.jfoenix.controls.JFXButton;

import Recursos.Propriedades;
import codigo.ControllerCodigo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.SetBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser.ExtensionFilter;


public class ControllerInicial implements Initializable {
	
	@FXML
	Label name;
	
	@FXML
	Hyperlink link1, link2, link3, link4;
	
	@FXML
	JFXButton aprender, site, sobre, novo, continuar, abrir, config, ifma;
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	aprender.setOnAction(e -> {
    		try {
				java.awt.Desktop.getDesktop().browse( new java.net.URI( "https://www.devmedia.com.br/logica-de-programacao-introducao-a-algoritmos-e-pseudocodigo/37918"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
    	});
    	
    	site.setOnAction(e -> {
    		try {
				java.awt.Desktop.getDesktop().browse( new java.net.URI( "http://portugol.sourceforge.net"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
    	});
    	
    	sobre.setOnAction(e -> {
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
    		} catch (IOException erro) {
    			// TODO Auto-generated catch block
    			erro.printStackTrace();
    		}
    	});
    	
    	novo.setOnAction(e -> {
    		Inicial.codigo();
    	});
    	
    	continuar.setOnAction(e -> {
    		Inicial.setAutoSave = 1;
    		Inicial.codigo();
    	});
    	
    	abrir.setOnAction(e -> {
    		FileChooser fileChooser = new FileChooser();
        	fileChooser.getExtensionFilters().addAll( new ExtensionFilter("Portugol Files", "*.miptg"));
    		File arquivo = fileChooser.showOpenDialog(null);
    		 
    		if (arquivo != null) {
    			ControllerCodigo.setArquivoStatic(arquivo);
    			Inicial.setAutoSave = 2;
    			Scanner scanner;
				try {
					scanner = new Scanner(arquivo);
					String str = "";
	    			while (scanner.hasNext())
	    				str = str + scanner.next();
	    			Inicial.openFile = str;
	    			Inicial.codigo();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			//MainController.tx.setText("File selected: " + selectedFile.getName());
    		}
    		else {
    			//Alertas("File selection cancelled.");
    		}
    		
    	});
    	
    	config.setOnAction(e -> {
    		
    	});
    	
    	ifma.setOnAction(e -> {
    		try {
				java.awt.Desktop.getDesktop().browse( new java.net.URI( "https://portal.ifma.edu.br/inicio/"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
    	});
    	
    	getFiles();
    	config.setVisible(false);
    	
    	String scontinuar = Propriedades.getPropriedade("autosave");
    	if(scontinuar == null || scontinuar.equals(""))
    		continuar.setDisable(true);
    }
    
    private void getFiles() {
    	Propriedades.prepararBackup();
    	String file = Propriedades.getFile("file1");
    	if(Propriedades.getNumDeFile() == 1) {
    		link1.setText(file);
    		link1.setOnAction(e -> {
    			try {
    				File a = new File(link1.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		link1.setVisible(true);
    		
    	}else if(Propriedades.getNumDeFile() == 2) {
    		link1.setText(file);
    		link1.setOnAction(e -> {
    			try {
    				File a = new File(link1.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		file = Propriedades.getFile("file2");
    		link2.setText(file);
    		link2.setOnAction(e -> {
    			try {
    				File a = new File(link2.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		link1.setVisible(true);
    		link2.setVisible(true);
    	}else if(Propriedades.getNumDeFile() == 3) {
    		link1.setText(file);
    		link1.setOnAction(e -> {
    			try {
    				File a = new File(link1.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		file = Propriedades.getFile("file2");
    		link2.setText(file);
    		link2.setOnAction(e -> {
    			try {
    				File a = new File(link2.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		file = Propriedades.getFile("file3");
    		link3.setText(file);
    		link3.setOnAction(e -> {
    			try {
    				File a = new File(link3.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		link1.setVisible(true);
    		link2.setVisible(true);
    		link3.setVisible(true);
    	} else if(Propriedades.getNumDeFile()== 4) {
    		link1.setText(file);
    		link1.setOnAction(e -> {
    			try {
    				File a = new File(link1.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		file = Propriedades.getFile("file2");
    		link2.setText(file);
    		link2.setOnAction(e -> {
    			try {
    				File a = new File(link2.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		file = Propriedades.getFile("file3");
    		link3.setText(file);
    		link3.setOnAction(e -> {
    			try {
    				File a = new File(link3.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		file = Propriedades.getFile("file4");
    		link4.setText(file);
    		link4.setOnAction(e -> {
    			try {
    				File a = new File(link4.getText());
    				ControllerCodigo.setArquivoStatic(a);
    				try {
    					Scanner scanner = new Scanner(a);
    					String str = "";
    	    			while (scanner.hasNext())
    	    				str = str + scanner.next();
    	    			Inicial.openFile = str;
    	    			Inicial.setAutoSave = 2;
    	    			Inicial.codigo();
    				} catch (FileNotFoundException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    		});
    		link1.setVisible(true);
    		link2.setVisible(true);
    		link3.setVisible(true);
    		link4.setVisible(true);
    	}
    }

}

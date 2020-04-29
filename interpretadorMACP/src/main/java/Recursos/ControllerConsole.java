package Recursos;


import com.jfoenix.controls.JFXButton;
import com.sun.management.OperatingSystemMXBean;

import codigo.ControllerCodigo;
import inicial.Inicial;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fxmisc.richtext.StyleClassedTextArea;

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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ControllerConsole implements Initializable {
	
	@FXML
	ProgressIndicator pg_cpu, pg_ram;
	
	@FXML
	StyleClassedTextArea area_console;
	
	@FXML
	JFXButton btn_finalizar;
	
	//static
	 private ArrayList<String> codigo = new ArrayList<String>();
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	pg_ram.getStylesheets().add(this.getClass().getResource("/css/progressindicator.css").toExternalForm());
    	pg_cpu.getStylesheets().add(this.getClass().getResource("/css/progressindicator.css").toExternalForm());
    	area_console.getStylesheets().add(this.getClass().getResource("/css/txt.css").toExternalForm());
    	area_console.deleteText(0, area_console.getText().length());
    	setConsumo();
    	
    	btn_finalizar.setOnAction(e -> {
    		fechar();
    	});
    	
    }
    
    private void Executar() {
    	Iterator<String> i = codigo.iterator();
    	int aux = 0;
    	area_console.setEditable(true);
    	while(i.hasNext()){
    		if(aux == 0 || aux == codigo.size()-1) {
    			area_console.setStyleClass(area_console.getText().length(), area_console.getText().length(), "white");
    			area_console.appendText(i.next().toString()+"\n");
        		setConsumo();
        		aux ++;
        		continue;
    		}
    		aux++;
    		area_console.setStyleClass(area_console.getText().length(), area_console.getText().length(), "marcador");
    		area_console.appendText(">>" + i.next().toString()+"\n");
    		setConsumo();
    	}
    	area_console.setEditable(false);
    }
    
    private void setConsumo() {
    	Runtime run = Runtime.getRuntime();
    	OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    	pg_ram.setProgress((double)(run.totalMemory() - run.freeMemory())/run.totalMemory());
    	pg_cpu.setProgress(osBean.getProcessCpuLoad());
    }
    
    public void setCodigo(ArrayList<String> cod) {
    	codigo = cod;
    	//System.out.println("Adicionando "+cod.size());
    	Executar();
    }
    
    public void fechar() {
    	ControllerCodigo.console.hide();
    	ControllerCodigo.console.close();
    	Inicial.controle.setClose();
    }
}

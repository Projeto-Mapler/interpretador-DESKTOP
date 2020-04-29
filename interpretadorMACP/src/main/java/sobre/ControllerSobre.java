package sobre;


import com.jfoenix.controls.JFXButton;

import codigo.ControllerCodigo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ControllerSobre implements Initializable {
	
	@FXML
	Hyperlink link1;
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	
    	link1.setOnAction(e -> {
    		try {
				java.awt.Desktop.getDesktop().browse( new java.net.URI( "http://portugol.sourceforge.net"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
    	});
    	
    }
    
    

}

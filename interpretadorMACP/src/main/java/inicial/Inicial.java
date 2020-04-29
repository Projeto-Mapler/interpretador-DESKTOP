package inicial;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.gluonhq.charm.glisten.control.ProgressBar;

import Recursos.Alertas;
import Recursos.Propriedades;
import codigo.ControllerCodigo;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public class Inicial extends Application {
    
    public static double htela, wtela;
    private static Scene scene;
    private static Stage stage;
    public static int setAutoSave;
    public static String openFile = "";
	private static int tela = 1;
	public static ControllerCodigo controle;
    
    @Override
    public void start(Stage stage) throws Exception {
    	Inicial.stage = stage;
    	Inicial.setAutoSave = 0;
    	
    	Properties properties = System.getProperties();
    	String so = String.valueOf( System.getProperty("os.name") );
    	Parent root = FXMLLoader.load(getClass().getResource("inicial.fxml"));
    	ControllerCodigo.sobre = new Stage();
    	
    	Inicial.scene = new Scene(root);
        //stage.getIcons().add(new Image(getClass().getResourceAsStream("madein.png")));
        Inicial.stage.setScene(Inicial.scene);
        Inicial.stage.setTitle("MACP"); //#CompiladorNo"+so.replace(" ","")
        Inicial.stage.setMinHeight(680.0);
        Inicial.stage.setMinWidth(960.0);
        
        Inicial.stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            	Inicial.wtela = stage.getWidth();
            }
        });

        Inicial.stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            	Inicial.htela = stage.getHeight();
            }
        });
        
        Inicial.stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
            	Inicial.wtela = stage.getWidth();
            	Inicial.htela = stage.getHeight();
            }
        });
        
        Inicial.stage.setOnCloseRequest(e -> Platform.exit());
        
        Inicial.stage.show();
        //System.out.println("Stage: "+stage.getHeight());
        
    }
    
    @FXML
    public void exitApplication(ActionEvent event) {
        	stop();
    }
    
    @Override
    public void stop(){
    	if(tela == 2) {
    		//System.out.println("salvar");
    	}
    	Platform.exit();
    }

    public static void iniciar() {
    	Parent root;
		try {
			tela = 1;
			root = FXMLLoader.load(Inicial.class.getResource("inicial.fxml"));
			Inicial.scene = new Scene(root);
			Inicial.stage.setScene(Inicial.scene);
			Inicial.stage.setMinHeight(Inicial.htela);
	        Inicial.stage.setMinWidth(Inicial.wtela);
	        Inicial.stage.hide();
	        Inicial.stage.setMaximized(true);
	        Inicial.stage.setMaximized(false);
	        Inicial.stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
    
    public static void codigo() {
    	Parent root;
		try {
			tela = 2;
			FXMLLoader fxmlLoader = new FXMLLoader(Inicial.class.getResource("/codigo/codigo.fxml"));
			root = fxmlLoader.load();
			controle = fxmlLoader.getController();
			Inicial.scene = new Scene(root);
			Inicial.stage.setScene(Inicial.scene);
			Inicial.stage.setMinHeight(Inicial.htela);
	        Inicial.stage.setMinWidth(Inicial.wtela);
	        Inicial.stage.hide();
	        Inicial.stage.setMaximized(true);
	        Inicial.stage.setMaximized(false);
	        Inicial.stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    
    public static void main(String[] args) {
        launch(args);
    }
    
}


package com.pasegados.emulador;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que inicia el programa donde se muestra una emulación del equipo
 * OXFORD LAB-X3500 y lo que se mostraría en su pantalla trasn recibir unos determinados
 * datos a traves del puerto COM
 * 
 * @author Pedro Antonio Segado Solano
 */
public class Main extends Application {
    
    private static VistaController controlador; // Acceso al controlador de la vista

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        launch(args);
    }
    
    //Devuelve el controlador de la vista
    public static VistaController getControlador(){
        return controlador;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("vista.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {}            

        controlador = loader.getController(); //Asignamos el controlador de la vista a la variable controlador
        Scene scene = new Scene(root);
        
        scene.getStylesheets().add(this.getClass().getResource("aspecto.css").toExternalForm());

        stage.setTitle("Emulador OXFORD LabX-3500 para pruebas del software controlador");
        stage.setMinHeight(180d);        
        stage.setMinWidth(665d);
        stage.setMaxHeight(685d);        
        stage.setMaxWidth(665d);
        stage.setResizable(true);
              
        stage.setScene(scene);
        stage.show();
    }    
}
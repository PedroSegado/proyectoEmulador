
package com.pasegados.emulador;

import java.io.IOException;
import java.util.ArrayList;
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
    private static ArrayList<Calibrado> listaCal = new ArrayList();

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
       
       // Creo los tres calibrados de prueba, coincidentes con los que crea la BBDD de prueba de la aplicacion
       Calibrado bajo = new Calibrado("AZUFRE BAJO", 20, 1, 1, 1, 1, 0, 0.000085812200524, -0.2316941753);
       listaCal.add(bajo);
       Calibrado medio = new Calibrado("AZUFRE MEDIO", 20, 1, 2, 1, 2, 0.0000000005832865211765, 0.000075021149552202, -0.236976);
       listaCal.add(medio);
       Calibrado alto = new Calibrado("AZUFRE ALTO", 20, 2, 1, 2, 1, 0.00000000214322618239, 0.000071686700045, -0.0095443);
       listaCal.add(alto);
       
        
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

    public static ArrayList<Calibrado> getListaCal() {
        return listaCal;
    }
    
    public static Calibrado getCalibrado(String nombre) {
        Calibrado cal = null;
        for (Calibrado c : listaCal){
            if (c.getNombre().equals(nombre)) cal = c;
        }
        return cal;
    }
    
    
}
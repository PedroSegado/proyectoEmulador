package com.pasegados.emulador;

import com.fazecast.jSerialComm.SerialPort;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author Pedro Antonio Segado Solano
 */
public class VistaController implements Initializable {

    //Botones
    @FXML
    private Button btIniciar;
    @FXML
    private Button btLimpiar;

    //ComboBox
    @FXML
    private ComboBox<String> cbPuerto;
    private ObservableList<String> listaPuertos = FXCollections.observableArrayList();
    @FXML
    private ComboBox<Integer> cbBPS;
    private ObservableList<Integer> listaBPS = FXCollections.observableArrayList();
    @FXML
    private ComboBox<Integer> cbBDD;
    private ObservableList<Integer> listaBDD = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> cbPAR;
    private ObservableList<String> listaPAR = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> cbBDP;
    private ObservableList<String> listaBDP = FXCollections.observableArrayList();

    //TextArea
    @FXML
    private TextArea taDatos;
    @FXML
    private TextArea taPantalla;

    //Otros
    private static final SerialPort[] PORTS = Puerto.getPuertosSistema();
    private Puerto puerto;
    public int menu = 0; //posicion inicial del menú (máquina de estados)
    private String calibrado; // Almacena el calibrado con el que se realiza el analisis
    private String id; // Almacena el identificador de la muestra a analizar    
    private Medida medida; // Almacena el hilo de cuenta atras que controla la medida
    private Condicionamiento acondicionamiento; // Almacena el hilo de cuenta atras que controla el acondicionamiento
    private Energia ajusteEnergia; // Almacena el hilo de cuenta atras que controla el ajuste de energia
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Combobox PUERTOS + Busca puertos COM en el equipo
        for (SerialPort port : PORTS) {
            listaPuertos.add(port.getSystemPortName()); //añade los puertos al observablelist
        }
        cbPuerto.setItems(listaPuertos); //asigna la observablelist al combobox

        // Combobox BPS
        listaBPS.addAll(600, 1200, 2400, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000);
        cbBPS.setItems(listaBPS);
        cbBPS.setValue(2400);

        //Combobx BDD
        listaBDD.addAll(4, 5, 6, 7, 8);
        cbBDD.setItems(listaBDD);
        cbBDD.setValue(8);

        //Combobox PARIDAD
        listaPAR.addAll("Par", "Impar", "Ninguna", "Marca", "Espacio");
        cbPAR.setItems(listaPAR);
        cbPAR.setValue("Ninguna");

        //Combobox BDP
        listaBDP.addAll("1", "1.5", "2");
        cbBDP.setItems(listaBDP);
        cbBDP.setValue("1");
        
        menuInicioEquipo();  
    }

    @FXML
    private void iniciaConexion(ActionEvent event) {

        if (btIniciar.getText().equals("INICIAR")) {

            //Creamos el objeto puerto con el que manejar la conexión
            puerto = new Puerto(cbPuerto.getValue(), cbBPS.getValue(), cbBDD.getValue(), cbPAR.getValue(), cbBDP.getValue());

            if (puerto.activaPuerto()) {
                System.out.println("Conexión exitosa");
                btIniciar.setText("PARAR");
                puerto.escucharPuerto(); //Empieza a escuchar
            } else {
                System.out.println("La conexión al puerto ha fallado");
            }
        }
        else if (btIniciar.getText().equals("PARAR")) {
            puerto.cierraConexion();
            btIniciar.setText("INICIAR");            
        }
    }

    //Borra el texto del texArea, por si acumulamos mucho
    @FXML
    private void limpiarDatos(ActionEvent event) {
        //Borra el contenido del textarea
        taDatos.clear();        
    }

    //Devuelve el taDatos, para acceder desde otras clases del programa
    public TextArea getTextAreaDatos(){
        return taDatos;
    }
    
    //Devuelve el taPantalla, para acceder desde otras clases del programa
    public TextArea getTextAreaPantalla(){
        return taPantalla;
    }
    
    //Definimos el texto de todos los menús del equipo OXFORD, y las opciones a elegir en cada uno.
    public void menuInicioEquipo(){ //Menu 1: Al encender el equipo.
        menu=1;
        taPantalla.clear();
        taPantalla.appendText("         LAB-X 3500 PROGRAMAS ANALITICOS" + "\n");        
        taPantalla.appendText("LZ 1.0                       <ENTER> PARA CONTINUAR");                
    }
        
    public void menuPrincipal(){ //Menu 2: Primer menú del equipo, donde el usuario ya tiene que elegir
        menu=2;
        taPantalla.clear();
        taPantalla.appendText("                    : MENU PRINCIPAL:" + "\n");        
        taPantalla.appendText("1=ANALISIS                       2=OTRAS FUNCIONES");  //1 nos lleva al menú 3      
    }                                                                  //2 no estará implementado 
    
    public void menuAnalisis1(){ //Menu 3: Elección de calibrado de analisis(1)
        menu=3;
        taPantalla.clear();
        taPantalla.appendText(" 1=AZUFRE BAJO                  2=AZUFRE MEDIO" + "\n");
        taPantalla.appendText("3=LISTAR NOMBRES             4=CAMBIAR PAGINA"); //1 y 2 nos llevan al menú 6
                                                                     //3 no estará implementado
    }                                                                //4 nos lleva al menú 4
    
    public void menuAnalisis2(){ //Menu 4: Elección de calibrado de analisis(2)
        menu=4;
        taPantalla.clear();
        taPantalla.appendText(" 1=AZUFRE ALTO" + "\n");
        taPantalla.appendText("3=LISTAR NOMBRES             4=CAMBIAR PAGINA"); //1 nos lleva al menú 6
                                                                     //3 no estará implementado
    }                                                                 //4 nos lleva al menú 5
        
    public void menuAnalisis3(){ //Menu 5: Info de que no hay mas calibraciones
        menu=5;
        taPantalla.clear();
        taPantalla.appendText("Sin mas calibraciones" + "\n");
        taPantalla.appendText("Tecle <ENTER> para continuar"); //Enter nos lleva al menú 2
    }
    
    public void menuEnsayo1(String calibrado){ //Menu 6: Configuración inicial del ensayo con el calibrado seleccionado previamente
        menu=6;
        taPantalla.clear();
        taPantalla.appendText("Nombre de la calibracion: " + calibrado + "\n");
        taPantalla.appendText("1=MANUAL     2=CAMBIADOR     3=RECAL     4=FIN");  // 1 nos lleva le menú 7 
                                                                            // 2 y 3 no estarán implementados
    }                                                                       // 4 nos lleva le menú 2
        
    public void menuEnsayo2(String calibrado, String id){ //Menu 7: Identificación de la muestra a analizar
        menu=7;
        taPantalla.clear();
        taPantalla.appendText("Nombre de la calibracion: " + calibrado + "\n");
        taPantalla.appendText("Identificacion: " + id); // ENTER nos lleva al menú 8
    }
    
    public void menuEnsayo3(String muestra){ //Menu 8: Petición introducción de muestra en el equipo
        menu=8;
        taPantalla.clear();
        taPantalla.appendText("Introducir muestra " + muestra + "\n");
        taPantalla.appendText("Tecle <YES> cuando colocada"); // YES(y) o ENTER nos llevan al menú 9
    }                                                         // NO(n) O ESCAPE nos llevan al menú 6
    
    public void menuCondicionando1(){ //Menu 9: Acondicionamiento(1) del equipo
        menu=9;
        taPantalla.clear(); //dura 13 segundos y pasa al siguiente menú, no responde a nada ni se puede cancelar        
        taPantalla.appendText("CONDICIONANDO" + "\n");
        taPantalla.appendText("Favor Esperar"); 
    }                                                        
    
    public void menuCondicionando2(int segundo){ //Menu 10: Acondicionamiento(2) del equipo
        menu=10;
        taPantalla.clear();
        taPantalla.appendText("CONDICIONANDO" + "\n");
        taPantalla.appendText("(" + segundo + ")"); //Realiza una cuenta atras de 10 segundos. ¿Cancelar con ESC?
    }                                               //cuenta atras controlada por un hilo de la clase "Condicionamiento"    

    public void menuAjusteEnergia1(){ //Menu 11: Ajuste interno de energia(1)
        menu=11;
        taPantalla.clear(); //dura 3 segundos y pasa al siguiente, no responde a nada ni se puede cancelar        
        taPantalla.appendText("AJUSTANDO ENERGIA 1" + "\n"); 
        taPantalla.appendText("Favor Esperar"); 
    }  
    
    public void menuAjusteEnergia2(int segundo){ //Menu 12: Ajuste interno de energia(2)
        menu=12;
        taPantalla.clear();
        taPantalla.appendText("AJUSTANDO ENERGIA 1" + "\n"); //Realiza una cuenta atras de 5 segundos. ¿Cancelar con ESC?
        taPantalla.appendText("DAC=2812   TIEMPO RESTANTE=(" + segundo + ")"); 
    }                                                        //cuenta atras controlada por un hilo de la clase "Energia"
    
    
    public void menuMidiendo(String identificacion, float medida, String calibrado, int segundo){ 
        menu=13;    //Menu 13: Medida por los segundos correspondientes al calibrado (100 o 200 normalmente)
        taPantalla.clear();
        String med = String.format("%.4f", medida); //Formateamos para mostrar siempre 4 decimales
        taPantalla.appendText("Muestra: " + identificacion +   "                             S=" + med + "MASS%" + "\n");
        taPantalla.appendText(calibrado + "                         SEGUNDOS="+ segundo); 
    }                                    //cuenta atras controlada por un hilo de la clase "Medida"
                                         //ESCAPE nos devuelve al menú 8 (introducción de muestra en el equipo)
    
    public void menuResultado(float medida, String calibrado){ //Menu 14: Muestra resultado del analisis
        menu=14;
        taPantalla.clear();
        String med = String.format("%.4f", medida); //Formateamos para mostrar siempre 4 decimales
        taPantalla.appendText(calibrado + "   S=" + med + "MASS%" + "\n");
        taPantalla.appendText("Tecle <ENTER> para continuar"); //ENTER nos devuelve al menú 6 (inicio de ensayo del mismo calibrado)
    } 
    
    public Puerto getPuerto(){
        return puerto;
    }    
    
    
    //--------------------------------------------------------------------------
    //RECEPCION DE DATOS DEL PUERTO
    public void recibeDatos(byte[]buffer){
          for (byte b : buffer) {
                    System.out.println(b);
                }

                switch (menu) {
                    case 1:
                        inicioEquipo(buffer);
                        break;
                    case 2:
                        menuPrincipal(buffer);
                        break;
                    case 3:
                        menuAnalisis1(buffer);
                        break;
                    case 4:
                        menuAnalisis2(buffer);
                        break;
                    case 5:
                        menuAnalisis3(buffer);
                        break;
                    case 6:
                        menuEnsayo1(buffer);
                        break;
                    case 7:
                        menuEnsayo2(buffer);
                        break;
                    case 8:
                        menuEnsayo3(buffer);
                        break;
                    case 9:
                        menuCondicionando1(buffer);
                        break;
                    case 10:
                        menuCondicionando2(buffer);
                        break;
                    case 11:
                        menuAjusteEnergia1(buffer);
                        break;
                    case 12:
                        menuAjusteEnergia2(buffer);
                        break;
                    case 13:
                        menuMidiendo(buffer);
                        break;
                    case 14:
                        menuResultado(buffer);
                        break;
               }
    }
    
    //SECUENCIA MENUS, MAQUINA DE ESTADOS
    
    public void inicioEquipo(byte[] array) {//Menu 1
        if (array[0] == 0x0d) { // Solo si se recibe pulsacion ENTER , puede ser necesario incluir 0x0a
            taDatos.appendText("Recibido ENTER inicial" + "\n");
            menuPrincipal();
        }
    }
    
    public void menuPrincipal(byte[] array) { //Menu 2        
        if (array[0] == 49) {
            taDatos.appendText("Seleccionada opcion ANALISIS del menu" + "\n");
            menuAnalisis1();
        }
        else if (array[0] == 50) {
            taDatos.appendText("Seleccionada opcion OTRAS FUNCIONES del menu" + "\n");
            //No implementado, innecesario para el proyecto      
        }
    }

    public void menuAnalisis1(byte[] array) { //Menu 3        
        switch (array[0]) {
            case 49:
                taDatos.appendText("Seleccionada opcion AZUFRE BAJO del menu" + "\n");
                menuEnsayo1("AZUFRE BAJO");
                calibrado = "AZUFRE BAJO";
                break;
            case 50:
                taDatos.appendText("Seleccionada opcion AZUFRE MEDIO del menu" + "\n");
                menuEnsayo1("AZUFRE MEDIO");
                calibrado = "AZUFRE MEDIO";
                break;
            case 51:
                taDatos.appendText("Seleccionada opcion LISTAR NOMBRES del menu" + "\n");
                break;
            //No implementado, innecesario para el proyecto
            case 52:
                taDatos.appendText("Seleccionada opcion CAMBIAR PAGINA del menu" + "\n");
                menuAnalisis2();
        }
    }
    
    public void menuAnalisis2(byte[] array) { //Menu 4              
        switch (array[0]) {
            case 49: // 1
                taDatos.appendText("Seleccionada opcion AZUFRE ALTO del menu" + "\n");
                menuEnsayo1("AZUFRE ALTO");
                calibrado = "AZUFRE ALTO";
                break;
            case 51: // 3 (no existe el 2 en esta pantalla)
                taDatos.appendText("Seleccionada opcion LISTAR NOMBRES del menu" + "\n");
                // No implementada
                break;
            //No implementado, innecesario para el proyecto
            case 52: // 4
                taDatos.appendText("Seleccionada opcion CAMBIAR PAGINA del menu" + "\n");
                menuAnalisis3();
        }
    }
            
    public void menuAnalisis3(byte[] array) { //Menu 5             
        if (array[0] == 0x0d) { // Solo si se recibe pulsacion ENTER , puede ser necesario incluir 0x0a
            taDatos.appendText("Recibido ENTER" + "\n");
            menuPrincipal();
        }
    }
    
    public void menuEnsayo1(byte[] array) { //Menu 6       
        switch (array[0]) {
            case 49: // 1
                taDatos.appendText("Seleccionada opcion MANUAL del menu" + "\n");
                id = "";
                menuEnsayo2(calibrado, id);
                break;
            case 50: // 2
                taDatos.appendText("Seleccionada opcion CAMBIADOR del menu" + "\n");
                // No implementada
                break;
            //No implementado, innecesario para el proyecto
            case 51: // 3
                taDatos.appendText("Seleccionada opcion RECAL del menu" + "\n");
                // No implementada
                break;
            //No implementado, innecesario para el proyecto
            case 52: // 4
                taDatos.appendText("Seleccionada opcion FIN del menu" + "\n");
                menuPrincipal(); //Volvemos al menú principal
                break;
        }
    }

    public void menuEnsayo2(byte[] array) { //Menu 7: Introducir identificacion muestra        
        if (id.length() < 9 && array[0] != 0x0d) {
            for (byte b : array) {
                id = id + String.valueOf((char) b);
                id = id.substring(0, id.length());
                menuEnsayo2(calibrado, id);
            }
        }
        //Confirmamos la id pulsando ENTER
        if (array[0] == 0x0d) { // Solo si se recibe pulsacion ENTER , puede ser necesario incluir 0x0a
            taDatos.appendText("Recibida identificacion " + id + "\n");
            taDatos.appendText("Recibido ENTER" + "\n");
            menuEnsayo3(id);
        }
    }

    public void menuEnsayo3(byte[] array) { //Menu 8: YES(y) o ENTER inician el analisis. NO(n) o ESCAPE nos devuelven a menuEnsayo1  
        if (array[0] == 121 || array[0] == 0x0d) { // YES o ENTER, iniciamos medida 
            taDatos.appendText("Recibido YES" + "\n");
            //Equipo inicia acondicionamiento                
            iniciaAcondicionamiento();
        }  
        else if (array[0] == 110 || array[0] == 27) {  // NO o ESCAPE, volvemos al menúEnsayo1
            taDatos.appendText("Recibido NO" + "\n");
            menuEnsayo1(calibrado);
        }
    }
        
    public void menuCondicionando1(byte[] array) { //Menu 9
        //No responde a ninguna tecla, obligatorio esperar
    }
    
    public void menuCondicionando2(byte[] array) { //Menu 10
    if (array[0] == 27) { //Si se pulsa ESCAPE durante la cuenta atrás del acondicionamiento
            acondicionamiento.setAcondicionando(false); //para detener el hilo que actualiza cada segundo
            taDatos.appendText("Recibido ESCAPE. Deteniendo el acondicionamiento" + "\n");
            menuEnsayo3(calibrado);  //Volvemos al menu para insertar la muestra e iniciar su analisis          
        }  
    }
    
    public void menuAjusteEnergia1(byte[] array) { //Menu 11
        //No responde a ninguna tecla, obligatorio esperar
    }
       
    public void menuAjusteEnergia2(byte[] array) { //Menu 12
    if (array[0] == 27) { //Si se pulsa ESCAPE durante la cuenta atrás del ajuste de energia
            ajusteEnergia.setAjustando(false); //para detener el hilo que actualiza cada segundo
            taDatos.appendText("Recibido ESCAPE. Deteniendo el ajuste de energia" + "\n");
            menuEnsayo3(calibrado);  //Volvemos al menu para insertar la muestra e iniciar su analisis          
        }  
    }    
    
    public void menuMidiendo(byte[] array) { //Menu 13
        if (array[0] == 27) { //Si se pulsa ESCAPE durante la medida
            medida.setMidiendo(false); //para detener el hilo que actualiza la medida cada segundo
            taDatos.appendText("Recibido ESCAPE. Deteniendo la medición" + "\n");
            menuEnsayo3(calibrado);  //Volvemos al menu para insertar la muestra e iniciar su analisis          
        }        
    }   
       
    public void menuResultado(byte[] array) { //Menu 14
        if (array[0] == 0x0d) { //Si se pulsa ENTER en la pantalla de resultado, una vez terminado el análisis
            taDatos.appendText("Recibido ENTER. Volviendo al menu de ensayo" + "\n");
            menuEnsayo1(calibrado);  //Volvemos al menu de ensayo de esa calibración          
        }        
    } 
        
    public void iniciaAcondicionamiento(){
        acondicionamiento = new Condicionamiento(10);
        acondicionamiento.start();
    }
    
    public void iniciaAjusteEnergia(){
        ajusteEnergia = new Energia(5);
        ajusteEnergia.start();
    }
    
    public void iniciaMedida(){
        medida = new Medida(id, calibrado, 10); //10 segundos para pruebas
        medida.start();
    }    
}
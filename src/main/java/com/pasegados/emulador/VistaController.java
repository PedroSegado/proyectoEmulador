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
    private Calibrado calibrado; // Almacena el calibrado con el que se realiza el analisis
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
    }                                                                                 //2 nos lleva al menu 15
    
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
    
    
    public void menuMidiendo(String identificacion, double medida, String calibrado, int segundo){ 
        menu=13;    //Menu 13: Medida por los segundos correspondientes al calibrado (100 o 200 normalmente)
        taPantalla.clear();
        String med = String.format("%.4f", medida); //Formateamos para mostrar siempre 4 decimales
        taPantalla.appendText("Muestra: " + identificacion +   "                            S=" + med + "MASS%" + "\n");
        taPantalla.appendText(calibrado + "                         SEGUNDOS="+ segundo); 
    }                                    //cuenta atras controlada por un hilo de la clase "Medida"
                                         //ESCAPE nos devuelve al menú 8 (introducción de muestra en el equipo)
    
    public void menuResultado(double medida, String calibrado){ //Menu 14: Muestra resultado del analisis
        menu=14;
        taPantalla.clear();
        String med = String.format("%.4f", medida); //Formateamos para mostrar siempre 4 decimales
        taPantalla.appendText(calibrado + "   S=" + med + "MASS%" + "\n");
        taPantalla.appendText("Tecle <ENTER> para continuar"); //ENTER nos devuelve al menú 6 (inicio de ensayo del mismo calibrado)
    } 
    
    
    
    // ------ ACCEDER AL CALIBRADO --------- //
    public void menuPassword1(){ //Menu 15
        menu=15;
        taPantalla.clear();        
        taPantalla.appendText(" Entre password del operador: ---------" + "\n");
        taPantalla.appendText(" TECLE <ESCAPE> PARA SALIR"); // ENTER nos lleva al menú 16 y ESC al menú 2
    }   
    public void menuOtrasFunciones(){ //Menu 16
        menu=16;
        taPantalla.clear();        
        taPantalla.appendText(" 1=ESPECTRO                  2=CALIBRACION" + "\n");
        taPantalla.appendText("3=UTILITARIOS             4=MENU PRINCIPAL"); // 2 nos lleva al menú 17 y 4 nos lleva al menú 2
    }    
    
    public void menuPassword2(){ //Menu 17
        menu=17;
        taPantalla.clear();        
        taPantalla.appendText(" ENTRE PASSWORD DEL RESPONSABLE: ---------" + "\n");
        taPantalla.appendText(" TECLE <ESCAPE> PARA SALIR"); // ENTER nos lleva al menú 18 y ESC al menú 2
    }    
    
    public void menuCalibracion1(){ //Menu 18
        menu=18;
        taPantalla.clear();        
        taPantalla.appendText(" 1=Nueva calibracion   2=Calibracion existe" + "\n");
        taPantalla.appendText("        "); // 2 nos lleva al menú 19
    }   
    
    public void menuCalibracion2(){ //Menu 19
        menu=19;
        taPantalla.clear();                
        taPantalla.appendText(" 1=LZMET048                  2=AZUFRE BAJO" + "\n");
        taPantalla.appendText("3=LISTAR NOMBRES             4=CAMBIAR PAGINA"); // 2 nos lleva al menú  y 4 al menú 20
    }   
    
    public void menuCalibracion3(){ //Menu 20
        menu=20;
        taPantalla.clear();                
        taPantalla.appendText(" 1=LZMET049                  2=AZUFRE MEDIO" + "\n");
        taPantalla.appendText("3=LISTAR NOMBRES             4=CAMBIAR PAGINA"); // 2 nos lleva al menú   y 4 al menú 21
    }   
    
    public void menuCalibracion4(){ //Menu 21
        menu=21;
        taPantalla.clear();                
        taPantalla.appendText(" 1=LZMET050                  2=AZUFRE ALTO" + "\n");
        taPantalla.appendText("3=LISTAR NOMBRES             4=CAMBIAR PAGINA"); // 2 nos lleva al menú   y 4 al menú 22
    }   
    
    public void menuCalibracion5(){ //Menu 22  //REVISAR SI ES ASI
        menu=22;
        taPantalla.clear();                
        taPantalla.appendText("Sin mas calibraciones" + "\n");
        taPantalla.appendText("Tecle <ENTER> para continuar"); //Enter nos lleva al menú 16
    }   
    
    public void menuModificarCalibracion1(){ //Menu 23
        menu=23;
        taPantalla.clear();                
        taPantalla.appendText("1=REVISION     2=COPIA     3=LISTA     4=MANDAR" + "\n");
        taPantalla.appendText("5=BORRAR           6=ANALIZAR            7=SALIR"); // 1 nos lleva al menú 24 y 7 al menú 16
    }   
    
    public void menuModificarCalibracion2(){ //Menu 24
        menu=24;
        taPantalla.clear();                
        taPantalla.appendText("Nombre de la calibración:" + "\n");
        taPantalla.appendText(calibrado.getNombre()+"-".repeat(15-calibrado.getNombre().length())); // ENT nos lleva al menú 25
    }  
    
    public void menuModificarCalibracion3(){ //Menu 25
        menu=25;
        taPantalla.clear();                
        taPantalla.appendText(calibrado.getNombre()+":" + "\n");
        taPantalla.appendText("1=CONDICIONES     2=SEGMENTOS     3=CONTINUAR"); // 3 nos lleva al menú 26
    }  
    
    public void menuModificarCalibracion4(){ //Menu 26
        menu=26;
        taPantalla.clear();                
        taPantalla.appendText("1=PATRONES                  2=REGRESION" + "\n");
        taPantalla.appendText("3=CORRECCIONES            4=CONTINUAR"); // 2 nos lleva al menu 31
    }                                                                   // 4 nos lleva al menu 27

    public void menuModificarCalibracion5(){ //Menu 27
        menu=27;
        taPantalla.clear();                
        taPantalla.appendText("1=ALTAS Y BAJAS SUS      2=REPETICIONES" + "\n");
        taPantalla.appendText("3=RECALIBRACION            4=CONTINUAR"); // 4 nos lleva al menu 28
    }   
    
    public void menuModificarCalibracion6(){ //Menu 28
        menu=28;
        taPantalla.clear();                
        taPantalla.appendText("FORMA DE IMPRESION:" + "\n");
        taPantalla.appendText("1=NORMAL   2=REDUCIDO  3=PERSONALIZADO"); // ENT, 1, 2 o 3 nos llevan al menu 29
    }  
    
    public void menuModificarCalibracion7(){ //Menu 29
        menu=29;
        taPantalla.clear();                
        taPantalla.appendText("1=FORMA RS232            2=OTROS" + "\n");
        taPantalla.appendText("3=BORRAR PATRON       4=CONTINUAR"); // 4 nos lleva al menu 30
    }                                                               
    
    public void menuModificarCalibracion8(){ //Menu 30
        menu=30;
        taPantalla.clear();                
        taPantalla.appendText("¿ARCHIVAR CALIBRACION?:  <YES> <NO>" + "\n");  // YES nos lleva al menu 23        
    }  
    
    public void menuModificarRegresion1(){ //Menu 31
        menu=31;
        taPantalla.clear();                
        taPantalla.appendText("1=DIFERENCIAS     2=GRAF.1     3=GRAF.2" + "\n");
        taPantalla.appendText("4=INTESIDADES     5=MANUAL     6=SALIR"); // 5 nos lleva al menu 32
    }                                                                  // 6 nos lleva al menu 26
    
    public void menuModificarRegresion2(){ //Menu 32
        menu=32;
        taPantalla.clear();                
        taPantalla.appendText("COEFICIENTES PARA S       (ORIGEN)"+"\n");
        taPantalla.appendText("ENTRE A(0):" + calibrado.getTermInd() + "-".repeat(18 - (String.valueOf(calibrado.getTermInd())).length())); 
    }   // Usuario enviara el dato para sustituir al actual o pulsara enter para seguir manteniendolo 
    
    public void menuModificarRegresion3(){ //Menu 33
        menu=33;
        taPantalla.clear();                
        taPantalla.appendText("COEFICIENTES PARA S       (PENDIENTE)"+"\n");
        taPantalla.appendText("ENTRE A(1):" + calibrado.getCoefLin() + "-".repeat(18 - (String.valueOf(calibrado.getCoefLin())).length())); 
    }   // Usuario enviara el dato para sustituir al actual o pulsara enter para seguir manteniendolo 
    
    public void menuModificarRegresion4(){ //Menu 34
        menu=34;
        taPantalla.clear();                
        taPantalla.appendText("COEFICIENTES PARA S       (EFECTO DE S)"+"\n");
        taPantalla.appendText("ENTRE A(2):" + calibrado.getCoefCuad() + "-".repeat(18 - (String.valueOf(calibrado.getCoefCuad())).length())); 
    }   // Usuario enviara el dato para sustituir al actual o pulsara enter para seguir manteniendolo 



    
    
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
                        
                    case 15:
                        menuPassword1(buffer);
                        break;
                    case 16:
                        menuOtrasFunciones(buffer);
                        break;
                    case 17:
                        menuPassword2(buffer);
                        break;
                    case 18:
                        menuCalibracion1(buffer);
                        break;
                    case 19:
                        menuCalibracion2(buffer);
                        break;
                    case 20:
                        menuCalibracion3(buffer);
                        break;
                    case 21:
                        menuCalibracion4(buffer);
                        break;
                    case 22:
                        menuCalibracion5(buffer);
                        break;
                    case 23:
                        menuModificarCalibracion1(buffer);
                        break;
                    case 24:
                        menuModificarCalibracion2(buffer);
                        break;
                    case 25:
                        menuModificarCalibracion3(buffer);
                        break;
                    case 26:
                        menuModificarCalibracion4(buffer);
                        break;
                    case 27:
                        menuModificarCalibracion5(buffer);
                        break;
                    case 28:
                        menuModificarCalibracion6(buffer);
                        break;
                    case 29:
                        menuModificarCalibracion7(buffer);
                        break;
                    case 30:
                        menuModificarCalibracion8(buffer);
                        break;
                    case 31:
                        menuModificarRegresion1(buffer);
                        break;
                    case 32:
                        menuModificarRegresion2(buffer);
                        break;
                    case 33:
                        menuModificarRegresion3(buffer);
                        break;
                    case 34:
                        menuModificarRegresion4(buffer);
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
            menuPassword1();
        }
    }

    public void menuAnalisis1(byte[] array) { //Menu 3        
        switch (array[0]) {
            case 49:
                taDatos.appendText("Seleccionada opcion AZUFRE BAJO del menu" + "\n");
                calibrado = Main.getCalibrado("AZUFRE BAJO");                
                menuEnsayo1(calibrado.getNombre());    
                break;
            case 50:
                taDatos.appendText("Seleccionada opcion AZUFRE MEDIO del menu" + "\n");
                calibrado = Main.getCalibrado("AZUFRE MEDIO");                                
                menuEnsayo1(calibrado.getNombre());    
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
                calibrado = Main.getCalibrado("AZUFRE ALTO");                         
                menuEnsayo1(calibrado.getNombre());
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
                menuEnsayo2(calibrado.getNombre(), id);
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
                menuEnsayo2(calibrado.getNombre(), id);
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
            menuEnsayo1(calibrado.getNombre());
        }
    }
        
    public void menuCondicionando1(byte[] array) { //Menu 9
        //No responde a ninguna tecla, obligatorio esperar
    }
    
    public void menuCondicionando2(byte[] array) { //Menu 10
    if (array[0] == 27) { //Si se pulsa ESCAPE durante la cuenta atrás del acondicionamiento
            acondicionamiento.setAcondicionando(false); //para detener el hilo que actualiza cada segundo
            taDatos.appendText("Recibido ESCAPE. Deteniendo el acondicionamiento" + "\n");
            menuEnsayo3(calibrado.getNombre());  //Volvemos al menu para insertar la muestra e iniciar su analisis          
        }  
    }
    
    public void menuAjusteEnergia1(byte[] array) { //Menu 11
        //No responde a ninguna tecla, obligatorio esperar
    }
       
    public void menuAjusteEnergia2(byte[] array) { //Menu 12
    if (array[0] == 27) { //Si se pulsa ESCAPE durante la cuenta atrás del ajuste de energia
            ajusteEnergia.setAjustando(false); //para detener el hilo que actualiza cada segundo
            taDatos.appendText("Recibido ESCAPE. Deteniendo el ajuste de energia" + "\n");
            menuEnsayo3(calibrado.getNombre());  //Volvemos al menu para insertar la muestra e iniciar su analisis          
        }  
    }    
    
    public void menuMidiendo(byte[] array) { //Menu 13
        if (array[0] == 27) { //Si se pulsa ESCAPE durante la medida
            medida.setMidiendo(false); //para detener el hilo que actualiza la medida cada segundo
            taDatos.appendText("Recibido ESCAPE. Deteniendo la medición" + "\n");
            menuEnsayo3(calibrado.getNombre());  //Volvemos al menu para insertar la muestra e iniciar su analisis          
        }        
    }   
       
    public void menuResultado(byte[] array) { //Menu 14
        if (array[0] == 0x0d) { //Si se pulsa ENTER en la pantalla de resultado, una vez terminado el análisis
            taDatos.appendText("Recibido ENTER. Volviendo al menu de ensayo" + "\n");
            menuEnsayo1(calibrado.getNombre());  //Volvemos al menu de ensayo de esa calibración          
        }        
    } 
    
    
    // CALIBRADO
    
    public void menuPassword1(byte[] array) { //Menu 15
        if (array[0] == 0x0d) { //Si se pulsa ENTER
            taDatos.appendText("Recibido ENTER. Accediendo menu OTRAS FUNCIONES" + "\n");
            menuOtrasFunciones();
        }   
        if (array[0] == 27) { //Si se pulsa ESCAPE 
            menuPrincipal();
        }
    } 
     
    public void menuOtrasFunciones(byte[] array) { //Menu 16
        if (array[0] == 50) { //2
            taDatos.appendText("Recibido 2. Accediendo al petición PSW" + "\n");
            menuPassword2();
        }   
        if (array[0] == 52) { //4
            taDatos.appendText("Recibido 4. Volviendo al menú principal" + "\n");
            menuPrincipal();
        }
    } 
    
    public void menuPassword2(byte[] array) { //Menu 17
        if (array[0] == 0x0d) { //Si se pulsa ENTER
            taDatos.appendText("Recibido ENTER. Accediendo menu Calibracion" + "\n");
            menuCalibracion1();
        }   
        if (array[0] == 27) { //Si se pulsa ESCAPE 
            menuOtrasFunciones();
        }
    } 
    
    public void menuCalibracion1(byte[] array) { //Menu 18
        if (array[0] == 50) { //2
            taDatos.appendText("Recibido 2. Accediendo al Calibracion existe" + "\n");
            menuCalibracion2();
        }  
    } 
    
    public void menuCalibracion2(byte[] array) { //Menu 19
        if (array[0] == 50) { //2
            taDatos.appendText("Recibido 2. Accediendo a calibrado AZUFRE BAJO" + "\n");
            calibrado = Main.getCalibrado("AZUFRE BAJO");
            menuModificarCalibracion1();
        }  
        if (array[0] == 52) { //4
            taDatos.appendText("Recibido 4. Cambiando a página 2 de calibrados" + "\n");
            menuCalibracion3();
        }  
    } 
    
    public void menuCalibracion3(byte[] array) { //Menu 20
        if (array[0] == 50) { //2
            taDatos.appendText("Recibido 2. Accediendo a calibrado AZUFRE MEDIO" + "\n");
            calibrado = Main.getCalibrado("AZUFRE MEDIO");
            menuModificarCalibracion1();
        }  
        if (array[0] == 52) { //4
            taDatos.appendText("Recibido 4. Cambiando a página 3 de calibrados" + "\n");
            menuCalibracion4();
        }  
    } 
    
    public void menuCalibracion4(byte[] array) { //Menu 21
        if (array[0] == 50) { //2
            taDatos.appendText("Recibido 2. Accediendo a calibrado AZUFRE ALTO" + "\n");
            calibrado = Main.getCalibrado("AZUFRE ALTO");
            menuModificarCalibracion1();
        }  
        if (array[0] == 52) { //4
            taDatos.appendText("Recibido 4. Cambiando a página 4 de calibrados" + "\n");
            menuCalibracion5();
        }  
    } 
    
    public void menuCalibracion5(byte[] array) { //Menu 22
        if (array[0] == 0x0d) { //Si se pulsa ENTER
            taDatos.appendText("Recibido ENTER. Volviendo a menú OTRAS FUNCIONES" + "\n");
            menuOtrasFunciones();
        }          
    } 
    
    
    public void menuModificarCalibracion1(byte[] array) { //Menu 23
        if (array[0] == 49) { //Si se pulsa 1
            taDatos.appendText("Recibido 1. Accediendo al menú REVISION" + "\n");
            menuModificarCalibracion2();
        }          
        if (array[0] == 55) { //Si se pulsa 7
            taDatos.appendText("Recibido 7. Volviendo al menú OTRAS FUNCIONES" + "\n");
            menuOtrasFunciones();
        }          
    } 
    
    public void menuModificarCalibracion2(byte[] array) { //Menu 24
        if (array[0] == 0x0d) { //Si se pulsa ENTER
            taDatos.appendText("Recibido ENT. Mantener nombre y acceder a config Calibrado" + "\n");
            menuModificarCalibracion3();
        }                         
    } 
    
    public void menuModificarCalibracion3(byte[] array) { //Menu 25
        if (array[0] == 51) { //Si se pulsa 3
            taDatos.appendText("Recibido 3. Continuar ajustando Calibrado" + "\n");
            menuModificarCalibracion4();
        }                         
    } 
    
    public void menuModificarCalibracion4(byte[] array) { //Menu 26
        if (array[0] == 50) { //Si se pulsa 2
            taDatos.appendText("Recibido 2. Accediendo a REGRESION" + "\n");
            menuModificarRegresion1();
        }                         
        if (array[0] == 52) { //Si se pulsa 4
            taDatos.appendText("Recibido 4. Continuar ajustando Calibrado" + "\n");
            menuModificarCalibracion5();
        }                         
    } 
    
    public void menuModificarCalibracion5(byte[] array) { //Menu 27        
        if (array[0] == 52) { //Si se pulsa 4
            taDatos.appendText("Recibido 4. Continuar ajustando Calibrado" + "\n");
            menuModificarCalibracion6();
        }                         
    } 
    
    public void menuModificarCalibracion6(byte[] array) { //Menu 28        
        if (array[0] == 49 || array[0] == 50 || array[0] == 51 || array[0] == 0x0d) { //Si se pulsa 1, 2, 3 o ENT vamos a menú 29
            taDatos.appendText("Ajustada Impresión. Continuar ajustando Calibrado" + "\n");
            menuModificarCalibracion7();
        }                         
    } 
    
    public void menuModificarCalibracion7(byte[] array) { //Menu 29        
        if (array[0] == 52) { //Si se 4 vamos a menú 30
            taDatos.appendText("Ajustada Impresión. Continuar ajustando Calibrado" + "\n");
            menuModificarCalibracion8();
        }                         
    } 
    
    public void menuModificarCalibracion8(byte[] array) { //Menu 30        
        if (array[0] == 121) { //Si se pulsa YES vamos al menú 23
            taDatos.appendText("Guardados cambios del calibrado. Volviendo a menu calibracion" + "\n");
            menuModificarCalibracion1();
        }                         
    } 

    public void menuModificarRegresion1(byte[] array) { //Menu 31        
        if (array[0] == 53) { //Si se pulsa 5 vamos al menú 32
            taDatos.appendText("Recibido 5: Accedemos a los coeficientes" + "\n");
            menuModificarRegresion2();
        }                         
        if (array[0] == 54) { //Si se pulsa 6 vamos al menú 26
            taDatos.appendText("Recibido 6: Volvemos al menu anterior" + "\n");
            menuModificarCalibracion4();
        }                         
    }
    
    // ENT mantiene el coef. O se recibe nuevo coef. lineal + ENT, sustituyendo al anterior. 
    public void menuModificarRegresion2(byte[] array) { //Menu 32                
        String nuevoCoef = "";        
        if (array[0] == 0x0d) { //Si se pulsa ENT vamos al menú 33
            taDatos.appendText("Recibido ENT: cambiamos a coef lineal" + "\n");
            menuModificarRegresion3();
        }                         
        else{
            for (byte b:array){
                nuevoCoef += (char)b;
            }
            calibrado.setTermInd(Double.valueOf(nuevoCoef)); // Actualizamos el coeficiente en nuestro objeto calibrado            
            menuModificarRegresion2(); //Actualizamos la pantalla del equipo con el nuevo valor del coeficiente
        }
    }
    
    // ENT mantiene el coef. O se recibe nuevo coef. lineal + ENT, sustituyendo al anterior. 
    public void menuModificarRegresion3(byte[] array) { //Menu 33                
        String nuevoCoef = "";        
        if (array[0] == 0x0d) { //Si se pulsa ENT vamos al menú 34
            taDatos.appendText("Recibido ENT: cambiamos a coef cuadratico" + "\n");
            menuModificarRegresion4();
        }                         
        else{
            for (byte b:array){
                nuevoCoef += (char)b;
            }
            calibrado.setCoefLin(Double.valueOf(nuevoCoef)); // Actualizamos el coeficiente en nuestro objeto calibrado            
            menuModificarRegresion3(); //Actualizamos la pantalla del equipo con el nuevo valor del coeficiente
        }
    }
    
    // ENT mantiene el coef. O se recibe nuevo coef. lineal + ENT, sustituyendo al anterior. 
    public void menuModificarRegresion4(byte[] array) { //Menu 34                
        String nuevoCoef = "";        
        if (array[0] == 0x0d) { //Si se pulsa ENT vamos al menú 31
            taDatos.appendText("Recibido ENT: volvemos al menú anterior" + "\n");
            menuModificarRegresion1();
        }                         
        else{
            for (byte b:array){
                nuevoCoef += (char)b;
            }
            calibrado.setCoefCuad(Double.valueOf(nuevoCoef)); // Actualizamos el coeficiente en nuestro objeto calibrado            
            menuModificarRegresion4(); //Actualizamos la pantalla del equipo con el nuevo valor del coeficiente
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
        medida = new Medida(id, calibrado); //10 segundos para pruebas
        medida.start();
    }    
}
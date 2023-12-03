package com.pasegados.emulador;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Esta clase genera el puerto de comunicaciones con todos sus atributos y métodos para enviar y recibir datos
 * @author Pedro Antonio Segado Solano
 */
public class Puerto {

    private final VistaController CONTROLADOR = Main.getControlador(); // Acceso al controlador de la vista
    private static SerialPort[] puertosSistema = SerialPort.getCommPorts(); //Puertos COM del sistema operativo
    private SerialPort puertoActivo;
    private String nombrePuerto;
    private int bps;
    private int bdd;
    private String paridad;
    private String bdp;    

    public Puerto() {
    }

    public Puerto(String nombrePuerto, int bps, int bdd, String paridad, String bdp) {
        this.nombrePuerto = nombrePuerto;
        this.bps = bps;
        this.bdd = bdd;
        this.paridad = paridad;
        this.bdp = bdp;
    }

    //GETTERS Y SETTER
    
    public static SerialPort[] getPuertosSistema() {
        return puertosSistema;
    }

    public static void setPuertosSistema(SerialPort[] puertosSistema) {
        Puerto.puertosSistema = puertosSistema;
    }

    public SerialPort getPuertoActivo() {
        return puertoActivo;
    }

    public void setPuertoActivo(SerialPort puertoActivo) {
        this.puertoActivo = puertoActivo;
    }

    public String getNombrePuerto() {
        return nombrePuerto;
    }

    public void setNombrePuerto(String nombrePuerto) {
        this.nombrePuerto = nombrePuerto;
    }

    public int getBps() {
        return bps;
    }

    public void setBps(int bps) {
        this.bps = bps;
    }

    public int getBdd() {
        return bdd;
    }

    public void setBdd(int bdd) {
        this.bdd = bdd;
    }

    public String getParidad() {
        return paridad;
    }

    public void setParidad(String paridad) {
        this.paridad = paridad;
    }

    public String getBdp() {
        return bdp;
    }

    public void setBdp(String bdp) {
        this.bdp = bdp;
    }

    //OTROS METODDOS
    public int buscaNumeroPuerto(String puerto) {

        int numPuerto = 0; //almacenará la posición numérica del puerto con el nombre indicado

        for (int i = 0; i < puertosSistema.length; i++) {
            if (puertosSistema[i].getSystemPortName().equals(puerto)) {
                numPuerto = i; //posición numerica del puerto dentro del array de puertos del sistema operativo
                break;
            }
        }
        return numPuerto;
    }

    public boolean activaPuerto() {

        if (nombrePuerto == null) { //Si no se ha seleccionado un puerto
            System.out.println("No se ha seleccionado ningún puerto");
            return false;
        }

        System.out.println("Intentado activar el puerto " + nombrePuerto);

        //Busco el número de puerto en el sistema que corresponde a ese nombre de puerto        
        try {
            puertoActivo = puertosSistema[buscaNumeroPuerto(nombrePuerto)];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false; //Si hay configurado un puerto pero el pc no tiene ningun puerto serie            
        }

        //Ajusto los valores del puerto a los indicados
        puertoActivo.setBaudRate(bps);
        puertoActivo.setNumDataBits(bdd);

        switch (paridad) {
            case "Ninguna" ->
                puertoActivo.setParity(SerialPort.NO_PARITY);
            case "Par" ->
                puertoActivo.setParity(SerialPort.EVEN_PARITY);
            case "Impar" ->
                puertoActivo.setParity(SerialPort.ODD_PARITY);
            case "Marca" ->
                puertoActivo.setParity(SerialPort.MARK_PARITY);
            case "Espacio" ->
                puertoActivo.setParity(SerialPort.SPACE_PARITY);
            default -> {
            }
        }

        switch (bdp) {
            case "1" ->
                puertoActivo.setNumStopBits(SerialPort.ONE_STOP_BIT);
            case "1.5" ->
                puertoActivo.setNumStopBits(SerialPort.ONE_POINT_FIVE_STOP_BITS);
            case "2" ->
                puertoActivo.setNumStopBits(SerialPort.TWO_STOP_BITS);
            default -> {
            }
        }

        //Ajusto de tiempos de lectura del puerto (TIMEOUT_Mode, READ_TIMEOUT_milliSec, WRITE_TIMEOUT_milliSec)
        puertoActivo.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        puertoActivo.flushIOBuffers();

        return puertoActivo.openPort(); //Devuelve true si abre el puerto correctamente
    }

    //Cierra conexion del puerto serie
    public void cierraConexion() {  
                      
        puertoActivo.removeDataListener(); //Eliminamos el listener
        puertoActivo.flushIOBuffers(); //Vaciamos buffer
        puertoActivo.closePort(); //Cerramos conexión del puerto
        System.out.println("Puerto cerrado. Lectura finalizada");
    }

    //ESCUCHA LO QUE LE MANDA EL SOFTWARE
    public void escucharPuerto() {
        
        puertoActivo.flushIOBuffers(); //Limpiamos el buffer de posibles datos anteriores

        puertoActivo.addDataListener(new SerialPortDataListener() { //Añadimos escuchador al puerto

            String cadena = ""; //Almacena la cadena recibida por el puerto de comunicaciones

            @Override
            public void serialEvent(SerialPortEvent event) {

                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                event.getSerialPort().readBytes(buffer, size);
                              
                CONTROLADOR.recibeDatos(buffer);
                
                puertoActivo.flushIOBuffers(); //eliminamos buffer                
            }

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
        });
    }
    
    //ENVIO DE LAS CUENTAS AL SOFTWARE
    public void enviarCuentas(String cadena){        
        byte[] sendData;
        sendData = new byte[cadena.length()];

        for (int i = 0; i < cadena.length(); i++) {
            sendData[i] = (byte) cadena.charAt(i);            
        }

        puertoActivo.writeBytes(sendData, cadena.length());
        esperar(300);
        //puertoActivo.flushIOBuffers();
    }
    
    //Esperar x milisegundos
    public void esperar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

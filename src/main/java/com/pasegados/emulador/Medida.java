package com.pasegados.emulador;

import java.util.Random;

/** 
 * Esta clase nos permite simular la medida de una muestra en el equipo OXFORD, realizando una cuenta atrás
 * sobre el textArea del controlador, actualizando los segundos que quedan del análisis de manera visual,
 * ya que desde la misma clase solo se mostraría la última actualización al finalizar el correspondiente bucle.
 * 
 * @author Pedro Antonio Segado Solano
 */
public class Medida extends Thread {

    private final VistaController CONTROLADOR = Main.getControlador(); //Acceso al controlador de la vista
    private final String IDENTIFICACION; // Identificación de la muestra a analizare
    private final Calibrado CALIBRADO; // Calibrado por el que se analiza la muestra
    private final int DURACION; // Duración en segundos del ensayo en ese calibrado
    private boolean midiendo; // Para detener la medición si pasa a "false" al cancelar el análisis el usuario
    private int cps; // Cuentas por segundo detectadas por el equipo
    private double resultado; // Concentración de azufre correspondiente a las cps detectadas, en función de la
                             // ecuación del calibrado

    public Medida(String identificacion, Calibrado calibrado) {
        this.IDENTIFICACION = identificacion;
        this.CALIBRADO = calibrado;
        this.DURACION = calibrado.getDuración();
        this.midiendo = true;
        this.cps = 0;
        this.resultado = 0;
    }

    @Override
    public void run() {
           
        Random cuentas = new Random(); // Para crear un aleatorio de cuentas, dentro del rango del calibrado correspondiente
        cps=0;
        
        if (CALIBRADO.getNombre().equals("AZUFRE BAJO")) {
            cps = 2800 + cuentas.nextInt(1100);  //rango 2800 - 3900
        }
        else if (CALIBRADO.getNombre().equals("AZUFRE MEDIO")) {
            cps = 4370 + cuentas.nextInt(10410);  //rango 4370 - 14780
        }
        else if (CALIBRADO.getNombre().equals("AZUFRE ALTO")) {
            cps = 10740 + cuentas.nextInt(23700);  //rango 10740 - 34440
        }
                
        // Una vez generado un numero de cuentas aleatorio, simulamos la medida durante los segundos indicados en
        // DURACION, haciendo oscilar ligueramente la lectura del numero de cuentas como suele pasar en la realidad
        
        for (int i = DURACION; i >= 0 & midiendo; i--) { //mientras no llegue a 0 segundos y midiendo sea true
            cps = (cps - 10) + cuentas.nextInt(20);            
            
            resultado = (CALIBRADO.getCoefCuad() * (float)Math.pow((double)cps, 2)) + (CALIBRADO.getCoefLin() * cps) + CALIBRADO.getTermInd();            
                        
            //Redondeamos resultado a 4 decimales            
            resultado = Math.round(resultado * 10000.0f) / 10000.0f;
            
            //Mostramos en pantalla del OXFORD el resultado momentaneo
            CONTROLADOR.menuMidiendo(IDENTIFICACION, resultado, CALIBRADO.getNombre(), i);
            try {
                sleep(1000); //Actualizamos cada segundo el valor de la medida en pantalla
            } catch (InterruptedException ex) {}                
        }

        //Si el usuario no interrumpe la medida, al terminar el bucle enviamos las cuentas desde
        //el OXFORD al software
        if (midiendo) { 
            System.out.println("enviando cuentas");
            CONTROLADOR.getPuerto().enviarCuentas(" " + cps + " cps  "); //Estructura típica que envía el OXFORD
            CONTROLADOR.getPuerto().enviarCuentas("\n");
            
            //Informamos de las cuentas en el textArea
            CONTROLADOR.getTextAreaDatos().appendText("Analisis Terminado" +  "\n");
            CONTROLADOR.getTextAreaDatos().appendText("Enviadas cuentas: " + cps +  "\n");
            
            //Cambiamos a pantalla de resultado en el equipo
            CONTROLADOR.menuResultado(resultado, CALIBRADO.getNombre());
        }
    }
        
    public void setMidiendo(boolean estado) {
        midiendo = estado;
    }

    public boolean getMidiendo() {
        return midiendo;
    }
}

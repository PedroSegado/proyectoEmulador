package com.pasegados.emulador;

/**
 * Esta clase nos permite simular el ajuste de energia del equipo OXFORD,
 * realizando una cuenta atrás sobre el textArea del controlador, actualizando
 * los segundos de manera visual, ya que desde la misma clase solo se mostraría
 * la última actualización al finalizar el correspondiente bucle.
 *
 * @author Pedro Antonio Segado Solano
 */
public class Energia extends Thread {

    private final VistaController CONTROLADOR = Main.getControlador(); // Acceso al controlador de la vista
    private final int VALOR; // Valor inicial en segundos de la cuenta atrás
    private boolean ajustando; // Para control del proceso, por si queremos detenerlo pulsando ESCAPE

    public Energia(int valor) {
        this.VALOR = valor;
        ajustando = true;
    }

    @Override
    public void run() {
        //Primera pantalla de ajuste de energia durante 3 segundos
        CONTROLADOR.menuAjusteEnergia1();
        try {
            sleep(1000);
        } catch (InterruptedException ex) {
        }

        //Segunda pantalla del ajuste de energia donde se hace la cuenta atras de los 
        //segundos pasados a la variable "valor"        
        for (int i = VALOR; i >= 0 & ajustando; i--) {            
            CONTROLADOR.menuAjusteEnergia2(i);
            try {
                sleep(1000);
            } catch (InterruptedException ex) {}                        
        }
        
        if(ajustando){ //Si se completa el juste, se inicia la medida de la muestra
            CONTROLADOR.iniciaMedida(); 
        }
    }

    public void setAjustando(boolean estado) {
        ajustando = estado;
    }

    public boolean getAjustando() {
        return ajustando;
    }
}

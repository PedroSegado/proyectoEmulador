
package com.pasegados.emulador;

/**
 * Esta clase nos permite simular el acondicionamiento del equipo OXFORD,
 * realizando una cuenta atrás sobre el textArea del controlador, actualizando
 * los segundos de manera visual, ya que desde la misma clase solo se mostraría
 * la última actualiación al finalizar el correpondiente bucle.
 *
 * @author Pedro Antonio Segado Solano
 */
public class Condicionamiento extends Thread {

    private final VistaController CONTROLADOR = Main.getControlador(); // Acceso al controlador de la vista
    private final int VALOR; // Valor inicial en segundos de la cuenta atrás
    private boolean acondicionando; // Para detener el acondicionamiento si pasa a "false" 
                                    // al cancelar el usuario pulsando ESCAPE

    public Condicionamiento(int valor) {
        this.VALOR = valor;
        acondicionando = true;
    }

    @Override
    public void run() {
        // Primera pantalla de acondicionamiento durante 13 segundos, no responde a ESC del usuario,
        // Se realiza siempre.
        CONTROLADOR.menuCondicionando1();
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
        }

        // Segunda pantalla de acondicionamiento donde se hace la cuenta atras de los segundos pasados
        // a la variable "valor". Se puede parar pulsando ESC.
        for (int i = VALOR; i >= 0 & acondicionando; i--) {            
            CONTROLADOR.menuCondicionando2(i);
            try {
                sleep(1000); //Actualiza el menuCondicionamiento() cada segundo
            } catch (InterruptedException ex) {}   
        }
        
        if(acondicionando){ //Si se completa el acondicionamiento, se inicia el ajuste de energia
            CONTROLADOR.iniciaAjusteEnergia(); 
        }
    }

    public void setAcondicionando(boolean estado) {
        acondicionando = estado;
    }

    public boolean getAcondicionando() {
        return acondicionando;
    }
}
module com.pasegados.emulador {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;

    opens com.pasegados.emulador;
    exports com.pasegados.emulador;
    
}

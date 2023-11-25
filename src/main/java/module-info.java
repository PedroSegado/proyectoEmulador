module com.pasegados.emulador {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.pasegados.emulador to javafx.fxml;
    exports com.pasegados.emulador;
    requires com.fazecast.jSerialComm;
}

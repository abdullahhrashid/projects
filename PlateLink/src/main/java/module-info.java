module com.example.platelink {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires opencv;
    requires tess4j;
    requires java.desktop;

    opens com.example.platelink to javafx.fxml;
    exports com.example.platelink;
}
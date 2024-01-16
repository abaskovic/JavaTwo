module hr.algebra.javatwo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;

    exports hr.algebra.javatwo.chat.service to java.rmi;

    opens hr.algebra.javatwo to javafx.fxml;
    exports hr.algebra.javatwo;
    exports hr.algebra.javatwo.utils;
    opens hr.algebra.javatwo.utils to javafx.fxml;
    exports hr.algebra.javatwo.model;
    opens hr.algebra.javatwo.model to javafx.fxml;
    exports hr.algebra.javatwo.controller;
    opens hr.algebra.javatwo.controller to javafx.fxml;



}
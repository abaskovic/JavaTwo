module hr.algebra.javatwo {
    requires javafx.controls;
    requires javafx.fxml;


    opens hr.algebra.javatwo to javafx.fxml;
    exports hr.algebra.javatwo;
}
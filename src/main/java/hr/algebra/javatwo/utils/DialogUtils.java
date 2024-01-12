package hr.algebra.javatwo.utils;

import javafx.scene.control.Alert;

public class DialogUtils {

    public static void showDialog(Alert.AlertType alertType,
                                  String title, String message)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

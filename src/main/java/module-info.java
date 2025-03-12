module org.example.module_3_csc311 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.scripting;


    opens org.example.module_3_csc311 to javafx.fxml;
    exports org.example.module_3_csc311;
}
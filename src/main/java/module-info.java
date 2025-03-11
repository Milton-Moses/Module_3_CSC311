module org.example.module_3_csc311 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.module_3_csc311 to javafx.fxml;
    exports org.example.module_3_csc311;
}
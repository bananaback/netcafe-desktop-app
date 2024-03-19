module dev.hideftbanana.netcafejavafxapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens dev.hideftbanana.netcafejavafxapp to javafx.fxml;
    exports dev.hideftbanana.netcafejavafxapp;
}

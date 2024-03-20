module dev.hideftbanana.netcafejavafxapp {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens dev.hideftbanana.netcafejavafxapp to javafx.fxml;

    exports dev.hideftbanana.netcafejavafxapp;
}

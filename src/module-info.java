module SAAD.CriticalPath {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    // Add other JavaFX modules as needed

    exports ProjectManager.ui; // Allow javafx.graphics to access this package
    // If you have other packages to export, add them similarly
}

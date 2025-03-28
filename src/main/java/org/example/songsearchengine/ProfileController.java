package org.example.songsearchengine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController extends SceneController {

    @FXML
    private Label ShowUsername;
    @FXML
    private Label ShowPassword;
    @FXML
    private Button ResetUsername;
    @FXML
    private Button ResetPassword;
    @FXML
    private VBox ChangeUser;

    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField confirmUsernameField;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;

    @FXML private VBox ChangePassword;
    @FXML private TextField checkUsername;
    @FXML private TextField NewPassword;
    @FXML private PasswordField ConfirmNewPassword;
    @FXML private Button PasswordconfirmButton;
    @FXML private Button PasswordcancelButton;

    @FXML private Button LogOut;


    private String currentUsername;

    @FXML
    public void initialize() {
        super.initialize();
        currentUsername = UserSession.getCurrentUser(); // Get logged-in user
        ShowUsername.setText(currentUsername);
        ShowPassword.setText("******"); // Hide password for security
        ChangeUser.setVisible(false); // Hide change username box initially
    }

    @FXML
    private void showResetUsernameBox() {
        ChangeUser.setVisible(true);
    }

    @FXML
    private void showResetPasswordeBox() {
        ChangePassword.setVisible(true);
    }


    @FXML
    private void closeResetUsernameBox() {
        ChangeUser.setVisible(false);
    }

    @FXML
    private void closeResetPasswordBox() {
        ChangePassword.setVisible(false);
    }

    @FXML
    private void LogOut(ActionEvent event) {
        LogInController.isSignUpShown = false;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainLogin.fxml"));
            Parent root = fxmlLoader.load(); // Ensure the FXML is reloaded
            Scene scene = new Scene(root);

            // Get current stage and set new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUsernameChange() {
        String newUsername = newUsernameField.getText();
        String confirmUsername = confirmUsernameField.getText();

        if (newUsername.isEmpty() || confirmUsername.isEmpty()) {
            showAlert("Error", "Fields cannot be empty!");
            return;
        }

        if (!newUsername.equals(confirmUsername)) {
            showAlert("Error", "Usernames do not match!");
            return;
        }

        if (Backend.updateUsername(currentUsername, newUsername)) {
            UserSession.setCurrentUser(newUsername);
            ShowUsername.setText(newUsername);
            showAlert("Success", "Username updated successfully!");
            closeResetUsernameBox();
        } else {
            showAlert("Error", "Failed to update username.");
        }
    }

    @FXML
    private void handlePasswordChange() {
        String enteredUsername = checkUsername.getText();
        String newPassword = NewPassword.getText();
        String confirmPassword = ConfirmNewPassword.getText();

        if (enteredUsername.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Fields cannot be empty!");
            return;
        }

        if (!enteredUsername.equals(currentUsername)) {
            showAlert("Error", "Incorrect username. Please enter your correct username.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match!");
            return;
        }

        if (Backend.updatePassword(enteredUsername, newPassword)) {
            showAlert("Success", "Password updated successfully!");
            closeResetPasswordBox();
        } else {
            showAlert("Error", "Failed to update password.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

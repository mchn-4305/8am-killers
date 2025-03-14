package org.example.songsearchengine;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.animation.TranslateTransition;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.TextField;

public class LogInController{
    @FXML
    private VBox vbox;
    @FXML
    private Parent fxml;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField signupusername;
    @FXML
    private TextField signuppassword;
    @FXML
    private PasswordField confirmpassword;

    private static boolean isSignUpShown = false;
    public void initialize(){
        if (!isSignUpShown) {
            TranslateTransition t = new TranslateTransition(Duration.seconds(1), vbox);
            t.play();
            t.setOnFinished((e) -> {
                try {
                    fxml = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
                    vbox.getChildren().clear();
                    vbox.getChildren().setAll(fxml);
                    isSignUpShown = true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
    @FXML
    private void open_signin(ActionEvent event){
        TranslateTransition t = new TranslateTransition(Duration.seconds(1),vbox);
        t.setToX(vbox.getLayoutX()*36);
        t.play();
        t.setOnFinished((e)-> {
            try{
                fxml = FXMLLoader.load(getClass().getResource("SignIn.fxml"));
                vbox.getChildren().clear();
                vbox.getChildren().setAll(fxml);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void open_signup(ActionEvent event){
        TranslateTransition t = new TranslateTransition(Duration.seconds(1),vbox);
        t.setToX(0);
        t.play();
        t.setOnFinished((e)-> {
            try{
                fxml = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
                vbox.getChildren().clear();
                vbox.getChildren().setAll(fxml);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void login(ActionEvent event) {
        String user = username.getText();
        String pw = password.getText();

        if (user.isEmpty() || pw.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Username and password cannot be empty.");
            return;
        }

        if (Backend.verifyLogin(user, pw)) {
            UserSession.setCurrentUser(user);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Home.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load());

                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
        }
    }


    @FXML
    private void signup(ActionEvent event) {
        String user = signupusername.getText();
        String pw = signuppassword.getText();
        String confirmPw = confirmpassword.getText(); // Get the confirmation password

        // check if any field is empty
        if (user.isEmpty() || pw.isEmpty() || confirmPw.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Signup Error", "All fields must be filled.");
            return;
        }

        // check if passwords match
        if (!pw.equals(confirmPw)) {
            showAlert(Alert.AlertType.ERROR, "Signup Error", "Passwords do not match.");
            return;
        }

        // check if the username already exists in the database
        if (Backend.doesUserExist(user)) {
            showAlert(Alert.AlertType.ERROR, "Signup Failed", "Username already exists. Please choose another.");
            return;
        }

        // insert new user if username is unique and passwords match
        if (Backend.insertUser(user, pw)) {
            UserSession.setCurrentUser(user);
            showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Account created successfully!");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Home.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load());

                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Signup Failed", "An error occurred while creating the account. Please try again.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

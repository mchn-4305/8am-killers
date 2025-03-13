package org.example.songsearchengine;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
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
        t.setToX(vbox.getLayoutX()*37);
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
    private void login(ActionEvent event){
        String user = username.getText();
        String pw = password.getText();

        if (user.isEmpty() || pw.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        if(Backend.verifyLogin(user,pw)){
            UserSession.setCurrentUser(user);
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Home.fxml"));
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load());

                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();

            } catch (Exception e){
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Wrong Login");
        }
    }

    @FXML
    private void signup(ActionEvent event) {
        String user = signupusername.getText();
        String pw = signuppassword.getText();

        if (user.isEmpty() || pw.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        if (Backend.insertUser(user, pw)) {
            UserSession.setCurrentUser(user);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Home.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load());

                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Signup failed. Username may already exist.");
        }
    }

}

package com.example.tj_monopoly;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StartController implements Initializable {
    @FXML
    Button newGame_btn, loadGame_btn, options_btn, quitGame_btn;
    @FXML
    AnchorPane crawl;
    @FXML
    ImageView background_img;

    Audio themeAudio = new Audio();
    Audio traverseMenuAudio = new Audio();
    Audio switchSceneAudio = new Audio();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        newGame_btn.setOnAction(actionEvent -> {
            try {

                switchSceneAudio.setFile(1);
                switchSceneAudio.play();

                themeAudio.stop();

                DBUtils.newGame(actionEvent);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        addFocusedListenerToMenuItems();

        playStartingTheme();
        playStartingAnimation();
    }

    public void addFocusedListenerToMenuItems(){
        addFocusedListenerToNode(newGame_btn);
        addFocusedListenerToNode(loadGame_btn);
        addFocusedListenerToNode(options_btn);
        addFocusedListenerToNode(quitGame_btn);
    }

    public void addFocusedListenerToNode(Button _btn){
        _btn.focusedProperty().addListener((ov, old_val, new_val) -> {
            if(new_val){
                playMenuSound();

                if(_btn.equals(newGame_btn)){
                    background_img.setFocusTraversable(false);
                }
            }
        });
    }

    public void playStartingTheme(){
        themeAudio.setFile(3);
        themeAudio.play();
        themeAudio.loop();
    }

    public void playStartingAnimation(){

        TranslateTransition transition = new TranslateTransition(Duration.seconds(9), crawl);
        transition.setByY(350);
        transition.play();
    }
    public void playMenuSound(){
        traverseMenuAudio.setFile(0);
        traverseMenuAudio.play();
    }

    public void exit(){
        System.exit(0);
    }
}
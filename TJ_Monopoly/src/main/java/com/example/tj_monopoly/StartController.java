package com.example.tj_monopoly;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        newGame_btn.setOnAction(actionEvent -> {
            try {

                traverseMenuAudio.setFile(1);
                traverseMenuAudio.play();

                themeAudio.stop();

                DBUtils.newGame(actionEvent);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        newGame_btn.focusedProperty().addListener((ov, old_val, new_val) -> {
            if (new_val) {
                playMenuSound();
                background_img.setFocusTraversable(false);
            }
        });

        loadGame_btn.focusedProperty().addListener((ov, old_val, new_val) -> {
            if (new_val) {
                playMenuSound();
            }
        });

        options_btn.focusedProperty().addListener((ov, old_val, new_val) -> {
            if(new_val){
                playMenuSound();
            }
        });

        quitGame_btn.focusedProperty().addListener((ov, old_val, new_val) -> {
            if(new_val){
                playMenuSound();
            }
        });

        themeAudio.setFile(3);
        themeAudio.play();
        themeAudio.loop();
        playStartingAnimation();
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
package com.example.tj_monopoly;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBUtils {

    //region Helper Methods
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/monopoly_tj", "root", "password");
    }
    public static void showAlert(String _content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(DBUtils.class.getResource("alert.css").toExternalForm());
        alert.setContentText(_content);
        alert.setHeaderText("User input error D:");
        alert.show();
    }
    //endregion

    public static void changeScene(ActionEvent _event, String _fxmlFile, String _title) throws IOException {

        //Load the scene into the root
        FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(_fxmlFile));
        Parent root = loader.load();

        //Create the stage that holds the scene
        Stage stage = (Stage)((Node)_event.getSource()).getScene().getWindow();
        stage.setTitle(_title);
        stage.setScene(new Scene(root, 1280, 725));
        stage.getIcons().add(new Image("rexLogo.png"));
        stage.setResizable(false);
        stage.centerOnScreen();

        if(_fxmlFile.equals("monopoly.fxml")){
            //stage.setMaximized(true);

            MonopolyController movieTicketController = loader.getController();
            movieTicketController.setUpPlayers();
            movieTicketController.setUpSpaces();
            movieTicketController.setUpTiles();
            //movieTicketController.passObjectsToScene(stage, _admin, _movieList, _transactionList);
        }

        stage.show();
    }

    public static void newGame(ActionEvent event) throws IOException, SQLException {
//        Connection conn = getConnection();
//        PreparedStatement psInsertInto;
//
//        String _email = "hello@gmail.com";
//
//        psInsertInto = conn.prepareStatement("INSERT INTO test (testing) VALUES (?)");
//        psInsertInto.setString(1, _email);
//        psInsertInto.executeUpdate();

        DBUtils.changeScene(event, "monopoly.fxml", "Star Wars Monopoly");

//        psInsertInto.close();
//        conn.close();
    }
}

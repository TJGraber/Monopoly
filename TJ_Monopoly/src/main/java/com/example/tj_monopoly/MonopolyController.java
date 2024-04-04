package com.example.tj_monopoly;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MonopolyController implements Initializable {

    //region FXML Compontents
    @FXML
    Button firstDie_btn, secondDie_btn, openCard_btn, hamburger_btn;
    @FXML
    ImageView firstDie_img, secondDie_img, playerPiece1, tilePreview_img, hamburgerIcon_img;
    @FXML
    AnchorPane space0, space1, space2, space3, space4, space5, space6, space7, space8, space9, space10, space11,
            space12, space13, space14, space15, space16, space17, space18, space19, space20, space21, space22, space23,
            space24, space25, space26, space27, space28, space29, space30, space31, space32, space33, space34, space35,
            space36, space37, space38, space39;
    @FXML
    AnchorPane outerBoard_ap, innerBoard_ap, tilePreviewColor_ap, tilePreviewCard_ap, hamburgerMenu_ap, hamburgerBackground_ap;
    @FXML
    Label tilePreviewPrice_lbl, tilePreviewName_lbl, moneyChange_lbl;
    //endregion

    Image[] diceImages = {
            new Image("dOne.png"),
            new Image("dTwo.png"),
            new Image("dThree.png"),
            new Image("dFour.png"),
            new Image("dFive.png"),
            new Image("dSixFixed.png")
    };

    String[] tileName = {
            "Go", "Battle Droid", "Community Chest", "General Grevious", "", "Millennium Falcon", "Captain Rex", "Chance",
            "Ahsoka Tano", "Arc Trooper Fives", "Jail", "Stormtrooper", "Death Star", "Director Krennic", "Grand Inquisitor",
            "Slave 1", "Wedge Antilles", "Community Chest", "Cassian Andor", "Han Solo", "Free Parking", "Count Dooku",
            "Chance", "Darth Maul", "Kylo Ren", "Star Destroyer", "Plo Koon", "Anakin Skywalker", "Starkiller Base",
            "Mace Windu", "Go To Jail", "Master Yoda", "Luke Skywalker", "Community Chest", "Obi-Wan Kenobi",
            "AT-AT Walker", "Chance", "Emperor Palpatine", "", "Darth Vader"
    };

    Player player;
    Audio audio = new Audio();

    //region Animation Variables
    ScaleTransition die1PulseTransition;
    ScaleTransition die2PulseTransition;
    ScaleTransition pulseCardTransition;
    //endregion

    AnchorPane[] spaces;
    Tile[] tiles = new Tile[40];
    boolean isPreviewCardCentered = false;
    int startingMoney = 1500;
    final Color MONEYGREEN = Color.rgb(22, 129, 24);
    final Color NEGATIVERED = Color.rgb(255,51,51);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        audio.setFile(4);
        audio.play();
        audio.loop();

        pulseDice();
    }
    int randomIndexD1;
    int randomIndexD2;

    //region Setup
    public void setUpPlayers(){
        player = new Player(0, playerPiece1, startingMoney, false);
    }
    public void setUpSpaces(){
        spaces = new AnchorPane[]{space0, space1, space2, space3, space4, space5, space6, space7, space8, space9, space10, space11,
                space12, space13, space14, space15, space16, space17, space18, space19, space20, space21, space22, space23,
                space24, space25, space26, space27, space28, space29, space30, space31, space32, space33, space34, space35,
                space36, space37, space38, space39};

        setUpTiles();

    }
    public void setUpTiles(){

        for(int i = 0; i < spaces.length; i++){

            boolean isCorner = false;

            if(i % 10 == 0){
                isCorner = true;
            }


            Property property = null;
            ImageView imgV = null;
            Image img = null;
            boolean isChanceOrChest = false;

            if(spaces[i].getChildren().size() == 3 /* probably or 2 in the future for utilities with no color anchor*/){

                imgV = (ImageView) spaces[i].getChildren().get(2);
                img = imgV.getImage();

                Label priceLbl = (Label) spaces[i].getChildren().get(1);
                property = new Property(priceLbl.getText());
            }else{

                imgV = (ImageView) spaces[i].getChildren().get(0);
                img = imgV.getImage();

                if(img == null){
                    img = new Image("vaderIcon.png");
                }
            }

            File file = new File(img.getUrl());
            String fName = file.getName();

            if(fName.equals("empireLogo.png") || fName.equals("rebellionLogo.png")){
                isChanceOrChest = true;
            }

            tiles[i] = new Tile(tileName[i], property, spaces[i], img, isCorner, isChanceOrChest);
        }

    }
    //endregion

    //region Click on this indicator Animations
    public void pulseDice(){
        die1PulseTransition = new ScaleTransition(Duration.seconds(0.4), firstDie_btn);
        die1PulseTransition.setByX(0.05f);
        die1PulseTransition.setByY(0.05f);
        die1PulseTransition.setCycleCount(Animation.INDEFINITE);
        die1PulseTransition.setAutoReverse(true);
        die1PulseTransition.play();

        die2PulseTransition = new ScaleTransition(Duration.seconds(0.4), secondDie_btn);
        die2PulseTransition.setByX(0.05f);
        die2PulseTransition.setByY(0.05f);
        die2PulseTransition.setCycleCount(Animation.INDEFINITE);
        die2PulseTransition.setAutoReverse(true);
        die2PulseTransition.play();
    }
    public void pulseCard(){
        pulseCardTransition = new ScaleTransition(Duration.seconds(0.5), tilePreviewCard_ap);
        pulseCardTransition.setByX(0.03f);
        pulseCardTransition.setByY(0.03f);
        pulseCardTransition.setCycleCount(Animation.INDEFINITE);
        pulseCardTransition.setAutoReverse(true);
        pulseCardTransition.play();
    }
    //endregion

    //region Dice Clicked
    public void rollDice(){

        if(isPreviewCardCentered){
            movePreviewCardBack();
        }

        die1PulseTransition.stop();
        die2PulseTransition.stop();

        audio.setFile(2);
        audio.play();

        //Dice will flicker every 0.1 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event ->{

            Random random = new Random();
            randomIndexD1 = random.nextInt(6);
            randomIndexD2 = random.nextInt(6);

            firstDie_img.setImage(diceImages[randomIndexD1]);
            secondDie_img.setImage(diceImages[randomIndexD2]);
        }));

        //25 Total "bounces"
        timeline.setCycleCount(25);
        timeline.play();

        timeline.setOnFinished(event -> {
            handleBoardAfterDiceRoll(randomIndexD1 + 1 + randomIndexD2 + 1);
        });
    }
    public void handleBoardAfterDiceRoll(int numberOfSpaces){

        numberOfSpaces = 2;
        
        int tileBeforeMove = player.getCurrentTileIndex();

        //Checking to see if new tile will be going back to start of list (going to or past go)
        int newTileIndex = (tileBeforeMove + numberOfSpaces) % tiles.length;
        player.setCurrentTile(newTileIndex);

        //Dividing by 10 to get first digit, getting the board side (since it's an int if number is less than 10
        // boardSide will equal 0)
        int boardSideBeforeMoving = tileBeforeMove / 10;
        int boardSideAfterMoving = newTileIndex / 10;

        int numberOfSidesJumpedTo = getNumSidesJumped(boardSideBeforeMoving, boardSideAfterMoving);

        rotateBoard(numberOfSidesJumpedTo);

        checkForPassedGo(tileBeforeMove, newTileIndex);

        movePlayerPiece(newTileIndex, boardSideAfterMoving);

        setUpTilePreview(newTileIndex);
    }

    public int getNumSidesJumped(int _boardSideBeforeMoving, int _boardSideAfterMoving){

        //If Player goes from side 0 (bottom) to side 2 (top) then number of sides jumped = 2
        int numberOfSidesJumpedTo = Math.abs(_boardSideAfterMoving - _boardSideBeforeMoving);

        //If you are jumping from side 3 to side 0, the player has rounded go
        if(_boardSideBeforeMoving > _boardSideAfterMoving){

            if(numberOfSidesJumpedTo == 3){
                numberOfSidesJumpedTo = 1;

                //If numberOfSidesJumpedTo == 2, value is already correct

            }else if(numberOfSidesJumpedTo == 1){
                numberOfSidesJumpedTo = 3;
            }
        }

        return numberOfSidesJumpedTo;
    }

    public void rotateBoard(int _numberOfSidesJumpedTo){

        int addedRotation = _numberOfSidesJumpedTo * -90;

        if(_numberOfSidesJumpedTo > 0){
            RotateTransition outerRotation = new RotateTransition(Duration.millis(2000 * _numberOfSidesJumpedTo), outerBoard_ap);
            //Board will rotate by -90 degrees for each side jumped to, using cycle count so that animation will
            //play at the same speed no matter the number of jumps
            outerRotation.setByAngle(addedRotation);
            //rt.setCycleCount(numberOfSidesJumpedTo);
            outerRotation.play();

            RotateTransition innerRotation = new RotateTransition(Duration.millis(2000 * _numberOfSidesJumpedTo), innerBoard_ap);
            innerRotation.setByAngle(-addedRotation);
            innerRotation.play();

            RotateTransition playerRotation = new RotateTransition(Duration.millis(2000 * _numberOfSidesJumpedTo), playerPiece1);
            playerRotation.setByAngle(-addedRotation);
            playerRotation.play();
        }
    }

    public void movePlayerPiece(int _newTileIndex, int _boardSideAfterMoving){

        checkForSpecialTile(_newTileIndex);

        spaces[_newTileIndex].getChildren().add(player.getPlayerIcon());

        //Set player icon to default X and Y
        player.getPlayerIcon().setLayoutX(25);
        player.getPlayerIcon().setLayoutY(25);

        if(!tiles[_newTileIndex].isCornerTile()){

            //If new tile is not a corner tile, offset is necessary based on which side of the board
            //the new tile is on. By default, player location is set to corner offset
            if(_boardSideAfterMoving == 0 || _boardSideAfterMoving == 2){
                player.getPlayerIcon().setLayoutX(3);
            }else if(_boardSideAfterMoving == 1 || _boardSideAfterMoving == 3){
                player.getPlayerIcon().setLayoutY(3);
            }
        }
    }

    public void checkForPassedGo(int _tileBeforeMove, int _tileAfterMove){

        if(_tileBeforeMove > _tileAfterMove){
            player.addMoney(200);
        }
    }
    public void checkForSpecialTile(int _newTileIndex){

        switch (_newTileIndex){

            case 4:
                //$200 Tax
                player.subtractMoney(200);

                break;
            case 10:
                //Just Visiting

                break;
            case 20:
                //Free Parking

                break;
            case 30:
                //Go to Jail

                break;
            case 38:
                //$100 Tax
                player.subtractMoney(100);

                break;
        }
    }

    //region Tile Preview
    public void setUpTilePreview(int _newTileIndex){

        boolean isProperty = tiles[_newTileIndex].getProperty() != null;

        //region Preview Image
        Image img = tiles[_newTileIndex].getImg();
        tilePreview_img.setImage(img);
        tilePreview_img.setLayoutX(getImageOffset(img));
        //endregion

        //region Preview Color Banner & Text
        String colorStyle = ": white;";

        if(isProperty){
            colorStyle = spaces[_newTileIndex].getChildren().get(0).getStyle();

            if(colorStyle.isEmpty()){
                colorStyle = ": white;";
            }
        }

        tilePreviewColor_ap.setStyle(colorStyle);

        tilePreviewName_lbl.setText(tiles[_newTileIndex].getName());

        int delimiterIndex = colorStyle.indexOf(":");
        String isolatedColor = colorStyle.substring(delimiterIndex + 2,  colorStyle.length() - 1);

        if(isolatedColor.equals("brown") || isolatedColor.equals("blue")){
            tilePreviewName_lbl.setTextFill(Paint.valueOf("white"));
        }else{
            tilePreviewName_lbl.setTextFill(Paint.valueOf("black"));
        }


        if(isProperty){
            tilePreviewPrice_lbl.setText(tiles[_newTileIndex].getProperty().getPrice());
        }else{
            tilePreviewPrice_lbl.setText("");
        }
        //endregion

        if(tiles[_newTileIndex].isChanceOrChest){
            pulseCard();
            openCard_btn.setVisible(true);
        }

        if(tiles[_newTileIndex].isCornerTile){
            tilePreviewCard_ap.setStyle("-fx-background-color:  #c7eaca;");
        }else{
            tilePreviewCard_ap.setStyle("-fx-background-color:  white;");
        }
    }

    public int getImageOffset(Image _img){

        double aspectRatio = _img.getWidth() / _img.getHeight();
        int imageOffset = 0;

        //Very tall image -> amount of offset should decrease the wider the image
        if(aspectRatio < 0.5){
            imageOffset = 60;
        }

        //9:16 aspect ratio
        else if(aspectRatio > 0.5 && aspectRatio < 0.65){
            imageOffset = 50;
        }

        //4:5 aspect ratio
        else if(aspectRatio > 0.65 && aspectRatio < 0.9){
            imageOffset = 40;
        }

        //1:1 aspect ratio
        else if(aspectRatio > 0.9 && aspectRatio < 1.1){
            imageOffset = 20;
        }

        return imageOffset;
    }
    //endregion

    //endregion

    //region Community Chest and Chance Cards
    public void openCardAnimate(){
        pulseCardTransition.stop();

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1.5), tilePreviewCard_ap);
        translateTransition.setToX(100);
        translateTransition.setToY(80);
        translateTransition.play();

        translateTransition.setOnFinished(event -> {
            openCardShake();
        });
    }

    public void openCardShake(){

        audio.setFile(5);
        audio.play();

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), tilePreviewCard_ap);
        translateTransition.setByX(20);
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(26);
        translateTransition.play();

        translateTransition.setOnFinished(event -> {
            openCard();
        });
    }

    public void openCard(){
        tilePreview_img.setImage(new Image("jarJar.png"));
        openCard_btn.setVisible(false);

        isPreviewCardCentered = true;
    }

    public void movePreviewCardBack(){
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1.5), tilePreviewCard_ap);
        translateTransition.setToX(-5);
        translateTransition.setToY(0);
        translateTransition.play();

        isPreviewCardCentered = true;
    }
    //endregion

    //region Money Animations
    public void moneyGainedAnimation(int _moneyGained){

        moneyChange_lbl.setTextFill(MONEYGREEN);
        moneyChange_lbl.setText("+$" + _moneyGained);

        fadeAndMoveMoneyChange();
    }
    public void moneyLostAnimation(int _moneyLost){

        moneyChange_lbl.setTextFill(NEGATIVERED);
        moneyChange_lbl.setText("-$" + _moneyLost);

        fadeAndMoveMoneyChange();
    }
    public void fadeAndMoveMoneyChange(){

        FadeTransition fadeMoneyText = new FadeTransition(Duration.millis(1000), moneyChange_lbl);
        fadeMoneyText.setFromValue(0.0);
        fadeMoneyText.setToValue(1);
        fadeMoneyText.setCycleCount(2);
        fadeMoneyText.setAutoReverse(true);

        fadeMoneyText.play();

        TranslateTransition moveMoneyText = new TranslateTransition(Duration.millis(2000), moneyChange_lbl);
        moveMoneyText.setToY(0.0);
        moveMoneyText.setToY(550);
        moveMoneyText.setCycleCount(1);

        moveMoneyText.play();

        moveMoneyText.setOnFinished(event -> {
            resetMoneyChangeLabel();
        });
    }
    public void resetMoneyChangeLabel(){
        moneyChange_lbl.setTranslateY(0);
        moneyChange_lbl.setOpacity(0);
    }
    //endregion


    //region User Interface (Things on the right side of screen)
    public void burgerMenuClicked(){
        hamburgerMenu_ap.setVisible(!hamburgerMenu_ap.isVisible());
        hamburgerBackground_ap.setVisible(hamburgerMenu_ap.isVisible());

        //Try and figure out how to detect when a player clicks anywhere not on the menu
        //I might be able to do this by adding an event listener to the scene
        //When the user clicks off of the menu, it should close

//        hamburger_btn.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
//            System.out.println(mouseEvent.getPickResult().getIntersectedNode());
//        });
    }

    public void quit(){
        System.exit(0);
    }
    //endregion
}
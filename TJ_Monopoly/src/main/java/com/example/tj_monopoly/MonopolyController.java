package com.example.tj_monopoly;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MonopolyController implements Initializable {

    //region FXML Components
    @FXML
    Button firstDie_btn, secondDie_btn, openCard_btn, hamburger_btn, buyProperty_btn, endTurn_btn, mortgageProperty_btn, buyHousing_btn;
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
    Label tilePreviewPrice_lbl, tilePreviewName_lbl, moneyChange_lbl, playerMoney_lbl, landedOn_lbl, ownedBy_lbl, moneyMagnitude_lbl, player_lbl;
    @FXML
    TableView<Property> playerProperties_tv;
    @FXML
    TableColumn<Property, String> property_tc, mortgageValue_tc, mortgaged_tc, hotel_tc;
    @FXML
    TableColumn<Property, Integer> houses_tc;
    //endregion

    Image[] diceImages = {
            new Image("dOne.png"),
            new Image("dTwo.png"),
            new Image("dThree.png"),
            new Image("dFour.png"),
            new Image("dFive.png"),
            new Image("dSixFixed.png")
    };

    String[] tileNames = {
            "Go", "Battle Droid", "Community Chest", "General Grevious", "Trade Blockade", "Millennium Falcon", "Captain Rex", "Chance",
            "Ahsoka Tano", "Arc Trooper Fives", "Jail", "Stormtrooper", "Death Star", "Director Krennic", "Grand Inquisitor",
            "Slave 1", "Wedge Antilles", "Community Chest", "Cassian Andor", "Han Solo", "Free Parking", "Count Dooku",
            "Chance", "Darth Maul", "Kylo Ren", "Star Destroyer", "Plo Koon", "Anakin Skywalker", "Starkiller Base",
            "Mace Windu", "Go To Jail", "Master Yoda", "Luke Skywalker", "Community Chest", "Obi-Wan Kenobi",
            "AT-AT Walker", "Chance", "Emperor Palpatine", "Hyperspace Tax", "Darth Vader"
    };



    //region Animation Variables
    ScaleTransition die1PulseTransition;
    ScaleTransition die2PulseTransition;
    ScaleTransition pulseCardTransition;
    //endregion

    Player player;
    Audio audio = new Audio();
    AnchorPane[] spaces;
    Tile[] tiles = new Tile[40];
    Property[][] monopolies = new Property[8][];
    String currentColor = "";
    int monopolyRowCounter = 0;
    int monopolyColCounter = 0;
    boolean isPreviewCardCentered = false;
    boolean cardNeedsToBeOpened = false;
    boolean rolledDoubles = false;
    int doublesInARow;
    int startingMoney = 1500;
    int newTileIndex;
    Tile currentTile;
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
        player = new Player("Player 1", 0, playerPiece1, startingMoney, false, false);
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

            String color = "";
            boolean isCorner = (i % 10 == 0);
            Property property = null;
            boolean isProperty = false;
            boolean isChanceOrChest;
            Image tileImg;

            if(spaces[i].getChildren().size() == 3){

                color = getPropertyColor(i);
                tileImg = getTileImage(i, 2);

                Label priceLbl = (Label) spaces[i].getChildren().get(1);
                property = new Property(tileNames[i], color, priceLbl.getText(), null, false, false, 0, false);
                isProperty = true;

                boolean isStation = isStation(i);
                boolean isVehicle = isVehicle(i, isCorner);

                if(!isStation && !isVehicle){
                    setUpMonopolies(property);
                }

            }else{

                tileImg = getTileImage(i, 0);
            }

            File file = new File(tileImg.getUrl());
            String fName = file.getName();

            isChanceOrChest = isChanceOrChest(fName);

            tiles[i] = new Tile(tileNames[i], property, isProperty, spaces[i], tileImg, isCorner, isChanceOrChest);
        }

        printMonopolies();
    }
    public String getPropertyColor(int _index){
        //String colorStyle = ": white;";

        String colorStyle = spaces[_index].getChildren().get(0).getStyle();

        if(colorStyle.isEmpty()){
            colorStyle = ": gray;";
        }

        int delimiterIndex = colorStyle.indexOf(":");
        String isolatedColor = colorStyle.substring(delimiterIndex + 2,  colorStyle.length() - 1);

        return isolatedColor;
    }
    private boolean isChanceOrChest(String _fileName){
        return _fileName.equals("empireLogo.png") || _fileName.equals("rebellionLogo.png");
    }
    private Image getTileImage(int _index, int _childIndex){
        ImageView imgV = (ImageView) spaces[_index].getChildren().get(_childIndex);
        return imgV.getImage();
    }
    private boolean isStation(int _index){
        //12 and 28 are locations of the 2 stations
        return _index == 12 || _index == 28;
    }
    private boolean isVehicle(int _index, boolean _isCorner){
        //Is multiple of 5 but not multiple of 10 (not a corner)
        return _index % 5 == 0 && !_isCorner;
    }
    private void setUpMonopolies(Property _property){

        //jaggedArray = [4][]
        //[0] = new array[2]
        //[1] = new array[3]
        //[2] = new array[2]
        //[3] = new array[3]

        String newColor = _property.getColor();


        if(!currentColor.equals(newColor)){
            monopolyRowCounter++;
            monopolyColCounter = 0;
        }

        monopolies[monopolyRowCounter][monopolyColCounter] = _property;
        System.out.println(currentColor);

        monopolyColCounter++;
        currentColor = newColor;
    }
    public void printMonopolies(){
        for(int row = 0; row < monopolies.length; row++){
            for(int col = 0; col < monopolies[row].length; col++){
                System.out.println(monopolies[row][col].getColor());
            }
        }
    }
    public void setUpLabels(){
        playerProperties_tv.setPlaceholder(new Label("No Properties Owned"));
        playerMoney_lbl.setText("$" + player.getMoney());
        player_lbl.setText(player.getPlayerName());
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
    public void stopPulsingDice(){
        firstDie_btn.setScaleX(1);
        firstDie_btn.setScaleY(1);

        secondDie_btn.setScaleX(1);
        secondDie_btn.setScaleY(1);

        die1PulseTransition.stop();
        die2PulseTransition.stop();
    }
    public void pulseCard(){
        cardNeedsToBeOpened = true;

        pulseCardTransition = new ScaleTransition(Duration.seconds(0.5), tilePreviewCard_ap);
        pulseCardTransition.setByX(0.03f);
        pulseCardTransition.setByY(0.03f);
        pulseCardTransition.setCycleCount(Animation.INDEFINITE);
        pulseCardTransition.setAutoReverse(true);
        pulseCardTransition.play();
    }

    public void stopPulsingCard(){
        tilePreviewCard_ap.setScaleX(1);
        tilePreviewCard_ap.setScaleY(1);

        pulseCardTransition.stop();
    }
    //endregion

    //region Dice Clicked (All happens without user input after dice are clicked)
    public void rollDice(){

        audio.setFile(2);
        audio.play();

        stopPulsingDice();
        removeActionsFromDice();

        if(isPreviewCardCentered){
            movePreviewCardBack();
        }

        player.setHasRolled(true);

        //Dice will flicker every 0.1 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event ->{
            flickerDice();
        }));

        //25 Total "bounces"
        timeline.setCycleCount(25);
        timeline.play();

        timeline.setOnFinished(event -> {

            addActionsToDice();

            handleBoardAfterDiceRoll(randomIndexD1 + 1, randomIndexD2 + 1);
        });
    }
    public void removeActionsFromDice(){
        //Don't let the user click on the dice while they are rolling by removing the dice actions
        firstDie_btn.setOnAction(null);
        secondDie_btn.setOnAction(null);
    }
    public void addActionsToDice(){
        firstDie_btn.setOnAction(event -> rollDice());
        secondDie_btn.setOnAction(event -> rollDice());
    }
    private void flickerDice(){
        Random random = new Random();
        randomIndexD1 = random.nextInt(6);
        randomIndexD2 = random.nextInt(6);

        firstDie_img.setImage(diceImages[randomIndexD1]);
        secondDie_img.setImage(diceImages[randomIndexD2]);
    }

    public void handleBoardAfterDiceRoll(int die1Num, int die2Num){

//        die1Num = 1;
//        die2Num = 1;

        int numberOfSpaces = die1Num + die2Num;
        //numberOfSpaces = 38;

        //region Rolled doubles
        rolledDoubles = die1Num == die2Num;

        if(rolledDoubles){
            player.setHasRolled(false);
            doublesInARow++;
        }else{
            doublesInARow = 0;
        }
        if(doublesInARow >= 3){
            //goToJail();

            //numberOfSpaces = Math.abs(10 - player.getCurrentTileIndex());
        }
        //endregion
        
        int tileBeforeMove = player.getCurrentTileIndex();

        //Checking to see if new tile will be going back to start of list (going to or past go)
        newTileIndex = (tileBeforeMove + numberOfSpaces) % tiles.length;
        player.setCurrentTileIndex(newTileIndex);
        currentTile = tiles[newTileIndex];

        //Dividing by 10 to get first digit, getting the board side (since it's an int if number is less than 10
        // boardSide will equal 0)
        int boardSideBeforeMoving = tileBeforeMove / 10;
        int boardSideAfterMoving = newTileIndex / 10;

        int numberOfSidesJumpedTo = getNumSidesJumped(boardSideBeforeMoving, boardSideAfterMoving);

        setUpTilePreview();

        //rotateBoardAfterMove(numberOfSidesJumpedTo);

        movePlayerPiece(boardSideAfterMoving);

        handlePassedGo(tileBeforeMove);

        updateSideUILabels();
    }

    private int getNumSidesJumped(int _boardSideBeforeMoving, int _boardSideAfterMoving){

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

    public void rotateBoardAfterMove(int _numberOfSidesJumpedTo){

        int addedRotation = _numberOfSidesJumpedTo * -90;

        if(_numberOfSidesJumpedTo > 0){

            Duration duration = Duration.millis(2000);

            nodeRotateTransition(outerBoard_ap, duration, addedRotation);
            nodeRotateTransition(innerBoard_ap, duration, -addedRotation);
            nodeRotateTransition(playerPiece1, duration, -addedRotation);
        }
    }
    public void nodeRotateTransition(Node _node, Duration _duration, int _addedRotation){
        RotateTransition rotation = new RotateTransition(_duration, _node);
        rotation.setByAngle(_addedRotation);
        rotation.play();
    }

    public void movePlayerPiece(int _boardSideAfterMoving){

        handleNecessaryTileActions();
        setAllowedUserActions();

        spaces[newTileIndex].getChildren().add(player.getPlayerIcon());

        //Set player icon to default X and Y
        player.getPlayerIcon().setLayoutX(25);
        player.getPlayerIcon().setLayoutY(25);

        if(!currentTile.isCornerTile()){

            //If new tile is not a corner tile, offset is necessary based on which side of the board
            //the new tile is on. By default, player location is set to corner offset
            if(_boardSideAfterMoving == 0 || _boardSideAfterMoving == 2){
                player.getPlayerIcon().setLayoutX(3);
            }else if(_boardSideAfterMoving == 1 || _boardSideAfterMoving == 3){
                player.getPlayerIcon().setLayoutY(3);
            }
        }
    }

    //region Tile Landed On Actions
    public void handlePassedGo(int _tileBeforeMove){
        if(_tileBeforeMove > newTileIndex && !player.isInJail()){
            gainedMoney(200);
        }
    }
    public void handleNecessaryTileActions(){

        if(currentTile.isProperty()) {
            int rentOwed = calculateRent();

            if(rentOwed > 0){
                lostMoney(rentOwed);
            }
        }

        switch (newTileIndex){

            case 4:
                //$200 Tax
                lostMoney(200);

                break;
            case 10:
                //Just Visiting

                break;
            case 20:
                //Free Parking

                break;
            case 30:
                //Go to Jail
                //goToJail();

                break;
            case 38:
                //$100 Tax
                lostMoney(100);

                break;
        }
    }
    private int calculateRent(){
        int rentDue = 0;
        Property property = currentTile.getProperty();

        //is property owned && not mortgaged -> subtract rent
        if (property.isOwned() && !property.isMortgaged()) {

            rentDue = property.getPriceAsInt() / 10;

            if(property.getNumberHouses() >= 1){
                rentDue *= 4;
            }
            if(property.getNumberHouses() >= 2){
                rentDue *= 3;
            }
            if(property.getNumberHouses() >= 3){

                if(rentDue <= 250){
                    rentDue *= 3;
                }else{
                    rentDue *= 2 + 150;
                }

            }
            if(property.getNumberHouses() == 4){
                rentDue += 150;
            }

            if(property.hasHotel()){
                rentDue = property.getPriceAsInt() * 5;
            }

            //Check to see if current player owns property
            if(property.getOwner().equals(player)){
                rentDue = -1;
            }
        }

        return rentDue;
    }
    public void goToJail(){
        player.setInJail(true);
        //player.setCurrentTileIndex(10);

        //spaces[10].getChildren().add(player.getPlayerIcon());

        //setAllowedUserActions();
    }
    public void setAllowedUserActions(){

        //Disable End turn button if user has not rolled or if they have a card to open
        if(!player.hasRolled() || cardNeedsToBeOpened){

            endTurn_btn.setDisable(true);
        }else{

            endTurn_btn.setDisable(false);
        }

        //Checking for Doubles
        if(rolledDoubles){
            pulseDice();
        }

        //Disable dice buttons if player has already rolled or if there is a card to be opened first
        if(player.hasRolled() || cardNeedsToBeOpened){
            firstDie_btn.setDisable(true);
            secondDie_btn.setDisable(true);

            stopPulsingDice();
        }else{
            firstDie_btn.setDisable(false);
            secondDie_btn.setDisable(false);
        }

        //Disable buy button if tile can not be bought or player doesn't have enough money
        if(tileCanBePurchased()){

            if(currentTile.getProperty().getPriceAsInt() <= player.getMoney()){
                buyProperty_btn.setDisable(false);
            }else{
                buyProperty_btn.setDisable(true);
            }
        }else{
            buyProperty_btn.setDisable(true);
        }

        //Check to see if player is in jail
        if(player.isInJail()){

        }

        //Disable buy housing button if player has no monopolies
        buyHousing_btn.setDisable(player.getMonopolies().isEmpty());

        //Disable mortgage property button if the user has no properties
        mortgageProperty_btn.setDisable(player.getProperties().isEmpty());
    }

    private boolean tileCanBePurchased(){

        if(currentTile.isProperty()){

            //If the tile is a property and not owned, return true because it can be purchased
            return !currentTile.getProperty().isOwned();
        }

        return false;
    }
    //endregion

    //region Tile Preview
    public void setUpTilePreview(){

        Tile newTile = currentTile;
        boolean isProperty = newTile.isProperty();

        //region Preview Image
        Image img = newTile.getImg();
        tilePreview_img.setImage(img);
        tilePreview_img.setLayoutX(getImageOffset(img));
        //endregion

        //region Preview Color Banner & Text
        String colorStyle = ": white;";

        if(isProperty){
            colorStyle = spaces[newTileIndex].getChildren().get(0).getStyle();

            if(colorStyle.isEmpty()){
                colorStyle = ": white;";
            }
        }

        tilePreviewColor_ap.setStyle(colorStyle);

        tilePreviewName_lbl.setText(newTile.getName());

        int delimiterIndex = colorStyle.indexOf(":");
        String isolatedColor = colorStyle.substring(delimiterIndex + 2,  colorStyle.length() - 1);

        if(isolatedColor.equals("brown") || isolatedColor.equals("blue")){
            tilePreviewName_lbl.setTextFill(Paint.valueOf("white"));
        }else{
            tilePreviewName_lbl.setTextFill(Paint.valueOf("black"));
        }


        if(isProperty){
            tilePreviewPrice_lbl.setText(newTile.getProperty().getPriceAsString());
        }else{
            tilePreviewPrice_lbl.setText("");
        }
        //endregion

        if(newTile.isChanceOrChest()){
            pulseCard();
            openCard_btn.setVisible(true);
        }

        if(newTile.isCornerTile()){
            tilePreviewCard_ap.setStyle("-fx-background-color:  #c7eaca;");
        }else{
            tilePreviewCard_ap.setStyle("-fx-background-color:  white;");
        }
    }

    private int getImageOffset(Image _img){

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
        stopPulsingCard();
        openCard_btn.setVisible(false);

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

        isPreviewCardCentered = true;
        cardNeedsToBeOpened = false;

        setAllowedUserActions();
    }

    public void movePreviewCardBack(){
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1.5), tilePreviewCard_ap);
        translateTransition.setToX(-5);
        translateTransition.setToY(0);
        translateTransition.play();

        isPreviewCardCentered = true;
    }
    //endregion

    //region User Actions
    public void buyProperty(){

        Property property = currentTile.getProperty();

        lostMoney(property.getPriceAsInt());

        property.setOwned(true);
        property.setOwner(player);
        player.addProperty(property);
        buyProperty_btn.setDisable(true);

        updateSideUILabels();
        addPropertyToPropertyTable();

    }

    public void addPropertyToPropertyTable(){
        ObservableList<Property> observableProperties = FXCollections.observableArrayList();

        observableProperties.addAll(player.getProperties());

        property_tc.setCellValueFactory(new PropertyValueFactory<>("name"));
        mortgageValue_tc.setCellValueFactory(new PropertyValueFactory<>("mortgageValue"));
        mortgaged_tc.setCellValueFactory(new PropertyValueFactory<>("isMortgagedString"));
        houses_tc.setCellValueFactory(new PropertyValueFactory<>("numberHouses"));
        hotel_tc.setCellValueFactory(new PropertyValueFactory<>("hasHotelString"));

        playerProperties_tv.setItems(observableProperties);
    }

    public void endTurn(){
        player.setHasRolled(false);

        pulseDice();
        movePreviewCardBack();

        setAllowedUserActions();

        //Players should not be able to change their mind and buy a property after passing on it the turn prior
        buyProperty_btn.setDisable(true);
    }
    //endregion

    //region Money Animations
    public void gainedMoney(int _moneyGained){
        audio.setFile(6);
        audio.play();

        player.addMoney(_moneyGained);
        moneyGainedAnimation(_moneyGained);
        playerMoney_lbl.setText("$" + player.getMoney());
    }
    public void lostMoney(int _moneyLost){
        audio.setFile(6);
        audio.play();

        player.subtractMoney(_moneyLost);
        moneyLostAnimation(_moneyLost);
        playerMoney_lbl.setText("$" + player.getMoney());
    }
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
    public void setUpBurgerMenuClickOff(){
        Scene scene = hamburger_btn.getScene();

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent ->{

            if(!mouseEvent.getPickResult().getIntersectedNode().equals(hamburger_btn)){
                closeBurgerMenuClickOff();
            }
        });
    }
    public void burgerMenuClicked(){
        hamburgerMenu_ap.setVisible(!hamburgerMenu_ap.isVisible());
        hamburgerBackground_ap.setVisible(hamburgerMenu_ap.isVisible());

    }
    public void closeBurgerMenuClickOff(){
        hamburgerMenu_ap.setVisible(false);
        hamburgerBackground_ap.setVisible(false);

    }

    public void updateSideUILabels(){

        //Player label
        player_lbl.setText(player.getPlayerName());

        //Landed on label
        landedOn_lbl.setText("Player Landed On: " + tileNames[newTileIndex]);

        //Owned by label
        if(currentTile.isProperty()){

            if(currentTile.getProperty().isOwned()){
                ownedBy_lbl.setText("Owned By: " + currentTile.getProperty().getOwner().getPlayerName());
            }else{
                ownedBy_lbl.setText("Owned By: Nobody");
            }

        }else{
            ownedBy_lbl.setText("Owned By: N/A");
        }

        //Money owed/cost label
        if(currentTile.isProperty()){

            if(currentTile.getProperty().isOwned()){
                int rentOwed = calculateRent();

                //calculateRent returns -1 if current player owns property and 0 if it is mortgaged
                if(rentOwed == -1){
                    moneyMagnitude_lbl.setText("Welcome Home!");
                }else{

                    moneyMagnitude_lbl.setText("Rent Owed: $" + rentOwed);
                }

            }else{
                moneyMagnitude_lbl.setText("Price: " + currentTile.getProperty().getPriceAsString());
            }

        }else{
            moneyMagnitude_lbl.setText("Price: N/A");
        }

        //Check for taxes
        if(newTileIndex == 4){
            moneyMagnitude_lbl.setText("Taxes Owed: $200");
        }else if(newTileIndex == 38){
            moneyMagnitude_lbl.setText("Taxes Owed: $100");
        }

    }

    public void quit(){
        System.exit(0);
    }
    //endregion
}
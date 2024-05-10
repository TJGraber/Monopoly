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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

public class MonopolyController implements Initializable {

    //region FXML Components
    @FXML
    Button firstDie_btn, secondDie_btn, openCard_btn, hamburger_btn, buyProperty_btn, trade_btn, mortgageProperty_btn,
            buyHousing_btn, endTurn_btn, mortgageMenu_btn, unmortgageMenu_btn, buyHouse_btn, sellHouse_btn, buyHotel_btn, sellHotel_btn;
    @FXML
    ImageView firstDie_img, secondDie_img, playerPiece1, tilePreview_img, hamburgerIcon_img, lightsaber_img;
    @FXML
    AnchorPane space0, space1, space2, space3, space4, space5, space6, space7, space8, space9, space10, space11,
            space12, space13, space14, space15, space16, space17, space18, space19, space20, space21, space22, space23,
            space24, space25, space26, space27, space28, space29, space30, space31, space32, space33, space34, space35,
            space36, space37, space38, space39;
    @FXML
    AnchorPane outerBoard_ap, innerBoard_ap, tilePreviewColor_ap, tilePreviewCard_ap, hamburgerMenu_ap, hamburgerBackground_ap,
            lightsaberMenu_ap, blockButtonPress_ap, tradeMenu_ap, mortgagePropertyMenu_ap, buyHousingMenu_ap;
    @FXML
    Label tilePreviewPrice_lbl, tilePreviewName_lbl, moneyGained_lbl, moneyLost_lbl, playerMoney_lbl, landedOn_lbl, ownedBy_lbl, moneyMagnitude_lbl, player_lbl;
    @FXML
    TableView<Property> playerProperties_tv;
    @FXML
    TableColumn<Property, String> property_tc, mortgageValue_tc, mortgaged_tc, hotel_tc;
    @FXML
    TableColumn<Property, Integer> houses_tc;
    @FXML
    ComboBox<Property> properties_cmb, monopolyProperties_cmb;
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
    ArrayList<Player> players = new ArrayList<>();
    AnchorPane[] spaces;
    Tile[] tiles = new Tile[40];
    ArrayList<Property> properties = new ArrayList<>();
    Property[][] monopolies = new Property[8][];
    int housesLeft = 32;
    int hotelsLeft = 12;
    Property propertyToBeMortgaged;
    Property propertyToBuyHousing;
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

        setUpMonopolyJaggedArray();

        audio.setFile(4);
        audio.play();
        audio.loop();

        pulseDice();

        properties_cmb.valueProperty().addListener((observable, oldValue, selectedProperty) -> {
            propertyToBeMortgaged = selectedProperty;
            setMortgageButtons();
        });

        monopolyProperties_cmb.valueProperty().addListener((observable, oldValue, selectedProperty) -> {
            propertyToBuyHousing = selectedProperty;
            setHousingButtons();
        });
    }
    int randomIndexD1;
    int randomIndexD2;

    //region Setup
    public void setUpPlayers(){
        player = new Player("Player 1", 0, playerPiece1, startingMoney, false, false);
        players.add(player);
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
                String pricePerHouse = getPropertyHousePrice(color);

                property = new Property(tileNames[i], color, priceLbl.getText(), pricePerHouse, null, false, false, 0, false);
                isProperty = true;

                boolean isStation = isStation(i);
                boolean isVehicle = isVehicle(i, isCorner);

                if(!isStation && !isVehicle){
                    properties.add(property);
                }

            }else{

                tileImg = getTileImage(i, 0);
            }

            File file = new File(tileImg.getUrl());
            String fName = file.getName();

            isChanceOrChest = isChanceOrChest(fName);

            tiles[i] = new Tile(tileNames[i], property, isProperty, spaces[i], tileImg, isCorner, isChanceOrChest);
        }

        setUpMonopolies();
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
    private String getPropertyHousePrice(String _color){
        String pricePerHouse = "$";

        if(_color.equals("brown") || _color.equals("cyan")){
            pricePerHouse += "50";
        }
        else if(_color.equals("pink") || _color.equals("orange")){
            pricePerHouse += "100";
        }
        else if(_color.equals("red") || _color.equals("yellow")){
            pricePerHouse += "150";
        }
        else if(_color.equals("green") || _color.equals("blue")){
            pricePerHouse += "200";
        }

        return pricePerHouse;
    }
    private boolean isStation(int _index){
        //12 and 28 are locations of the 2 stations
        return _index == 12 || _index == 28;
    }
    private boolean isVehicle(int _index, boolean _isCorner){
        //Is multiple of 5 but not multiple of 10 (not a corner)
        return _index % 5 == 0 && !_isCorner;
    }
    public void setUpMonopolyJaggedArray(){
        monopolies[0] = new Property[2];
        monopolies[1] = new Property[3];
        monopolies[2] = new Property[3];
        monopolies[3] = new Property[3];
        monopolies[4] = new Property[3];
        monopolies[5] = new Property[3];
        monopolies[6] = new Property[3];
        monopolies[7] = new Property[2];
    }
    private void setUpMonopolies(){
        int count = 0;
        for(int row = 0; row < monopolies.length; row++){
            for(int col = 0; col < monopolies[row].length; col++){

                monopolies[row][col] = properties.get(count);
                count++;
            }
        }
    }
    public void printMonopolies(){
        for(int row = 0; row < monopolies.length; row++){

            System.out.print(monopolies[row][0].getColor() + "s: ");

            for(int col = 0; col < monopolies[row].length; col++){

                System.out.print(monopolies[row][col].getName() + ", ");
            }

            System.out.println();
        }
    }
    public void setUpLabels(){
        playerProperties_tv.setPlaceholder(new Label("No Properties Owned"));
        playerProperties_tv.setSelectionModel(null);
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

        die1Num = 1;
        die2Num = 0;

        int numberOfSpaces = die1Num + die2Num;


        rolledDoubles = die1Num == die2Num;
        handleDoubles();

        handleJail();
        if(player.isInJail()){
            numberOfSpaces = 0;
        }
        
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

    public void handleDoubles(){
        if(rolledDoubles){
            player.setHasRolled(false);
            doublesInARow++;
        }else{
            doublesInARow = 0;
        }

        if(doublesInARow >= 3){
            goToJail();
        }
        if(player.isInJail() && rolledDoubles){
            getOutOfJail();
        }
    }

    public void handleJail(){
        if(player.isInJail()){
            player.setTurnsSpentInJail(player.getTurnsSpentInJail() + 1);
        }

        if(player.getTurnsSpentInJail() == 3){
            getOutOfJail();
            lostMoney(50);
        }
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

        if(!player.isInJail()){
            spaces[newTileIndex].getChildren().add(player.getPlayerIcon());
        }

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

        //Player lands on jail but is just visiting
        if(newTileIndex == 10 && !player.isInJail()){
            player.getPlayerIcon().setLayoutX(5);
            player.getPlayerIcon().setLayoutY(50);
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
                goToJail();

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

            //Check to see if current player owns property
            if(property.getOwner().equals(player)){
                return -1;
            }

            rentDue = property.getPriceAsInt() / 10;
            int numHouses = property.getNumberHouses();

            if(numHouses >= 1){
                rentDue *= 4;
            }
            if(numHouses >= 2){
                rentDue *= 3;
            }
            if(numHouses >= 3){

                if(rentDue <= 250){
                    rentDue *= 3;
                }else{
                    rentDue *= 2 + 150;
                }

            }
            if(numHouses == 4){
                rentDue += 150;
            }

            if(property.hasHotel()){
                rentDue = property.getPriceAsInt() * 5;
            }

        }

        return rentDue;
    }
    public void goToJail(){
        player.setInJail(true);
        player.setTurnsSpentInJail(0);
        player.setCurrentTileIndex(10);
        player.setHasRolled(true);

        doublesInARow = 0;
        rolledDoubles = false;

        spaces[10].getChildren().add(player.getPlayerIcon());

        audio.setFile(9);
        audio.play();

        setAllowedUserActions();
    }
    public void getOutOfJail(){
        player.setInJail(false);
        player.setTurnsSpentInJail(0);

        setAllowedUserActions();
    }
    public void setAllowedUserActions(){

        //Disable End turn button if user has not rolled or if they have a card to open
        if(!player.hasRolled() || cardNeedsToBeOpened){

            endTurn_btn.setDisable(true);
        }else{

            stopPulsingDice();
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

            //stopPulsingDice();
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

        //Stop pulsing if dice are disabled
        if(firstDie_btn.isDisabled()){
            stopPulsingDice();
        }

        //Disable buy housing button if player has no monopolies
        buyHousing_btn.setDisable(player.getMonopolies().isEmpty());

        //Disable mortgage property button if the user has no properties
        mortgageProperty_btn.setDisable(player.getProperties().isEmpty());

        //Disable trade button if no players have properties
        trade_btn.setDisable(!doAnyPlayersHaveProperty());
    }

    private boolean tileCanBePurchased(){

        if(currentTile.isProperty()){

            //If the tile is a property and not owned, return true because it can be purchased
            return !currentTile.getProperty().isOwned();
        }

        return false;
    }

    private boolean doAnyPlayersHaveProperty(){
        for(int i = 0; i < players.size(); i++){
            if(!players.get(i).getProperties().isEmpty()){
                return true;
            }
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

        checkForNewMonopoly();
        setAllowedUserActions();
        updateSideUILabels();
        updatePlayerPropertyTable();
    }

    public void updatePlayerPropertyTable(){
        ObservableList<Property> observableProperties = FXCollections.observableArrayList();

        observableProperties.addAll(player.getProperties());

        property_tc.setCellValueFactory(new PropertyValueFactory<>("name"));
        mortgageValue_tc.setCellValueFactory(new PropertyValueFactory<>("mortgageValue"));
        mortgaged_tc.setCellValueFactory(new PropertyValueFactory<>("isMortgagedString"));
        houses_tc.setCellValueFactory(new PropertyValueFactory<>("numberHouses"));
        hotel_tc.setCellValueFactory(new PropertyValueFactory<>("hasHotelString"));

        playerProperties_tv.setItems(observableProperties);

        System.out.println("heyyy");

        //Trying to figure out how to get rows within the table so that I can style the background
        //based on the color of the property

//        playerProperties_tv.getRowFactory(0)
//        playerProperties_tv.setRowFactory(0);

        //TableCell<Property, String> cell = TableColumn.DEFAULT_CELL_FACTORY.call(property_tc);
//        TableCell<Property, String> cell = property_tc.getCellFactory();
//        Object item = cell.getTableRow().getItem();
    }

    //region Handle Monopolies
    private void checkForNewMonopoly(){

        for(int row = 0; row < monopolies.length; row++){
            boolean ownsAllPropertiesInRow = true;

            for(int col = 0; col < monopolies[row].length; col++){

                if(!playerOwnsProperty(monopolies[row][col])){
                    ownsAllPropertiesInRow = false;
                }
            }

            if(ownsAllPropertiesInRow && !playerAlreadyHasThisMonopoly(row)){
                addMonopolyToPlayer(row);
            }
        }
    }
    private boolean playerOwnsProperty(Property _property){

        for(int i = 0; i < player.getProperties().size(); i++){
            if(_property == player.getProperties().get(i)){
                return true;
            }
        }

        return false;
    }
    private boolean playerAlreadyHasThisMonopoly(int _row){

        if(player.getMonopolies().isEmpty()){
            return false;
        }

        String monopolyColor = monopolies[_row][0].getColor();

        for(int i = 0; i < player.getMonopolies().size(); i++){
            String playerMonopolyColor = player.getMonopolies().get(i).getColor();

            if(playerMonopolyColor.equals(monopolyColor)){
                return true;
            }
        }

        return false;
    }
    private void addMonopolyToPlayer(int _row){
        ArrayList<Property> newMonopolyProperties = new ArrayList<>(Arrays.asList(monopolies[_row]));
        player.addMonopoly(newMonopolyProperties);
    }
    //endregion

    public void trade(){
        deployLightsaberMenu(tradeMenu_ap);
    }

    public void mortgageProperty(){

        deployLightsaberMenu(mortgagePropertyMenu_ap);
    }

    public void buyHousing(){

        deployLightsaberMenu(buyHousingMenu_ap);

        //int pricePerHouse = player.getMonopolies().getFirst().getPricePerHouseAsInt();
        //lostMoney(pricePerHouse);
    }

    public void endTurn(){
        player.setHasRolled(false);

        pulseDice();

        if(isPreviewCardCentered){
            movePreviewCardBack();
        }

        setAllowedUserActions();

        //Players should not be able to change their mind and buy a property after passing on it the turn prior
        buyProperty_btn.setDisable(true);
    }

    //region Mortgage
    public void setMortgageButtons(){

        mortgageMenu_btn.setDisable(true);
        unmortgageMenu_btn.setDisable(true);

        //If property is not already mortgaged, activate mortgage button
        if(!propertyToBeMortgaged.isMortgaged()){
            mortgageMenu_btn.setDisable(false);
        }else{
            //If property is mortgaged and player has enough money to unmortgage, activate unmortgage button
            if(player.getMoney() >= propertyToBeMortgaged.getUnmortgageCostAsInt()){
                unmortgageMenu_btn.setDisable(false);
            }
        }
    }
    public void mortgageSelectedProperty(){
        propertyToBeMortgaged.setMortgaged(true);
        gainedMoney(propertyToBeMortgaged.getMortgageValueAsInt());

        setMortgageButtons();
        updatePlayerPropertyTable();
    }
    public void unmortgageSelectedProperty(){
        propertyToBeMortgaged.setMortgaged(false);
        lostMoney(propertyToBeMortgaged.getUnmortgageCostAsInt());

        setMortgageButtons();
        updatePlayerPropertyTable();
    }
    //endregion

    //region Housing
    public void setHousingButtons(){

        buyHouse_btn.setDisable(true);
        sellHouse_btn.setDisable(true);
        buyHotel_btn.setDisable(true);
        sellHotel_btn.setDisable(true);

        if(propertyToBuyHousing != null){

            int houses = propertyToBuyHousing.getNumberHouses();

            //If player has enough money, houses are evenly spread, property has less than 4 houses,
            //there are houses left in the housing pool, and property doesn't have a hotel, enable buyHousing button
            if(houses < 4 && playerHasHousingFunds() && housesAreEvenlySpread() && housesLeft > 0 && !propertyToBuyHousing.hasHotel()){
                buyHouse_btn.setDisable(false);
            }
            //If property has any houses, allow player to sell them
            if(houses > 0){
                sellHouse_btn.setDisable(false);
            }
            //If property has 4 houses, houses are evenly spread, has enough money, and doesn't already have
            // a hotel, allow player to buy hotel
            if(houses == 4 && playerHasHousingFunds() && housesAreEvenlySpread() && hotelsLeft > 0 && !propertyToBuyHousing.hasHotel()){
                buyHotel_btn.setDisable(false);
            }
            //If property has a hotel, allow player to sell it
            if(propertyToBuyHousing.hasHotel()){
                sellHotel_btn.setDisable(false);
            }
        }
    }
    public boolean playerHasHousingFunds(){
        return player.getMoney() >= propertyToBuyHousing.getPriceAsInt();
    }
    public boolean housesAreEvenlySpread(){

        ArrayList<Integer> numHousesOnProperties = new ArrayList<>();

        //Add 1 house to the property a house might be added to
        propertyToBuyHousing.setNumberHouses(propertyToBuyHousing.getNumberHouses() + 1);

        //Get all the houses of properties in a monopoly
        for(int i = 0; i < player.getMonopolies().size(); i++){
            if(player.getMonopolies().get(i).getColor().equals(propertyToBuyHousing.getColor())){
                numHousesOnProperties.add(player.getMonopolies().get(i).getNumberHouses());
            }
        }

        //Compare each number of houses between the properties in a monopoly
        //They should all be within 1 of each other
        int min = numHousesOnProperties.getFirst();
        int max = numHousesOnProperties.getFirst();

        for(int i = 1; i < numHousesOnProperties.size(); i ++){
            if(numHousesOnProperties.get(i) > max){
                max = numHousesOnProperties.get(i);
            }
            if(numHousesOnProperties.get(i) < min){
                min = numHousesOnProperties.get(i);
            }
        }

        propertyToBuyHousing.setNumberHouses(propertyToBuyHousing.getNumberHouses() - 1);

        return max - min <= 1;
    }

    public void buyHouse(){
        propertyToBuyHousing.setNumberHouses(propertyToBuyHousing.getNumberHouses() + 1);
        lostMoney(propertyToBuyHousing.getPricePerHouseAsInt());

        housesLeft--;
        setHousingButtons();
        updatePlayerPropertyTable();
    }
    public void sellHouse(){
        propertyToBuyHousing.setNumberHouses(propertyToBuyHousing.getNumberHouses() - 1);
        gainedMoney(propertyToBuyHousing.getPricePerHouseAsInt() / 2);

        housesLeft++;
        setHousingButtons();
        updatePlayerPropertyTable();
    }
    public void buyHotel(){
        propertyToBuyHousing.setHasHotel(true);
        lostMoney(propertyToBuyHousing.getPricePerHouseAsInt());

        hotelsLeft--;
        setHousingButtons();
        updatePlayerPropertyTable();
    }
    public void sellHotel(){
        propertyToBuyHousing.setHasHotel(false);
        gainedMoney(propertyToBuyHousing.getPricePerHouseAsInt() / 2);

        hotelsLeft++;
        setHousingButtons();
        updatePlayerPropertyTable();
    }
    //endregion
    //endregion

    //region Lightsaber Menu
    public void deployLightsaberMenu(AnchorPane _menu_ap){

        blockButtonPress_ap.setVisible(true);
        _menu_ap.setVisible(true);

        properties_cmb.setItems(FXCollections.observableList(player.getProperties()));
        monopolyProperties_cmb.setItems(FXCollections.observableList(player.getMonopolies()));

        TranslateTransition deployLightsaberTransition = new TranslateTransition(Duration.millis(500), lightsaberMenu_ap);
        deployLightsaberTransition.setToX(-688);
        deployLightsaberTransition.setCycleCount(1);
        deployLightsaberTransition.play();

        audio.setFile(7);
        audio.play();
    }
    public void retractLightsaberMenu(){

        TranslateTransition retractLightsaberTransition = new TranslateTransition(Duration.millis(1500), lightsaberMenu_ap);
        retractLightsaberTransition.setToX(0);
        retractLightsaberTransition.setCycleCount(1);
        retractLightsaberTransition.play();

        retractLightsaberTransition.setOnFinished(actionEvent -> {
            disableLightsaberPanes();
            setAllowedUserActions();
        });

        audio.setFile(8);
        audio.play();
    }
    private void disableLightsaberPanes(){
        blockButtonPress_ap.setVisible(false);

        //mortgageProperty_tf.clear();

        tradeMenu_ap.setVisible(false);
        mortgagePropertyMenu_ap.setVisible(false);
        buyHousingMenu_ap.setVisible(false);
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

        //moneyChange_lbl.setTextFill(MONEYGREEN);
        moneyGained_lbl.setText("+$" + _moneyGained);

        fadeAndMoveMoneyChange(moneyGained_lbl);
    }
    public void moneyLostAnimation(int _moneyLost){

        //moneyChange_lbl.setTextFill(NEGATIVERED);
        moneyLost_lbl.setText("-$" + _moneyLost);

        fadeAndMoveMoneyChange(moneyLost_lbl);
    }
    public void fadeAndMoveMoneyChange(Label _moneyChange_lbl){

        FadeTransition fadeMoneyText = new FadeTransition(Duration.millis(1000), _moneyChange_lbl);
        fadeMoneyText.setFromValue(0.0);
        fadeMoneyText.setToValue(1);
        fadeMoneyText.setCycleCount(2);
        fadeMoneyText.setAutoReverse(true);

        fadeMoneyText.play();

        TranslateTransition moveMoneyText = new TranslateTransition(Duration.millis(2000), _moneyChange_lbl);
        moveMoneyText.setToY(0.0);
        moveMoneyText.setToY(550);
        moveMoneyText.setCycleCount(1);

        moveMoneyText.play();

        moveMoneyText.setOnFinished(event -> {
            resetMoneyChangeLabel();
        });
    }
    public void resetMoneyChangeLabel(){
        moneyGained_lbl.setTranslateY(0);
        moneyGained_lbl.setOpacity(0);

        moneyLost_lbl.setTranslateY(0);
        moneyLost_lbl.setOpacity(0);
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
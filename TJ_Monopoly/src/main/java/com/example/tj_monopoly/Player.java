package com.example.tj_monopoly;

import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Player {

    private String playerName;
    private int currentTileIndex, money;
    private ImageView playerIcon;
    private boolean inJail, hasRolled;

    ArrayList<Property> properties = new ArrayList<>();
    ArrayList<Property> monopolies = new ArrayList<>();

    public Player(String _playerName, int _currentTile, ImageView _playerIcon, int _money, boolean _inJail, boolean _hasRolled){

        playerName = _playerName;
        currentTileIndex = _currentTile;
        playerIcon = _playerIcon;
        money = _money;
        inJail = _inJail;
        hasRolled = _hasRolled;
    }

    //region Getters & Setters
    public String getPlayerName(){
        return playerName;
    }

    public void setPlayerName(String _playerName){
        playerName = _playerName;
    }

    public int getCurrentTileIndex(){
        return currentTileIndex;
    }

    public void setCurrentTileIndex(int _currentTileIndex){
        currentTileIndex = _currentTileIndex;
    }

    public ImageView getPlayerIcon(){
        return playerIcon;
    }

    public int getMoney(){
        return money;
    }

    public void setMoney(int _money){
        money = _money;
    }

    public boolean isInJail(){
        return inJail;
    }

    public boolean hasRolled(){
        return hasRolled;
    }

    public void setHasRolled(boolean _hasRolled){
        hasRolled = _hasRolled;
    }

    public void setInJail(boolean _inJail){
        inJail = _inJail;
    }

    public ArrayList<Property> getProperties(){
        return properties;
    }

    public ArrayList<Property> getMonopolies(){
        return monopolies;
    }
    //endregion

    public void addMoney(int _money){
        money += _money;
    }

    public void subtractMoney(int _money){
        money -= _money;
    }

    public void addProperty(Property _property){
        properties.add(_property);
    }

}

package com.example.tj_monopoly;

import javafx.scene.image.ImageView;

public class Player {


    int currentTileIndex, money;
    ImageView playerIcon;
    boolean inJail;

    public Player(int _currentTile, ImageView _playerIcon, int _money, boolean _inJail){

        currentTileIndex = _currentTile;
        playerIcon = _playerIcon;
        money = _money;
        inJail = _inJail;
    }

    public int getCurrentTileIndex(){
        return currentTileIndex;
    }

    public void setCurrentTile(int _currentTileIndex){
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

    public void addMoney(int _money){
        money += _money;
    }

    public void subtractMoney(int _money){
        money -= _money;
    }

    public boolean isInJail(){
        return inJail;
    }

    public void setInJail(boolean _inJail){
        inJail = _inJail;
    }
}

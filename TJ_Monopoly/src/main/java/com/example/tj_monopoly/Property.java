package com.example.tj_monopoly;

public class Property {

    private String name, color, price, pricePerHouse, mortgageValue;
    private Player owner;
    private boolean isOwned, isMortgaged, hasHotel;
    private int numberHouses;

    private String isMortgagedString, hasHotelString;

    public Property(String _name, String _color, String _price, String _pricePerHouse, Player _owner, boolean _isOwned, boolean _isMortgaged, int _numHouses, boolean _hasHotel){
        name = _name;
        color = _color;
        price = _price;
        pricePerHouse = _pricePerHouse;
        owner = _owner;
        isOwned = _isOwned;
        isMortgaged = _isMortgaged;
        numberHouses = _numHouses;
        hasHotel = _hasHotel;

        setUpNonParameterVariables();
    }

    private void setUpNonParameterVariables(){
        mortgageValue = "$" + (getPriceAsInt() / 2);

        if(isMortgaged){
            isMortgagedString = "Yes";
        }else{
            isMortgagedString = "No";
        }

        if(hasHotel){
            hasHotelString = "Yes";
        }else{
            hasHotelString = "No";
        }
    }

    public String getName(){
        return name;
    }
    public void setName(String _name){
        name = _name;
    }
    public String getColor(){
        return color;
    }
    public void setColor(String _color){
        color = _color;
    }
    public String getPriceAsString(){
        return price;
    }
    public int getPriceAsInt(){
        return Integer.parseInt(price.substring(1));
    }
    public String getPricePerHouseAsString(){
        return pricePerHouse;
    }
    public int getPricePerHouseAsInt(){
        return Integer.parseInt(pricePerHouse.substring(1));
    }
    public Player getOwner(){
        return owner;
    }
    public void setOwner(Player _owner){
        owner = _owner;
    }
    public boolean isOwned(){
        return isOwned;
    }
    public void setOwned(boolean _isOwned){
        isOwned = _isOwned;
    }
    public boolean isMortgaged(){
        return isMortgaged;
    }
    public int getNumberHouses(){
        return numberHouses;
    }
    public void setNumberHouses(int _numberHouses){
        numberHouses = _numberHouses;
    }
    public boolean hasHotel(){
        return hasHotel;
    }
    public void setHasHotel(boolean _hasHotel){
        hasHotel = _hasHotel;
    }
    public String getMortgageValue(){
        return mortgageValue;
    }
    public void setMortgageValue(String _mortgageValue){
        mortgageValue = _mortgageValue;
    }
    public String getIsMortgagedString(){
        return isMortgagedString;
    }
    public String getHasHotelString(){
        return hasHotelString;
    }
}

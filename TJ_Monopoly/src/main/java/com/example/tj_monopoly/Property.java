package com.example.tj_monopoly;

public class Property {

    String price;
    boolean isOwned, isMortgaged;

    public Property(String _price, boolean _isOwned, boolean _isMortgaged){
        price = _price;
        isOwned = _isOwned;
        isMortgaged = _isMortgaged;
    }

    public String getPrice(){
        return price;
    }
    public boolean isOwned(){
        return isOwned;
    }
    public boolean isMortgaged(){
        return isMortgaged;
    }
}

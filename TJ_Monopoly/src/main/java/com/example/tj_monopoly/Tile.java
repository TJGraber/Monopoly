package com.example.tj_monopoly;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

public class Tile {

    String name;
    Property property;
    AnchorPane anchor;
    Image img;
    boolean isCornerTile;
    boolean isChanceOrChest;

    public Tile(String _name, Property _property, AnchorPane _anchor, Image _img, boolean _isCornerTile, boolean _isChanceOrChest){
        name = _name;
        property = _property;
        anchor = _anchor;
        img = _img;
        isCornerTile = _isCornerTile;
        isChanceOrChest = _isChanceOrChest;
    }

    public String getName(){
        return name;
    }
    public Property getProperty(){
        return property;
    }
    public AnchorPane getAnchor(){
        return anchor;
    }
    public Image getImg(){
        return img;
    }
    public boolean isCornerTile(){
        return isCornerTile;
    }
    public boolean isChanceOrChest(){
        return isChanceOrChest;
    }
}
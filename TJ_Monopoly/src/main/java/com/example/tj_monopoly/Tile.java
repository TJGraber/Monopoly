package com.example.tj_monopoly;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

public class Tile {

    private String name;
    private Property property;
    private boolean isProperty, isCornerTile, isChanceOrChest;
    private AnchorPane anchor;
    private Image img;

    public Tile(String _name, Property _property, boolean _isProperty, AnchorPane _anchor, Image _img, boolean _isCornerTile, boolean _isChanceOrChest){
        name = _name;
        property = _property;
        isProperty = _isProperty;
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
    public boolean isProperty(){
        return isProperty;
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
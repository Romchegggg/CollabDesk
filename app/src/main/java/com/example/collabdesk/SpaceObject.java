package com.example.collabdesk;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

public class SpaceObject {
    private String Text="";
    private float CoordX;

    private float CoordY;

    private float Height;

    private float Width;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName = "";
    @Exclude
    private Bitmap bitmap;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Exclude
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private int Type;
    public float getCoordX() {
        return CoordX;
    }

    public void setCoordX(float coordX) {
        CoordX = coordX;
    }

    public float getCoordY() {
        return CoordY;
    }

    public void setCoordY(float coordY) {
        CoordY = coordY;
    }

    public float getWidth() {
        return Width;
    }

    public void setWidth(float width) {
        Width = width;
    }

    public float getHeight() {
        return Height;
    }

    public void setHeight(float height) {
        Height = height;
    }


    public float getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getText() {
        return Text;
    }
    public void setText(String text) {
        Text = text;
    }
}

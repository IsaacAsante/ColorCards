package com.example.isaac.coloredcardsbraingame;

public class ColorCard {
    private String colorName;
    private int color;

    public ColorCard(String colorName, int color) {
        this.colorName = colorName;
        this.color = color;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

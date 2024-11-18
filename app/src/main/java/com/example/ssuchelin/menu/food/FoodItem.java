package com.example.ssuchelin.menu.food;

public class FoodItem {
    private String mainMenu;
    private String subMenu;
    private int imageResId;
    private float rating; // 별점

    public FoodItem(String mainMenu, String subMenu, int imageResId, float rating) {
        this.mainMenu = mainMenu;
        this.subMenu = subMenu;
        this.imageResId = imageResId;
        this.rating = rating;
    }

    public String getMainMenu() {
        return mainMenu;
    }

    public String getSubMenu() {
        return subMenu;
    }

    public int getImageResId() {
        return imageResId;
    }

    public float getRating() {
        return rating;
    }
}
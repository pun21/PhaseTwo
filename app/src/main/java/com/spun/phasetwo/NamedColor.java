package com.spun.phasetwo;

public class NamedColor {
    private float[] hsv;
    private String name;

    public NamedColor(){}
    public NamedColor(float[] hsv, String name) {
        this.hsv = hsv;
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getHsv() {
        return hsv;
    }

    public void setHsv(float[] hsv) {
        this.hsv = hsv;
    }
}

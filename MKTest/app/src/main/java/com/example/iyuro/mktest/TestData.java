package com.example.iyuro.mktest;

public class TestData {
    int x;
    public TestData(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "TestData{" +
                "x=" + x +
                '}';
    }
}

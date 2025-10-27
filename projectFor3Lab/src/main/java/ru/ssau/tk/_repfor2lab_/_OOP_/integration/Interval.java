package ru.ssau.tk._repfor2lab_._OOP_.integration;

public record Interval(int a, int b) {
    public int length() {
        return b - a;
    }
}
package com.cms.model;

public record Course(int id, String code, String title, int credits, String instructor, int maxSeats) {
    @Override public String toString() {
        return String.format("%-4d %-8s %-26s %-3d %-14s seats:%d", id, code, title, credits, instructor, maxSeats);
    }
}
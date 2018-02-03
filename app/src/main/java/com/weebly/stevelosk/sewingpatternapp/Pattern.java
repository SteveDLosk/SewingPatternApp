package com.weebly.stevelosk.sewingpatternapp;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by steve on 1/25/2018.  Data model for patterns in the database
 */

public class Pattern implements Serializable{

    private int patternId;  // a unique id for each instance, assigned automatically on instantiation
    private String brand;
    private String patternNumber;  // the pattern number assigned by the pattern company

    // sizing
    private String sizes;  // the listed sizes on the pattern
    private int minNumericSize;
    private int maxNumericSize;
    private String minCharacterBasedSize;  // example: "XS"
    private String maxCharacterBasedSize;  // example: "XXL"

    // images, stored as file path Strings
    private String frontPicture;
    private String backPicture;
    private byte[] frontImgBytes;
    private byte[] backImgBytes;

    public byte[] getBackImgBytes() {
        return backImgBytes;
    }

    public void setBackImgBytes(byte[] backImgBytes) {
        this.backImgBytes = backImgBytes;
    }

    public byte[] getFrontImgBytes() {
        return frontImgBytes;
    }

    public void setFrontImgBytes(byte[] frontImgBytes) {
        this.frontImgBytes = frontImgBytes;
    }

    private String content;  // a list of tags based on article type (skirt, blouse, etc.)
    private String notes;

    public int getPatternId() {
        return patternId;
    }
    public void setPatternId(int id) {
        this.patternId = id;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getPatternNumber() {
        return patternNumber;
    }
    public void setPatternNumber(String patternNumber) {
        this.patternNumber = patternNumber;
    }
    public String getSizes() {
        return sizes;
    }
    public void setSizes(String sizes) {
        this.sizes = sizes;
    }
    public String getMinCharacterBasedSize() {
        return minCharacterBasedSize;
    }
    public void setMinCharacterBasedSize(String size) {
        this.minCharacterBasedSize = size;
    }
    public String getMaxCharacterBasedSize() {
        return maxCharacterBasedSize;
    }
    public void setMaxCharacterBasedSize(String size) {
        this.maxCharacterBasedSize = size;
    }
    public int getMinNumericSize() {
        return minNumericSize;
    }
    public void setMinNumericSize(int size) {
        this.minNumericSize = size;
    }
    public int getMaxNumericSize() {
        return maxNumericSize;
    }
    public void setMaxNumericSize(int size) {
        this.maxNumericSize = size;
    }
    public String getFrontPicture() {
        return frontPicture;
    }
    public void setFrontPicture(String path) {
        this.frontPicture = path;
    }
    public String getBackPicture() {
        return backPicture;
    }
    public void setBackPicture(String path) {
        this.backPicture = path;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // helper methods

    public void addNote(String note) {
        this.notes += note;
    }

    private boolean parseNumericSizes(String sizes) {
        /**
         *  Takes a string of the form number, delimiter, number and assigns the first
         *  number as smallestNumericSize, and the last number as largestNumeric size.
         *  Returns true on a successful parse, and false if the string is malformed.
         */

        StringBuilder sbFront = new StringBuilder();
        StringBuilder sbBack = new StringBuilder();
        char[] chars = sizes.toCharArray();

        // iterate from the front, adding chars that are numeric digits to sbFront.  When
        // we get a character that is not a numeric digit, the first number is completely read.
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            // if not a digit, first number is read.  Break.
            if ( ! Character.isDigit(current)) {
                break;
            }
            sbFront.append(current);
        }
        // skip the character(s) in the middle, and continue until the end.
        boolean skip = true;  // logical flag, turns false on first digit read.
        for (int i = sbFront.length(); i < chars.length; i++) {
            char current = chars[i];
            if (Character.isDigit(current)) {
                skip = false;
            }
            if (!skip) {
                sbBack.append(current);
            }
        }

        // Now, we have two StringBuilders, for the first and second part of the input string.
        // If either do not parse to an integer, the String is invalid, and we return false.
        try {
            int min = Integer.parseInt(sbFront.toString());
            int max = Integer.parseInt(sbBack.toString());
            // min should not be larger than max
            if (min <= max) {
                this.minNumericSize = min;
                this.maxNumericSize = max;
                return true;
            }
            return false;
        }
        catch (NumberFormatException e) {
            return false;
        }

    }
}

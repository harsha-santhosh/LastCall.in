package com.inoek.apps.venuser.lastcallin;

/**
 * Created by harshasanthosh on 08/10/17.
 */

public class CContacts {
    private String name;
    private String number;
    private String imageString;
    private String type;

    public CContacts()
    {
        name = null;
        number = null;
        type = null;
        imageString = null;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getImageString() {
        return imageString;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public void setType(String type) {
        this.type = type;
    }

}

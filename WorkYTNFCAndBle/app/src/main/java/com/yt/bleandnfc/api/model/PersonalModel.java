package com.yt.bleandnfc.api.model;

public class PersonalModel {

    public String name;
    public int type; // 1 online 0 offline
    public String time;

    public PersonalModel(String name, int type, String time) {
        this.name = name;
        this.type = type;
        this.time = time;
    }
}

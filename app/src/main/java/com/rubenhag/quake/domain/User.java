package com.rubenhag.quake.domain;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("screen_name")
    private String screenName;

    @SerializedName("name")
    private String name;


    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

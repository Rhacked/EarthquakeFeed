package com.rubenhag.quake.domain;

import com.google.gson.annotations.SerializedName;

public class Tweet {

    @SerializedName("text")
    private String text;

    @SerializedName("user")
    private User user;

    private String featureIdentifier;

    public String getFeatureIdentifier(){
        return featureIdentifier;
    }

    public void setFeatureIdentifier(String featureIdentifier){
        this.featureIdentifier = featureIdentifier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

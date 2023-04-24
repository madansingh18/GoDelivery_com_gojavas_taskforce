package com.gojavas.taskforce.parserbean;

/**
 * Created by Lenovo on 10/22/2016.
 */
public class LoginBean {

    public String getBoyID() {
        return boyID;
    }

    public void setBoyID(String boyID) {
        this.boyID = boyID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBoyName() {
        return boyName;
    }

    public void setBoyName(String boyName) {
        this.boyName = boyName;
    }

    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    private String boyID;
    private String userID;
    private String message;
    private String boyName;
    private String hubName;

}

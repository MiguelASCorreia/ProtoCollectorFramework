package com.example.protocollectorframework.DataModule.Data;

public class HelperData {

    private int position;
    private String title;
    private String message;
    private String extra_json;


    public HelperData(int position, String title, String message){
        this.position = position;
        this.title = title;
        this.message = message;
        this.extra_json = null;
    }

    public HelperData(int position, String title, String message, String extra_json){
        this.position = position;
        this.title = title;
        this.message = message;
        this.extra_json = extra_json;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtra_json() {
        return extra_json;
    }

    public void setExtra_json(String extra_json) {
        this.extra_json = extra_json;
    }
}

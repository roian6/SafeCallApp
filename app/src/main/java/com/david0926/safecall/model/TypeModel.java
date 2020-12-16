package com.david0926.safecall.model;

public class TypeModel {

    private String name, voice, desc;

    public TypeModel(){}

    public TypeModel(String name, String voice, String desc) {
        this.name = name;
        this.voice = voice;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

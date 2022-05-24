package com.example.translater.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "translate")
public class Translate implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id;
    String to;
    String from;
    String lang1;
    String lang2;

    public Translate(String to, String from, String lang1, String lang2) {
        this.to = to;
        this.from = from;
        this.lang1 = lang1;
        this.lang2 = lang2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLang1() {
        return lang1;
    }

    public void setLang1(String lang1) {
        this.lang1 = lang1;
    }

    public String getLang2() {
        return lang2;
    }

    public void setLang2(String lang2) {
        this.lang2 = lang2;
    }
}

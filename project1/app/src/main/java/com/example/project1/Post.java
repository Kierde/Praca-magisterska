package com.example.project1;

public class Post {

    String data;
    String tresc;

    public Post(){
    }

    public Post(String data, String tresc) {
        this.data = data;
        this.tresc = tresc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }
}

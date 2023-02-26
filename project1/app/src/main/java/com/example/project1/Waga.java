package com.example.project1;

public class Waga {

    String data;
    float waga;
    String index;

    public Waga(){
    }


    public Waga(String data, float waga, String index) {
        this.data = data;
        this.waga = waga;
        this.index = index;
    }




    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getWaga() {
        return waga;
    }

    public void setWaga(float waga) {
        this.waga = waga;
    }
}

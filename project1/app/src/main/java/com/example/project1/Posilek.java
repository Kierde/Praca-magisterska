package com.example.project1;

public class Posilek {

    private int kalorycznosc;
    private String nazwaPosilku;
    private String index;
    private float bialko;
    private float weglowodany;
    private float tluszcz;

    public Posilek(){}


    public Posilek(int kalorycznosc, String nazwaPosilku, String index, float bialko, float weglowodany, float tluszcz) {
        this.kalorycznosc = kalorycznosc;
        this.nazwaPosilku = nazwaPosilku;
        this.index = index;
        this.bialko = bialko;
        this.weglowodany = weglowodany;
        this.tluszcz = tluszcz;
    }

    public float getBialko() {
        return bialko;
    }

    public void setBialko(float bialko) {
        this.bialko = bialko;
    }

    public float getWeglowodany() {
        return weglowodany;
    }

    public void setWeglowodany(float weglowodany) {
        this.weglowodany = weglowodany;
    }

    public float getTluszcz() {
        return tluszcz;
    }

    public void setTluszcz(float tluszcz) {
        this.tluszcz = tluszcz;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getKalorycznosc() {
        return kalorycznosc;
    }

    public void setKalorycznosc(int kalorycznosc) {
        this.kalorycznosc = kalorycznosc;
    }

    public String getNazwaPosilku() {
        return nazwaPosilku;
    }

    public void setNazwaPosilku(String nazwaPosilku) {
        this.nazwaPosilku = nazwaPosilku;
    }
}

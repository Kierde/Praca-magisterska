package com.example.project1;

public class Posilek {

    private int kalorycznosc;
    private String nazwaPosilku;
    private String index;


    public Posilek(){
    }

    public Posilek(int kalorycznosc, String nazwaPosilku, String index) {
        this.kalorycznosc = kalorycznosc;
        this.nazwaPosilku = nazwaPosilku;
        this.index = index;
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

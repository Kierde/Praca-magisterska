package com.example.project1;

import java.util.ArrayList;
import java.util.List;

public class Bieg {

    public double dystans;
    public double czas;
    public double sredniaPredkosc;
    public double tempo;
    public double spaloneKalorie;
    private String dataDnia;
    private ArrayList<KilometrBiegu> kilometrBiegus = new ArrayList<>();

    public Bieg(double dystans, double czas, double sredniaPredkosc, double tempo, String dataDnia, int spaloneKalorie) {
        this.dystans = dystans;
        this.czas = czas;
        this.sredniaPredkosc = sredniaPredkosc;
        this.tempo = tempo;
        this.dataDnia = dataDnia;
        this.spaloneKalorie = spaloneKalorie;
    }

    public void obliczSredniaPredkosc(){
        sredniaPredkosc=dystans/czas;
    }

    public void obliczSrednieTempo(){
        tempo=(1/(3600*sredniaPredkosc))*60;
    }

    public double getSpaloneKalorie() {
        return spaloneKalorie;
    }

    public void setSpaloneKalorie(double spaloneKalorie) {
        this.spaloneKalorie = spaloneKalorie;
    }

    public String getDataDnia() {
        return dataDnia;
    }

    public void setDataDnia(String dataDnia) {
        this.dataDnia = dataDnia;
    }

    public double getDystans() {
        return dystans;
    }

    public void setDystans(double dystans) {
        this.dystans = dystans;
    }

    public double getCzas() {
        return czas;
    }

    public void setCzas(double czas) {
        this.czas = czas;
    }

    public double getSredniaPredkosc() {
        return sredniaPredkosc;
    }

    public void setSredniaPredkosc(double sredniaPredkosc) {
        this.sredniaPredkosc = sredniaPredkosc;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public ArrayList<KilometrBiegu> getKilometrBiegus() {
        return kilometrBiegus;
    }

    public void setKilometrBiegus(ArrayList<KilometrBiegu> kilometrBiegus) {
        this.kilometrBiegus = kilometrBiegus;
    }
}

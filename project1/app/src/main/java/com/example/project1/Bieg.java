package com.example.project1;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

public class Bieg {

    public double dystans;
    public double czas;
    public double sredniaPredkosc;
    public double tempo;
    public double spaloneKalorie;
    private String dataDnia;
    private String mapaBiegu;
    private ArrayList<KilometrBiegu> kilometrBiegus = new ArrayList<>();
    private String index;


    public Bieg(){

    }

    public Bieg(double dystans, double czas, double sredniaPredkosc, double tempo, String dataDnia, int spaloneKalorie, String mapaBiegu, String index) {
        this.dystans = dystans;
        this.czas = czas;
        this.sredniaPredkosc = sredniaPredkosc;
        this.tempo = tempo;
        this.dataDnia = dataDnia;
        this.spaloneKalorie = spaloneKalorie;
        this.mapaBiegu = mapaBiegu;
        this.index = index;
    }



    public Bieg(double dystans, double czas, double sredniaPredkosc, double tempo, String dataDnia, int spaloneKalorie) {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getMapaBiegu() {
        return mapaBiegu;
    }

    public void setMapaBiegu(String mapaBiegu) {
        this.mapaBiegu = mapaBiegu;
    }

    public void obliczSredniaPredkosc(){
        sredniaPredkosc=dystans/czas;
    }

    public void obliczSrednieTempo(){
        tempo=(1/(sredniaPredkosc))*0.01666666666666666666666666666667;
        double doubleNumber =tempo;
        int intPart = (int) tempo;
        double doublePart = doubleNumber - intPart;
        tempo = intPart+(doublePart*60*0.01);


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

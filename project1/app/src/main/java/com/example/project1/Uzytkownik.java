package com.example.project1;

import android.net.Uri;

public class Uzytkownik {

    private String nazwaUzytkownika;
    private String adresEmail;
    private String zdjecieProfilowe;
    private int wiek;
    private float waga;
    private float wzrost;
    //true=męska, false=żeńska
    private Boolean plec;
    //1- tryb siedzący/brak, 2- mało aktywny, 3 - aktywny, 4- bardzo aktywny
    private int poziomAktywnosci;
    //2-utrzymanie wagi,1- utracenie wagi,3- przybranie wagi
    private int cel;
    private int wiek_cel;



    public Uzytkownik(){

    }



    public String getZdjecieProfilowe() {
        return zdjecieProfilowe;
    }

    public void setZdjecieProfilowe(String zdjecieProfilowe) {
        this.zdjecieProfilowe = zdjecieProfilowe;
    }

    public Uzytkownik(String nazwaUzytkownika, String adresEmail, String zdjecieProfilowe, int wiek, float waga, float wzrost, Boolean plec, int poziomAktywnosci, int cel, int wiek_cel) {
        this.nazwaUzytkownika = nazwaUzytkownika;
        this.adresEmail = adresEmail;
        this.zdjecieProfilowe = zdjecieProfilowe;
        this.wiek = wiek;
        this.waga = waga;
        this.wzrost = wzrost;
        this.plec = plec;
        this.poziomAktywnosci = poziomAktywnosci;
        this.cel = cel;
        this.wiek_cel = wiek_cel;
    }

    public String getNazwaUzytkownika() {
        return nazwaUzytkownika;
    }

    public void setNazwaUzytkownika(String nazwaUzytkownika) {
        this.nazwaUzytkownika = nazwaUzytkownika;
    }

    public String getAdresEmail() {
        return adresEmail;
    }

    public void setAdresEmail(String adresEmail) {
        this.adresEmail = adresEmail;
    }

    public int getWiek() {
        return wiek;
    }

    public void setWiek(int wiek) {
        this.wiek = wiek;
    }

    public float getWaga() {
        return waga;
    }

    public int getWiek_cel() {
        return wiek_cel;
    }

    public void setWiek_cel(int wiek_cel) {
        this.wiek_cel = wiek_cel;
    }

    public void setWaga(float waga) {
        this.waga = waga;
    }

    public float getWzrost() {
        return wzrost;
    }

    public void setWzrost(float wzrost) {
        this.wzrost = wzrost;
    }

    public Boolean getPlec() {
        return plec;
    }

    public void setPlec(Boolean plec) {
        this.plec = plec;
    }

    public int getPoziomAktywnosci() {
        return poziomAktywnosci;
    }

    public void setPoziomAktywnosci(int poziomAktywnosci) {
        this.poziomAktywnosci = poziomAktywnosci;
    }

    public int getCel() {
        return cel;
    }

    public void setCel(int cel) {
        this.cel = cel;
    }
}

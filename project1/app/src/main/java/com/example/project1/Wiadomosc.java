package com.example.project1;

public class Wiadomosc {


    private String wiadomosc;
    private String typ;
    private long czas;
    private String od;




    public Wiadomosc(){
    }

    public Wiadomosc(String wiadomosc, String typ, String od) {
        this.wiadomosc = wiadomosc;
        this.typ = typ;
        this.od = od;
    }

    public String getOd() {
        return od;
    }

    public void setOd(String od) {
        this.od = od;
    }

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

}

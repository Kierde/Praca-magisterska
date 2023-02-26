package com.example.project1;

public class Komentarz {

    public Komentarz(){
    }

    String trescKomentarza;
    String autor;
    String data;

    public Komentarz(String trescKomentarza, String autor, String data) {
        this.trescKomentarza = trescKomentarza;
        this.autor = autor;
        this.data = data;
    }


    public String getTrescKomentarza() {
        return trescKomentarza;
    }

    public void setTrescKomentarza(String trescKomentarza) {
        this.trescKomentarza = trescKomentarza;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}

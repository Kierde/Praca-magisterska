package com.example.project1;

public class Rozmowa {

    private Long czas;

    public Rozmowa(){
    }


    public Rozmowa(Boolean seen, Long czas) {

        this.czas = czas;
    }

    public Long getCzas() {
        return czas;
    }

    public void setCzas(Long czas) {
        this.czas = czas;
    }
}

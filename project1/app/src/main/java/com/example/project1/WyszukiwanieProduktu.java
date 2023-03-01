package com.example.project1;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Deserialization.Dish;
import Deserialization.Root;


public class WyszukiwanieProduktu extends AppCompatActivity {

    EditText doWyszukania;
    Button szukaj;
    String XRapidAPIKey ="35854fb247mshf655049f443d395p18b398jsn82cdbf49ffbb";
    String XRapidAPIHost = "dietagram.p.rapidapi.com";

    RecyclerView wyszukane;
    private final List<Dish> listaWyszukanychPosilkow = new ArrayList<>();
    private WyszukanyPosilekAdapter wyszukanyPosilekAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyszukiwanie_produktu);

        szukaj = (Button) findViewById(R.id.wyszukajProdukt);
        doWyszukania = (EditText) findViewById(R.id.nazwaSzukanegoProduktu);


        Dish dish1 = new Dish("jabłko", "100", "0.5", "10", "100","3","fok","3333");
        Dish dish2 = new Dish("jagoda", "100", "0.5", "10", "100","3","fok","3333");
        Dish dish3 = new Dish("borówka", "100", "0.5", "10", "100","3","fok","3333");


        listaWyszukanychPosilkow.add(dish1);
        listaWyszukanychPosilkow.add(dish2);
        listaWyszukanychPosilkow.add(dish3);



        wyszukane = (RecyclerView) findViewById(R.id.wyszukaneRecyclerView);
        wyszukanyPosilekAdapter = new WyszukanyPosilekAdapter(listaWyszukanychPosilkow);
        wyszukane.setHasFixedSize(true);
        wyszukane.setLayoutManager(new LinearLayoutManager(this));
        wyszukane.setAdapter(wyszukanyPosilekAdapter);




        wyszukanyPosilekAdapter.notifyDataSetChanged();



        szukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nazwaDoSzukania = processString(doWyszukania.getText().toString());
                String url ="https://"+ XRapidAPIHost+"/apiFood.php?name="+nazwaDoSzukania+"&lang=pl";
                Log.d("my-tag-url", url);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String jsonString = response.toString();
                        Log.d("jsonString", jsonString);
                        Gson gson = new Gson();
                        Root root = gson.fromJson(jsonString, Root.class);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap header = new HashMap();
                        header.put("X-RapidAPI-Key",XRapidAPIKey);
                        header.put("X-RapidAPI-Host",XRapidAPIHost);
                        return header;
                    }
                };


                Volley.newRequestQueue(WyszukiwanieProduktu.this).add(request);
            }
        });


    }

    static String processString(String nazwa) {

        String[] charToCheck = {"ą", "ć", "ę", "ł", "ń", "ó", "ś",
                "ź", "ż", "Ą", "Ć", "Ę", "Ł", "Ń", "Ó", "Ś", "Ź", "Ż"};
        String[] toReplace = {"%C4%85", "%C4%87", "%C4%99",
                "%C5%82", "%C5%84", "%C3%B3",
                "%C5%9B", "%C5%BA", "%C5%BC",
                "%C4%84", "%C4%86", "%C4%98",
                "%C5%81", "%C5%83", "%C3%93",
                "%C5%9A", "%C5%B9", "%C5%BB"
        };
        int i;
        for (i = 0; i < 18; i++) {
            if (nazwa.contains(charToCheck[i])) {
                String temp = nazwa.replace(charToCheck[i], toReplace[i]);
                nazwa = temp;
            }
        }
        return nazwa;
    }



}
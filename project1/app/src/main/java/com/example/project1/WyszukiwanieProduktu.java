package com.example.project1;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
    ImageButton szukaj;
    String XRapidAPIKey ="5778629af2msh7c559938fa40166p1a9b4ejsn2b36f5bc0e26";
    String XRapidAPIHost = "dietagram.p.rapidapi.com";

    RecyclerView wyszukane;
    private final List<Dish> listaWyszukanychPosilkow = new ArrayList<>();
    private WyszukanyPosilekAdapter wyszukanyPosilekAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyszukiwanie_produktu);

        String nazwaPosilku = getIntent().getStringExtra("nazwaPosilku");
        szukaj = (ImageButton) findViewById(R.id.wyszukajProdukt);
        doWyszukania = (EditText) findViewById(R.id.nazwaSzukanegoProduktu);

        wyszukane = (RecyclerView) findViewById(R.id.wyszukaneRecyclerView);
        wyszukanyPosilekAdapter = new WyszukanyPosilekAdapter(listaWyszukanychPosilkow,null,nazwaPosilku);
        Log.d("nazwaPosilku", nazwaPosilku);


        wyszukane.setHasFixedSize(true);
        wyszukane.setLayoutManager(new LinearLayoutManager(this));
        wyszukane.setAdapter(wyszukanyPosilekAdapter);

        //testowanie layout
       // Dish posilek = new Dish("1","Jajko z żurkiem owoc", "130","3","100","200","300","kat");
//        Dish posilek1 = new Dish("1","Jajko z żurkiem owoc fsdfasdfsdsdfafsdfdsfsadsfaddsf", "130","3","10","20","30","kat");
//        Dish posilek3 = new Dish("1","Jajko z żurkiem owoc fsdfasdfsdsdfafsdfdsfsadsfaddsf", "130","3","10.3","20.3","30.3","kat");
//
      //  listaWyszukanychPosilkow.add(posilek);
//        listaWyszukanychPosilkow.add(posilek1);
//        listaWyszukanychPosilkow.add(posilek3);

        wyszukanyPosilekAdapter.notifyDataSetChanged();


        szukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(300);
                szukaj.startAnimation(animation);

                listaWyszukanychPosilkow.clear();
                wyszukanyPosilekAdapter.notifyDataSetChanged();
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

                        for(int i=0;i<root.dishes.size();i++){

                            String temp = root.dishes.get(i).name.toLowerCase();
                            String temp1 = temp.substring(0, 1).toUpperCase() + temp.substring(1);
                            root.dishes.get(i).setName(temp1);

                            if(root.dishes.get(i).fat.equals("000")){
                                root.dishes.get(i).setFat("0");
                            }
                            if(root.dishes.get(i).carbon.equals("000")){
                                root.dishes.get(i).setCarbon("0");
                            }
                            if(root.dishes.get(i).protein.equals("000")){
                                root.dishes.get(i).setProtein("0");
                            }
                            listaWyszukanychPosilkow.add(root.dishes.get(i));
                        }
                            wyszukanyPosilekAdapter.notifyDataSetChanged();
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
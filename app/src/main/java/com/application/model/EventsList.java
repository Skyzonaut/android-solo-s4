package com.application.model;

import android.content.Context;
import android.util.Log;

import com.application.interfaces.InterfaceModel;
import com.application.jeu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class EventsList implements InterfaceModel {

    private final ArrayList<Event> eventsList = new ArrayList<>();

    public EventsList(Context context) {
        InputStream inventoryInputStream = context.getResources().openRawResource(R.raw.cards);
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inventoryInputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String jsonString = writer.toString();

            JSONArray eventJsonArray = new JSONArray(jsonString);
//            Log.d("CARDS", eventJsonArray.toString());

            for (int i = 0; i < eventJsonArray.length(); i++) {
                this.eventsList.add(new Event((JSONObject) eventJsonArray.get(i)));
            }
//            Log.d("CARDS-LIST", eventsList.toString());
            inventoryInputStream.close();

        } catch(Exception e) {
            Log.w("CARDS", e.getMessage());
        }
    }

    @Override
    public void notifierVue() {

    }

    public int length() {
        return this.eventsList.size();
    }

    public Event get(int i) {
        return this.eventsList.get(i);
    }
}

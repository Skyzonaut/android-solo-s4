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
import java.util.Iterator;
import java.util.stream.Stream;

public class Inventory implements InterfaceModel {

    private JSONArray availableStuff;
    private JSONObject inventory;

    public Inventory(Context context) {
        loadInventoryJson(context);
        loadAvailableStuff(context);

    }

    public void loadInventoryJson(Context context) {
        // Inventaire
        InputStream inventoryInputStream = context.getResources().openRawResource(R.raw.inventory);
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inventoryInputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String jsonString = writer.toString();

            this.inventory = new JSONObject(jsonString).getJSONObject("stuff");
//            Log.d("MODELE", inventory.toString());

            inventoryInputStream.close();

        } catch(Exception e) {
            Log.w("MODELE", e.getMessage());
        }
    }

    public void loadAvailableStuff(Context context) {

        // AvailableStuff
        InputStream inventoryInputStream = context.getResources().openRawResource(R.raw.objects);
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inventoryInputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String jsonString = writer.toString();

            this.availableStuff = new JSONObject(jsonString).getJSONArray("stuff");
//            Log.d("MODELE", inventory.toString());

            inventoryInputStream.close();

        } catch(Exception e) {
            Log.w("MODELE", e.getMessage());
        }
    }

    @Override
    public void notifierVue() {
        //
    }

    public JSONObject getSwordObject() {
        JSONObject sword = null;
        try {
            sword = (JSONObject) (this.inventory).getJSONObject("sword");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sword;
    }

    public JSONObject getShieldObject() {
        JSONObject Shield = null;
        try {
            Shield = (JSONObject) (this.inventory).getJSONObject("shield");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Shield;
    }


//    public JSONArray getStuffList() {
//        JSONArray stuffList = new JSONArray();
//        try {
//            stuffList = (JSONArray) (this.inventory).getJSONArray("object");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return stuffList;
//    }

    public JSONObject getItemObject(String searchedItem) {
        JSONObject item = null;
        try {
            item = this.inventory.getJSONObject(searchedItem);
//            Log.d("INIT-INVENT", item.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

     // TODO https://json2csharp.com/code-converters/json-to-pojo
    public void setItemObject(String key, JSONObject jo) {
        setItemObject(key, jo, 1);
    }

    public void setItemObject(String key, int value) {
        setItemObject(key, null, value);
    }


    public void setItemObject(String objectName, JSONObject objectValueInDB, int value){
        try {
            JSONObject initialItem = this.inventory.getJSONObject(objectName);
            switch (objectName) {
                case "pomme":
                case "gold":
                case "clef":
                    this.inventory.getJSONObject(objectName).put("quantity", this.inventory.getJSONObject(objectName).getInt("quantity") + value);
                    break;
                default:
                    for (Iterator<String> it = initialItem.keys(); it.hasNext(); ) {
                        // TODO Tester @Serialize & GSON
                        String keyOfbjectToModify = it.next();
                        this.inventory.getJSONObject(objectName).put(keyOfbjectToModify, objectValueInDB.get(keyOfbjectToModify));
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInventory() {
        return this.inventory;
    }

    public int getLife() {
        int life = 5;
        try {
            life = this.inventory.getInt("life");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return life;
    }

    public void setLife(int newLife) {
        try
        {
            this.inventory.put("life", newLife);
        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
        }
    }

//    public void update

    public JSONObject getItemFromAvailableStuff(String key) {
        try {
            JSONObject obj = null;
//            Log.d("FOUILLER-INVENTORY", this.availableStuff.toString());
            for (int i = 0; i < this.availableStuff.length(); i++) {
//                Log.d("FOUILLER-ITER",  this.availableStuff.get(i).toString() + " " + key);
                if (((JSONObject) this.availableStuff.get(i)).getString("name").equals(key)) {
                    obj = (JSONObject) this.availableStuff.get(i);
                }
            }
//            Log.d("FOUILLER-ITER-END", "END");
//            Log.d("FOUILLER-ITER-END", String.valueOf(obj));
            return obj;
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return this.inventory.toString();
    }
}

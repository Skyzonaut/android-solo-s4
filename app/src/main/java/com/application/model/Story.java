package com.application.model;

import android.content.Context;
import android.util.Log;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Story {


    private HashMap<String, Integer> story = new HashMap<String, Integer>();


    public Story(Context context) {
        InputStream inventoryInputStream = context.getResources().openRawResource(R.raw.story);
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inventoryInputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String jsonString = writer.toString();

            JSONObject storyJsonObject = new JSONObject(jsonString);
//            Log.d("STORY-INIT", storyJsonObject.toString());

            Iterator<String> tmpString = storyJsonObject.keys();
            while (tmpString.hasNext()) {
                String key = tmpString.next();
                this.story.put(key, storyJsonObject.getInt(key));
//                Log.d("STORY", "==> " + key + " : " + this.story.get(key));
            }

            inventoryInputStream.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int getEventCondition(String condition) {
        return this.story.get(condition);
    }

    public void setCondition(String condition, boolean value) {
        this.story.put(condition, value ? 1 : 0);
    }

    public void updateStory(JSONArray optionsToChange) {
        try
        {
//            Log.d("STORY", this.story.toString());

            for (int i = 0; i < optionsToChange.length(); i++)
            {

                String option = (String) optionsToChange.get(i);

                // Si aucune valeur n'est précisé <nomDeLaValeur>:<valeur>
                if (!option.contains(":"))
                {
                    int formerValue = this.story.get(option);
                    int newValue = formerValue == 0 ? 1 : 0;
                    this.story.put(option, newValue);
                }
                else
                {
                    String[] tmp = option.split(":");
                    switch (tmp[1].charAt(0)) {
                        case '+':
                            this.story.put(tmp[0], this.story.get(tmp[0]) + Integer.parseInt(tmp[1].substring(1)));
                            break;
                        case '-':
                            this.story.put(tmp[0], this.story.get(tmp[0]) - Integer.parseInt(tmp[1].substring(1)));
                            break;
                        default:
                            this.story.put(tmp[0], Integer.parseInt(tmp[1]));
                            break;
                    }
                }
            }
//            Log.d("STORY", this.story.toString());
        }
        catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    public String toString() {
        String str = "";
        for (String key : this.story.keySet()) {
            str += key + " : " + this.story.get(key) + "; ";
        }
        return str;
    }
}

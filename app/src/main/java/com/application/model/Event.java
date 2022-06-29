package com.application.model;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Event {

    private JSONObject json;
    private JSONArray options;
    private String imageName;
    private String eventText;
    private JSONObject conditions;
    private String type;

    public Event(JSONObject jo) {
        this.json = jo;
        try {
            this.imageName = jo.getString("img");
            this.eventText = jo.getString("text");
            this.conditions = jo.has("conditions") ? jo.getJSONObject("conditions") : null;
            this.type = jo.has("type") ? jo.getString("type") : null;

//            Log.d("EVENT-INIT-JSON", jo.toString());
//            Log.d("EVENT-INIT-CONDITION", conditions.toString());
            this.options = jo.has("options") ? jo.getJSONArray("options") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJson() {
        return this.json;
    }

    public JSONArray getOptions() {
        return this.options;
    }

    @NonNull
    @Override
    public String toString() {
        return this.json.toString();
    }

    public String getImageName() {
        return this.imageName;
    }

    public String getEventText() {
        return this.eventText;
    }

    public JSONObject getConditions() {
        return this.conditions;
    }

    public int getConditionValue(String condition) {
        int value = 0;
        try {
            value = this.conditions.getInt(condition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public JSONObject getEventConditionByText(String text) {
        for (int i = 0; i < this.options.length(); i++) {
            try {
                if (((JSONObject) this.options.get(i)).getString("text").equals(text)) {
                    return (JSONObject) this.options.get(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getType() { return this.type;}

}

package com.marcouberti.caregivers.webservice.parser;

import com.marcouberti.caregivers.db.entity.CaregiverEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CaregiversServiceParser {
    public static List<CaregiverEntity> parse(String jsonStr) {
        List<CaregiverEntity> list = new ArrayList<>();
        if(jsonStr != null) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray result = json.optJSONArray("results");
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        CaregiverEntity caregiver = new CaregiverEntity();
                        JSONObject itemJSON = result.getJSONObject(i);
                        caregiver.setId(itemJSON.getJSONObject("login").getString("uuid"));
                        caregiver.setName(itemJSON.getJSONObject("name").getString("first"));
                        caregiver.setSurname(itemJSON.getJSONObject("name").getString("last"));
                        caregiver.setPhoto(itemJSON.getJSONObject("picture").getString("large"));
                        caregiver.setThumbnail(itemJSON.getJSONObject("picture").getString("thumbnail"));
                        list.add(caregiver);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

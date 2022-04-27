package com.example.fantasyf1;

import org.json.JSONObject;

public interface APICallback {
    void onFinish(JSONObject response, FantasyManager.ResponseType respType, int statusCode);
}

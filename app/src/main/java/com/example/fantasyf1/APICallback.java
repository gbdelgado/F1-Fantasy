/**
 * APICallback.java
 * This is an interface for all of the API request callbacks. This should be implemented
 * to any class that will use FantasyManager
 */
package com.example.fantasyf1;

import org.json.JSONObject;

public interface APICallback {
    /**
     * Invoked upon a completion of an API Request. Provided is the JSONResponse of that call
     * along with the responsetype and the status code of that response.
     *
     * @param response {JSONObject} - the API response
     * @param respType {ResposneType} - the response type
     * @param statusCode {int} - the statuscode
     */
    void onFinish(JSONObject response, FantasyManager.ResponseType respType, int statusCode);
}

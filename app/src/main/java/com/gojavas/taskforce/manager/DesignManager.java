package com.gojavas.taskforce.manager;

import com.gojavas.taskforce.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GJS280 on 27/4/2015.
 */
public class DesignManager {

    private static DesignManager instance;
    private static JSONObject designJson;

    public DesignManager() {
        try {
            DesignManager.designJson = new JSONObject(Utility.getDesign());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static DesignManager getInstance() {
        if(instance == null) {
            instance = new DesignManager();
        }
        return instance;
    }

    public JSONObject getDesignJson() {
        return designJson;
    }
}

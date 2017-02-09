package rocks.athrow.android_stock_rotation.data;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ParseJSON
 * Created by joselopez on 2/9/17.
 */

public class ParseJSON {
    private static final String DATA = "data";

    public static JSONArray getJSONArray(String JSON) {
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            if (jsonObject.has(DATA)) {
                jsonArray = jsonObject.getJSONArray(DATA);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}

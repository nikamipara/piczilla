package world.ignite.piczilla.nodes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    public static List<ImageNode> parseFeed(String content) {
        try {
            JSONObject mainobj = new JSONObject(content);
            JSONObject secondObj = mainobj.getJSONObject("responseData");
            JSONArray ar = secondObj.getJSONArray("results");
            List<ImageNode> tempList = new ArrayList<>();
            for (int i = 0; i < ar.length(); i++) {
                JSONObject obj = ar.getJSONObject(i);
                ImageNode tempItem = new ImageNode();
                tempItem.setImageURL(obj.getString("unescapedUrl"));
                tempList.add(tempItem);
            }
            return tempList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}


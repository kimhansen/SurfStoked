package com.webresponsive.surfapps.surfstoked.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PictureVO
{
    private long id;
    private String pictureURL;
    private byte[] pictureData;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getPictureURL()
    {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL)
    {
        this.pictureURL = pictureURL;
    }

    public byte[] getPictureData()
    {
        return pictureData;
    }

    public void setPictureData(byte[] pictureData)
    {
        this.pictureData = pictureData;
    }

    public PictureVO()
    {
    }

    public PictureVO(JSONObject obj) throws JSONException
    {
        deserializeFromObj(obj);
    }

    public PictureVO(String serializedObj) throws JSONException
    {
        deserialize(serializedObj);
    }

    public void deserialize(String serializedObj) throws JSONException
    {
        JSONObject obj = new JSONObject(serializedObj);
        deserializeFromObj(obj);
    }

    public void deserializeFromObj(JSONObject obj) throws JSONException
    {
        this.id = obj.getLong("id");
        this.pictureURL = obj.getString("picture_url");
//        this.pictureString = obj.getString("picture_string");
    }

    public String serialize() throws JSONException
    {
        return serializeToObj().toString();
    }

    public JSONObject serializeToObj() throws JSONException
    {
        JSONObject serializedObj = new JSONObject();

        serializedObj.put("id", this.id);
        serializedObj.put("picture_url", this.pictureURL);
//        serializedObj.put("picture_string", this.pictureString);

        return serializedObj;
    }

    /**
     * Serialize an array into a JSON serialized string.
     */
    public static String serializeArray(ArrayList<PictureVO> pictures)
    {
        JSONArray jsArray = new JSONArray(pictures);
        
        return jsArray.toString();
    }
    
    /**
     * Deserialize a JSON string into an object array.
     */
    public static List<PictureVO> deserializeArray(JSONArray jsonObjs) throws JSONException
    {
//        JSONObject jsonObj = new JSONObject(serializedArray);
//        JSONArray jsonObjs = jsonObj.getJSONArray("pictures");
        List<PictureVO> pictures = new ArrayList<PictureVO>();
        for (int i = 0; i < jsonObjs.length(); i++)
        {
            JSONObject picture = jsonObjs.getJSONObject(i);
            pictures.add(new PictureVO(picture));
        }

        return pictures;
    }
}
